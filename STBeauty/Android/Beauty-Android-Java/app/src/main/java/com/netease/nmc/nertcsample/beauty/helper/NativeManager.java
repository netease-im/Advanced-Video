package com.netease.nmc.nertcsample.beauty.helper;

import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.application.NEApplication;
import com.netease.nmc.nertcsample.beauty.entity.GenderEnum;
import com.netease.nmc.nertcsample.beauty.utils.Constants;
import com.netease.nmc.nertcsample.beauty.utils.ContextHolder;
import com.netease.nmc.nertcsample.beauty.utils.FileUtils;
import com.netease.nmc.nertcsample.beauty.utils.ThreadUtils;
import com.sensetime.stmobile.STCommonNative;
import com.sensetime.stmobile.STMobileAnimalNative;
import com.sensetime.stmobile.STMobileEffectNative;
import com.sensetime.stmobile.STMobileEffectParams;
import com.sensetime.stmobile.STMobileFaceAttributeNative;
import com.sensetime.stmobile.STMobileFaceVerifyNative;
import com.sensetime.stmobile.STMobileHumanActionNative;
import com.sensetime.stmobile.STMobileObjectTrackNative;
import com.sensetime.stmobile.model.STFaceAttribute;
import com.sensetime.stmobile.model.STFaceMeshList;
import com.sensetime.stmobile.model.STHumanAction;
import com.sensetime.stmobile.model.STImage;
import com.sensetime.stmobile.model.STMobile106;
import com.sensetime.stmobile.params.STHumanActionParamsType;
import com.sensetime.stmobile.params.STResultCode;

/**
 * @Description 初始化非OpengGL相关的句柄，包括人脸检测及人脸属性
 * @Time 2021/10/19 11:38 上午
 */
public class NativeManager implements NativeManagerI {

    private static final String TAG = "NativeManager";

    private final static boolean TOAST_ERROR = true;
    private final AssetManager mAsset;

    public static final int CLIENT0 = 0;
//    public static final int CLIENT1 = 1;
//    public static final int CLIENT2 = 2;// tryOn预览->图片
    private static volatile NativeManager instance0;
    private static volatile NativeManager instance1;
    private static volatile NativeManager instance2;
    public boolean verifyHandleSuccess = false;

    private final STMobileAnimalNative mAnimalNative = new STMobileAnimalNative();
    private final STMobileEffectNative stMobileEffectNative = new STMobileEffectNative();
    private final STMobileFaceVerifyNative stMobileFaceVerifyNative = new STMobileFaceVerifyNative();
    private final STMobileFaceAttributeNative mSTFaceAttributeNative = new STMobileFaceAttributeNative();
    private final STMobileObjectTrackNative mSTMobileObjectTrackNative = new STMobileObjectTrackNative();

    private STFaceMeshList faceMeshList;
    private boolean hasSkinCapability = false;
    public boolean addAllSubModelFinish = false;

    private NativeManager() {
        mAsset = ContextHolder.getContext().getAssets();
    }

    public static NativeManager getInstance() {
//        IAtyState state = AtyStateContext.getInstance().getState();
//        if (state instanceof TryOnImgAtyState || state instanceof TryOnVideoAtyState) {// 图片版 预览版 倆effect
//            return getInstance(CLIENT2);
//        }
        return getInstance(CLIENT0);
    }

    public static NativeManager getInstance(int client) {
        switch (client) {
            case CLIENT0:
                if (instance0 == null) {
                    synchronized (NativeManager.class) {
                        instance0 = new NativeManager();
                    }
                }
                return instance0;
//            case CLIENT1:
//                if (instance1 == null) {
//                    synchronized (NativeManager.class) {
//                        instance1 = new NativeManager();
//                    }
//                }
//                return instance1;
//            case CLIENT2:
//                if (instance2 == null) {
//                    synchronized (NativeManager.class) {
//                        instance2 = new NativeManager();
//                    }
//                }
//                return instance2;
            default:
                throw new IllegalArgumentException("Unknown client " + client);
        }
    }

    @Override
    public void createAnimalHandle(int config) {
        Log.i(TAG, "createAnimalHandle() called with: config = [" + Integer.toHexString(config) + "]");
        int ret = mAnimalNative.createInstance(FileUtils.getFilePath(ContextHolder.getContext(), Constants.MODEL_CAT_FACE), config);
        checkAddModelRet(Constants.MODEL_CAT_FACE, ret);

        ret = mAnimalNative.addSubModelFromAssetFile(Constants.MODEL_DOG_FACE, ContextHolder.getContext().getAssets());
        checkAddModelRet(Constants.MODEL_DOG_FACE, ret);
    }

    @Override
    public void destroyAnimalHandle() {
        mAnimalNative.destroyInstance();
        Log.i(TAG, "destroyAnimalHandle() called");
    }

    public void createVerifyHandle() {
        // 鉴权成功去创建 verifyHandle
        if (ContextHolder.isCheckLicenseSuccess()) {
            int ret = stMobileFaceVerifyNative.createInstanceFromAssetFile(Constants.MODEL_VERIFY, mAsset);
            checkAddModelRet(Constants.MODEL_VERIFY, ret);
            verifyHandleSuccess = true;
        }
    }

    @Override
    public void destroyVerifyHandle() {
        stMobileFaceVerifyNative.destroyInstance();
        Log.i(TAG, "destroyVerifyHandle() called");
        verifyHandleSuccess = false;
    }

    // STMobileEffectNative.EFFECT_CONFIG_NONE
    @Override
    public void createEffectNative(int config) {
        int ret = stMobileEffectNative.createInstance(ContextHolder.getContext(), config);
        stMobileEffectNative.setParam(STMobileEffectParams.EFFECT_PARAM_QUATERNION_SMOOTH_FRAME, 5);
        Log.i(TAG, "createInstance effectNative result" + ret + ", config:" + config);

        stMobileEffectNative.setListener(new STMobileEffectNative.Listener() {
            @Override
            public void packageStateChange(int packageState, int packageId) {
                Log.i(TAG, "stMobileEffectNative packageStateChange packageState : " + packageState);
            }

            @Override
            public void greenSegment(int color) {
                Log.i(TAG, "greenSegment color:" + color);
                mHumanActionNative.setParam((int) STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_GREEN_SEGMENT_COLOR, color);
            }
        });
    }

    @Override
    public void destroyEffectNative() {
        stMobileEffectNative.destroyInstance();
    }

    @Override
    public void createObjectTrackHandle() {
        mSTMobileObjectTrackNative.createInstance();
        Log.i(TAG, "createObjectTrackHandle() called");
    }

    @Override
    public void destroyObjectTrackHandle() {
        mSTMobileObjectTrackNative.destroyInstance();
        Log.i(TAG, "destroyObjectTrackHandle() called");
    }

    @Override
    public void createFaceAttributeHandle() {
        int ret = mSTFaceAttributeNative.createInstanceFromAssetFile(Constants.MODEL_NAME_FACE_ATTRIBUTE, mAsset);
        checkAddModelRet(Constants.MODEL_NAME_FACE_ATTRIBUTE, ret);
    }

    @Override
    public void destroyFaceAttributeHandle() {
        Log.i(TAG,"destroyFaceAttributeHandle() called");
        mSTFaceAttributeNative.destroyInstance();
    }

    @Override
    public STMobileAnimalNative getAnimalNative() {
        return mAnimalNative;
    }

    @Override
    public STMobileObjectTrackNative getObjectTrackNative() {
        return mSTMobileObjectTrackNative;
    }

    @Override
    public STMobileFaceVerifyNative getFaceVerifyNative() {
        return stMobileFaceVerifyNative;
    }

    @Override
    public STMobileFaceAttributeNative getFaceAttributeNative() {
        return mSTFaceAttributeNative;
    }

    @Override
    public STMobileEffectNative getEffectNative() {
        return stMobileEffectNative;
    }

    @Override
    public STMobileHumanActionNative getHumanActionNative() {
        return mHumanActionNative;
    }

    public synchronized void initHumanAction(STMobileHumanActionNative humanActionNative, int config) {
        stringBuilder.setLength(0);
        mHumanActionNative = humanActionNative;
        //从asset资源文件夹读取model到内存，再使用底层st_mobile_human_action_create_from_buffer接口创建handle
        int result = humanActionNative.createInstanceFromAssetFile(Constants.MODEL_106, config, ContextHolder.getContext().getAssets());
        checkAddModelRet(Constants.MODEL_106, result);

        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_HAND);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_SEGMENT);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_FACE_EXTRA);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_SEGMENT_HAIR);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_LIPS_PARSING);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_FACE_OCCLUSION);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_SEGMENT_SKY);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_AVATAR_HELP);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_FOOT);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_PANT);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_SEGMENT_SKIN);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_3DMESH);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_WRIST);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_CLOTH);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_HEAD_INSTANCE);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_360HEAD_INSTANCE);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_HEAD_P_INSTANCE);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_HEAD_P_EAR);
        addSubModelFromAssetFile(humanActionNative, Constants.MODEL_NAIL);

        // 背景分割羽化程度[0,1](默认值0.35),0 完全不羽化,1羽化程度最高,在strenth较小时,羽化程度基本不变.值越大,前景与背景之间的过度边缘部分越宽.
        humanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_BACKGROUND_BLUR_STRENGTH, 0.35f);
        // 设置face mesh结果输出坐标系,(0: 屏幕坐标系， 1：3d世界坐标系， 2:3d摄像机坐标系,是摄像头透视投影坐标系, 原点在摄像机 默认是0）
        humanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_FACE_MESH_OUTPUT_FORMAT, 1.0f);
        // 设置mesh渲染模式
        humanActionNative.setParam(STHumanActionParamsType.ST_HUMAN_ACTION_PARAM_MESH_MODE, STCommonNative.MESH_CONFIG);

        this.faceMeshList = humanActionNative.getMeshList(STHumanActionParamsType.STMeshType.ST_MOBILE_FACE_MESH);
        addAllSubModelFinish = true;
        //if (listener != null) listener.onFaceMeshList(faceMeshList);
        if (mListener !=null ) mListener.onStatusChanged(STATUS_ADD_MESH_DONE);

        if (TOAST_ERROR && stringBuilder.length() > 0) {
            if (ThreadUtils.getInstance().isMainThread()) {
                Toast.makeText(ContextHolder.getContext(), stringBuilder, Toast.LENGTH_LONG).show();
            } else {
                ThreadUtils.getInstance().runOnUIThread(() -> Toast.makeText(ContextHolder.getContext(), stringBuilder, Toast.LENGTH_LONG).show());
            }
        }

        createVerifyHandle();
    }

    private void addSubModelFromAssetFile(STMobileHumanActionNative humanActionNative, String assetPath) {
        int ret = humanActionNative.addSubModelFromAssetFile(assetPath, ContextHolder.getContext().getAssets());
        // 是否有皮肤分割能力，美白3的逻辑处理
        if (assetPath.equals(Constants.MODEL_SEGMENT_SKIN)) {
            setHasSkinCapability(ret != STResultCode.ST_E_NO_CAPABILITY.getResultCode());
        }
        checkAddModelRet(assetPath, ret);
    }

    private final StringBuilder stringBuilder = new StringBuilder();

    private void checkAddModelRet(String path, int errorCode) {
        if (errorCode != 0) {
            // 模型加载失败
            Log.e(TAG, String.format(ContextHolder.getContext().getString(R.string.log_add_model), path, errorCode));
            //noinspection StringConcatenationInsideStringBufferAppend
            stringBuilder.append("models:" + path + ", ret:" + errorCode + "\n");
        } else {
            Log.i(TAG, String.format(ContextHolder.getContext().getString(R.string.log_add_model), path, errorCode));
        }
    }

    public STFaceMeshList getFaceMeshList() {
        return faceMeshList;
    }

    private STMobileHumanActionNative mHumanActionNative;

    /**
     * 性别检测
     */
    public synchronized GenderEnum detect(STImage image) {
        if (null == image || image.getImageData() == null || image.getImageData().length == 0)
            return GenderEnum.UNKNOWN;
        mHumanActionNative.nativeHumanActionDetectPtr(image.getImageData(), image.getPixelFormat(), 1, 0, image.getWidth(), image.getHeight());
        mHumanActionNative.nativeHumanActionPtrCopy();
        STHumanAction humanAction = mHumanActionNative.getNativeHumanAction();
        if (humanAction != null) {
            Log.i(TAG, "face count:" + humanAction.faceCount);
        }
        STMobile106[] arrayFaces = humanAction.getMobileFaces();
        if (null == arrayFaces || arrayFaces.length == 0) return GenderEnum.UNKNOWN;
        STFaceAttribute[] arrayFaceAttribute = new STFaceAttribute[arrayFaces.length];
        long attributeCostTime = System.currentTimeMillis();
        //int result = mSTFaceAttributeNative.detect(data, imageFormat, mImageHeight, mImageWidth, arrayFaces, arrayFaceAttribute);
        int result = mSTFaceAttributeNative.detect(image.getImageData(), image.getPixelFormat(), image.getWidth(), image.getHeight(), arrayFaces, arrayFaceAttribute);
        Log.i(TAG, "attribute cost time: " + (System.currentTimeMillis() - attributeCostTime));
        if (result != 0) return GenderEnum.UNKNOWN;
        if (arrayFaceAttribute[0].getAttributeCount() > 0) {
            STFaceAttribute stFaceAttribute = arrayFaceAttribute[0];
            for (int i = 0; i < stFaceAttribute.arrayAttribute.length; i++) {
                STFaceAttribute.Attribute attribute = stFaceAttribute.arrayAttribute[i];
                String category = attribute.getCategory();
                // female:女  male:男
                if (category.equals("gender")) {
                    if (attribute.getLabel().equals("male")) {
                        Log.i(TAG, "gender label: 男");
                        return GenderEnum.MAN;
                    }
                    if (attribute.getLabel().equals("female")) {
                        Log.i(TAG, "gender label: 女");
                        return GenderEnum.WOMAN;
                    }
                }
            }
        }
        return GenderEnum.UNKNOWN;
    }

    public synchronized GenderEnum detect2(STImage image, STHumanAction humanAction) {
        if (null == image || image.getImageData() == null || image.getImageData().length == 0)
            return GenderEnum.UNKNOWN;
        //mHumanActionNative.nativeHumanActionDetectPtr(image.getImageData(), image.getPixelFormat(), 1, 0,image.getWidth(), image.getHeight());
        //mHumanActionNative.nativeHumanActionPtrCopy();
        //STHumanAction humanAction = mHumanActionNative.getNativeHumanAction();
        if (humanAction != null) {
            Log.i(TAG, "face count:" + humanAction.faceCount);
        }
        STMobile106[] arrayFaces = humanAction.getMobileFaces();
        if (null == arrayFaces || arrayFaces.length == 0) return GenderEnum.UNKNOWN;
        STFaceAttribute[] arrayFaceAttribute = new STFaceAttribute[arrayFaces.length];
        long attributeCostTime = System.currentTimeMillis();
        //int result = mSTFaceAttributeNative.detect(data, imageFormat, mImageHeight, mImageWidth, arrayFaces, arrayFaceAttribute);
        int result = mSTFaceAttributeNative.detect(image.getImageData(), image.getPixelFormat(), image.getWidth(), image.getHeight(), arrayFaces, arrayFaceAttribute);
        Log.i(TAG, "attribute cost time: " + (System.currentTimeMillis() - attributeCostTime));
        if (result != 0) return GenderEnum.UNKNOWN;
        if (arrayFaceAttribute[0].getAttributeCount() > 0) {
            STFaceAttribute stFaceAttribute = arrayFaceAttribute[0];
            for (int i = 0; i < stFaceAttribute.arrayAttribute.length; i++) {
                STFaceAttribute.Attribute attribute = stFaceAttribute.arrayAttribute[i];
                String category = attribute.getCategory();
                // female:女  male:男
                if (category.equals("gender")) {
                    if (attribute.getLabel().equals("male")) {
                        Log.i(TAG, "gender label: 男");
                        return GenderEnum.MAN;
                    }
                    if (attribute.getLabel().equals("female")) {
                        Log.i(TAG, "gender label: 女");
                        return GenderEnum.WOMAN;
                    }
                }
            }
        }
        return GenderEnum.UNKNOWN;
    }

    public boolean isHasSkinCapability() {
        return hasSkinCapability;
    }

    public void setHasSkinCapability(boolean hasSkinCapability) {
        this.hasSkinCapability = hasSkinCapability;
    }

    public static final int STATUS_ADD_MESH_DONE = 0;// 加载完Mesh模型
    private Listener mListener;

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        void onStatusChanged(int status);
    }
}
