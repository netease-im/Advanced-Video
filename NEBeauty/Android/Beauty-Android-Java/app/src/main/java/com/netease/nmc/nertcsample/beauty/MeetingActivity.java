package com.netease.nmc.nertcsample.beauty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;
import com.netease.lava.nertc.sdk.NERtcCallback;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.NERtcOption;
import com.netease.lava.nertc.sdk.NERtcParameters;
import com.netease.lava.nertc.sdk.video.NERtcRemoteVideoStreamType;
import com.netease.lava.nertc.sdk.video.NERtcVideoConfig;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;
import com.netease.nertcbeautysample.BuildConfig;
import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.module.NEAssetsEnum;
import com.netease.nmc.nertcsample.beauty.module.NEEffect;
import com.netease.nmc.nertcsample.beauty.module.NEEffectEnum;
import com.netease.nmc.nertcsample.beauty.module.NEFilter;
import com.netease.nmc.nertcsample.beauty.module.NEFilterEnum;
import com.netease.nmc.nertcsample.beauty.ui.NEBeautyRadioGroup;
import com.netease.nmc.nertcsample.beauty.ui.WrapContentViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

//  Created by NetEase on 7/31/20.
//  Copyright (c) 2014-2020 NetEase, Inc. All rights reserved.
//
public class MeetingActivity extends AppCompatActivity implements NERtcCallback, View.OnClickListener {

    private static final String TAG = "MeetingActivity";
    private static final String EXTRA_ROOM_ID = "extra_room_id";

    private boolean enableLocalVideo = true;
    private boolean enableLocalAudio = true;
    private boolean joinedChannel = false;

    private NERtcVideoView localUserVv;
    private NERtcVideoView remoteUserVv;
    //private TextView waitHintTv;
    private ImageButton enableAudioIb;
    private ImageButton leaveIb;
    private ImageButton enableVideoIb;
    private ImageView cameraFlipImg;
    //private View remoteUserBgV;
    private ImageView beautyConstrastImg;

    private String[] tabTags;
    private TabLayout tabLayout;
    private WrapContentViewPager viewPager;
    private Slider filterLevelSlider;
    private NEBeautyRadioGroup filterRadioGroup;
    private int currentFilterStyle;
    private NEBeautyRadioGroup effectRadioGroup;
    private Slider effectLevelSlider;
    private String extFilesDirPath;
    private int currentEffect;
    private List<View> tabViews;
    private HashMap<Integer, NEFilter> filters;
    private HashMap<Integer, NEEffect> effects;

    public static void startActivity(Activity from, String roomId) {
        Intent intent = new Intent(from, MeetingActivity.class);
        intent.putExtra(EXTRA_ROOM_ID, roomId);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_meeting);

        initData();
        initViews();
        setupNERtc();
        String roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        long userId = generateRandomUserID();
        joinChannel(userId, roomId);
    }

    /**
     * 加入房间
     *
     * @param userID 用户ID
     * @param roomID 房间ID
     */
    private void joinChannel(long userID, String roomID) {
        Log.i(TAG, "joinChannel userId: " + userID);
        NERtcEx.getInstance().joinChannel(null, roomID, userID);
        localUserVv.setZOrderMediaOverlay(true);
        localUserVv.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FILL);
        NERtcEx.getInstance().setupLocalVideoCanvas(localUserVv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NERtcEx.getInstance().stopBeauty();
        NERtcEx.getInstance().release();
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void initViews() {
        localUserVv = findViewById(R.id.vv_local_user);
        remoteUserVv = findViewById(R.id.vv_remote_user);
        remoteUserVv.setVisibility(View.INVISIBLE);
        //waitHintTv = findViewById(R.id.tv_wait_hint);
        enableAudioIb = findViewById(R.id.ib_audio);
        leaveIb = findViewById(R.id.ib_leave);
        enableVideoIb = findViewById(R.id.ib_video);
        cameraFlipImg = findViewById(R.id.img_camera_flip);
        //remoteUserBgV = findViewById(R.id.v_remote_user_bg);
        //remoteUserBgV.setVisibility(View.INVISIBLE);

        localUserVv.setVisibility(View.INVISIBLE);
        enableAudioIb.setOnClickListener(this);
        leaveIb.setOnClickListener(this);
        enableVideoIb.setOnClickListener(this);
        cameraFlipImg.setOnClickListener(this);

        beautyConstrastImg = findViewById(R.id.img_beauty_constrast);
        beautyConstrastImg.setOnTouchListener((view, motionEvent) -> {
            int action = motionEvent.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                view.setAlpha(0.75f);
                NERtcEx.getInstance().enableBeauty(false);
            } else if (action == MotionEvent.ACTION_UP) {
                view.setAlpha(1.0f);
                NERtcEx.getInstance().enableBeauty(true);
            }
            return true;
        });
        tabLayout = findViewById(R.id.tab_bottom);
        viewPager = findViewById(R.id.vp_pager);
        tabViews = new ArrayList<>();
        tabTags = getResources().getStringArray(R.array.beauty_option_tags);

        View filterTab = getLayoutInflater().inflate(R.layout.tab_filter, null);
        tabViews.add(filterTab);

        View beautyTab = getLayoutInflater().inflate(R.layout.tab_effect, null);
        tabViews.add(beautyTab);

        for(String tag : tabTags) {
            tabLayout.addTab(tabLayout.newTab().setText(tag));
        }

        filterLevelSlider = filterTab.findViewById(R.id.slider_filter_level);
        filterRadioGroup = filterTab.findViewById(R.id.radio_group_filter);
        filterRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            currentFilterStyle = checkedId;
            NEFilter filter = filters.get(currentFilterStyle);
            if (filter != null && filter.getResId() != R.id.rb_filter_origin) {
                float level = filter.getLevel();
                filterLevelSlider.setValue(level * 100);
                NERtcEx.getInstance().addBeautyFilter(getBeautyAssetPath(NEAssetsEnum.FILTERS, filter.getName()));
                NERtcEx.getInstance().setBeautyFilterLevel(level);
            } else {
                filterLevelSlider.setValue(0);
                NERtcEx.getInstance().removeBeautyFilter();
            }
        });
        filterLevelSlider.addOnChangeListener((slider, value, fromUser) -> {
            NEFilter filter = filters.get(currentFilterStyle);
            if (filter != null && filter.getResId() != R.id.rb_filter_origin) {
                float level = value / 100;
                filter.setLevel(level);
                NERtcEx.getInstance().setBeautyFilterLevel(level);
            }
        });

        effectLevelSlider = beautyTab.findViewById(R.id.slider_effect_level);
        effectRadioGroup = beautyTab.findViewById(R.id.radio_group_effect);
        effectRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            currentEffect = checkedId;
            NEEffect effect = effects.get(checkedId);
            if (effect != null) {
                float level = effect.getLevel();
                effectLevelSlider.setValue(level * 100);
                NERtcEx.getInstance().setBeautyEffect(effect.getType(), level);
            }

            if (checkedId == R.id.rb_effect_recover) {
                effectLevelSlider.setValue(0);
                resetEffect();
            }
        });
        effectLevelSlider.addOnChangeListener((slider, value, fromUser) -> {
            NEEffect effect = effects.get(currentEffect);
            if (effect != null) {
                float level = value / 100;
                effect.setLevel(level);
                NERtcEx.getInstance().setBeautyEffect(effect.getType(), level);
            }
        });

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return tabViews.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = tabViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTags[position];
            }
        });
    }

    private void initData() {
        extFilesDirPath = getExternalFilesDir(null).getAbsolutePath();
        filters = NEFilterEnum.getFilters();
        effects = NEEffectEnum.getEffects();
    }


    /**
     * 初始化SDK
     */
    private void setupNERtc() {
        NERtcParameters parameters = new NERtcParameters();
        NERtcEx.getInstance().setParameters(parameters); //先设置参数，后初始化

        NERtcOption options = new NERtcOption();
        if (BuildConfig.DEBUG) {
            options.logLevel = NERtcConstants.LogLevel.INFO;
        } else {
            options.logLevel = NERtcConstants.LogLevel.WARNING;
        }

        try {
            NERtcEx.getInstance().init(getApplicationContext(), getString(R.string.app_key), this, options);
        } catch (Exception e) {
            // 可能由于没有release导致初始化失败，release后再试一次
            NERtcEx.getInstance().release();
            try {
                NERtcEx.getInstance().init(getApplicationContext(), getString(R.string.app_key), this, options);
            } catch (Exception ex) {
                Toast.makeText(this, "SDK初始化失败", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        NERtcVideoConfig videoConfig = new NERtcVideoConfig();
        videoConfig.width = 360;
        videoConfig.height = 640;
        videoConfig.frameRate = NERtcVideoConfig.NERtcVideoFrameRate.FRAME_RATE_FPS_15;
        NERtcEx.getInstance().setLocalVideoConfig(videoConfig);

        setLocalAudioEnable(true);
        setLocalVideoEnable(true);
        NERtcEx.getInstance().startBeauty();
        resetBeauty();
    }

    /**
     * 设置美颜特效默认参数，设置默认滤镜为白皙
     */
    private void resetBeauty() {
        resetEffect();
        //NERtcEx.getInstance().addBeautyFilter(getBeautyAssetPath(NEAssetsEnum.FILTERS, NEFilterEnum.FAIR.getName()));
        //NERtcEx.getInstance().setBeautyFilterLevel(NEFilterEnum.FAIR.getLevel());
    }

    /**
     * 设置美颜默认参数
     */
    private void resetEffect() {
        effects = NEEffectEnum.getEffects();
        for (NEEffect effect : effects.values()) {
            NERtcEx.getInstance().setBeautyEffect(effect.getType(), effect.getLevel());
        }
    }

    /**
     * 生成滤镜和美妆模板资源文件的路径，资源文件在App启动后会拷贝到的App的外部存储路径
     * @param type @see NEAssetsEnum
     * @param name 滤镜或者美妆的名称，对应assets下的资源文件名
     * @return 滤镜或者美妆的App外部存储路径
     */
    private String getBeautyAssetPath(NEAssetsEnum type, String name) {
        String separator = File.separator;
        return String.format(Locale.getDefault(), "%s%s%s%s%s", extFilesDirPath, separator, type.getAssetsPath(), separator, name);
    }

    /**
     * 随机生成用户ID
     *
     * @return 用户ID
     */
    private int generateRandomUserID() {
        return new Random().nextInt(100000);
    }

    /**
     * 退出房间
     *
     * @return 返回码
     * @see com.netease.lava.nertc.sdk.NERtcConstants.ErrorCode
     */
    private boolean leaveChannel() {
        joinedChannel = false;
        setLocalAudioEnable(false);
        setLocalVideoEnable(false);
        int ret = NERtcEx.getInstance().leaveChannel();
        return ret == NERtcConstants.ErrorCode.OK;
    }

    /**
     * 退出房间并关闭页面
     */
    private void exit() {
        if (joinedChannel) {
            leaveChannel();
        }
        finish();
    }

    @Override
    public void onJoinChannel(int result, long channelId, long elapsed, long uid) {
        Log.i(TAG, "onJoinChannel result: " + result + " channelId: " + channelId + " elapsed: " + elapsed);
        if (result == NERtcConstants.ErrorCode.OK) {
            joinedChannel = true;
            // 加入房间，准备展示己方视频
            localUserVv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLeaveChannel(int result) {
        Log.i(TAG, "onLeaveChannel result: " + result);
    }

    @Override
    public void onUserJoined(long uid) {
        Log.i(TAG, "onUserJoined uid: " + uid);
        // 已经有订阅，就不要变了
        if (remoteUserVv.getTag() != null) {
            return;
        }
        // 有用户加入，设置Tag，该用户离开前，只订阅和取消订阅此用户
        remoteUserVv.setTag(uid);
        // 不用等待了
        //waitHintTv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUserLeave(long uid, int reason) {
        Log.i(TAG, "onUserLeave uid: " + uid + " reason: " + reason);
        // 退出的不是当前订阅的对象，则不作处理
        if (!isCurrentUser(uid)) {
            return;
        }
        // 设置TAG为null，代表当前没有订阅
        remoteUserVv.setTag(null);
        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, false);

        // 显示在等待用户进入房间
        //waitHintTv.setVisibility(View.VISIBLE);
        // 不展示远端
        remoteUserVv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUserAudioStart(long uid) {
        Log.i(TAG, "onUserAudioStart uid: " + uid);
    }

    @Override
    public void onUserAudioStop(long uid) {
        Log.i(TAG, "onUserAudioStop, uid=" + uid);
    }

    @Override
    public void onUserVideoStart(long uid, int profile) {
        Log.i(TAG, "onUserVideoStart uid: " + uid + " profile: " + profile);
        if (!isCurrentUser(uid)) {
            return;
        }

        NERtcEx.getInstance().subscribeRemoteVideoStream(uid, NERtcRemoteVideoStreamType.kNERtcRemoteVideoStreamTypeHigh, true);
        remoteUserVv.setScalingType(NERtcConstants.VideoScalingType.SCALE_ASPECT_FILL);
        NERtcEx.getInstance().setupRemoteVideoCanvas(remoteUserVv, uid);

        // 更新界面
        remoteUserVv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUserVideoStop(long uid) {
        Log.i(TAG, "onUserVideoStop, uid=" + uid);
        if (!isCurrentUser(uid)) {
            return;
        }
        // 不展示远端
        remoteUserVv.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDisconnect(int reason) {
        Log.i(TAG, "onDisconnect reason: " + reason);
        if (reason != NERtcConstants.ErrorCode.OK) {
            finish();
        }
    }

    @Override
    public void onClientRoleChange(int old, int newRole) {
        Log.i(TAG, "onUserAudioStart old: " + old + ", newRole : " + newRole);
    }

    /**
     * 判断是否为onUserJoined中，设置了Tag的用户
     *
     * @param uid 用户ID
     * @return 用户ID是否匹配
     */
    private boolean isCurrentUser(long uid) {
        Object tag = remoteUserVv.getTag();
        Log.i(TAG, "isCurrentUser tag=" + tag);
        return tag != null && tag.equals(uid);
    }

    /**
     * 改变本地音频的可用性
     */
    private void changeAudioEnable() {
        enableLocalAudio = !enableLocalAudio;
        setLocalAudioEnable(enableLocalAudio);
    }

    /**
     * 改变本地视频的可用性
     */
    private void changeVideoEnable() {
        enableLocalVideo = !enableLocalVideo;
        setLocalVideoEnable(enableLocalVideo);
    }

    /**
     * 设置本地音频的可用性
     */
    private void setLocalAudioEnable(boolean enable) {
        enableLocalAudio = enable;
        NERtcEx.getInstance().enableLocalAudio(enableLocalAudio);
        enableAudioIb.setImageResource(enable ? R.drawable.selector_meeting_mute : R.drawable.selector_meeting_unmute);
    }

    /**
     * 设置本地视频的可用性
     */
    private void setLocalVideoEnable(boolean enable) {
        enableLocalVideo = enable;
        NERtcEx.getInstance().enableLocalVideo(enableLocalVideo);
        enableVideoIb.setImageResource(enable ? R.drawable.selector_meeting_close_video : R.drawable.selector_meeting_open_video);
        localUserVv.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        //remoteUserBgV.setBackgroundColor(getResources().getColor(enable ? R.color.white : R.color.black));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_audio:
                changeAudioEnable();
                break;
            case R.id.ib_leave:
                exit();
                break;
            case R.id.ib_video:
                changeVideoEnable();
                break;
            case R.id.img_camera_flip:
                NERtcEx.getInstance().switchCamera();
                break;
            default:
                break;
        }
    }
}