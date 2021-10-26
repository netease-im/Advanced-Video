package com.netease.nmc.nertcsample.beauty;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.video.NERtcBeautyEffectType;
import com.netease.nertcbeautysample.R;
import com.netease.nmc.nertcsample.beauty.adapter.BeautyOptionsAdapter;
import com.netease.nmc.nertcsample.beauty.adapter.FilterAdapter;
import com.netease.nmc.nertcsample.beauty.adapter.ObjectAdapter;
import com.netease.nmc.nertcsample.beauty.adapter.StickerAdapter;
import com.netease.nmc.nertcsample.beauty.adapter.StickerOptionsAdapter;
import com.netease.nmc.nertcsample.beauty.utils.FileUtils;
import com.netease.nmc.nertcsample.beauty.view.BeautyOptionsItem;
import com.netease.nmc.nertcsample.beauty.view.FilterItem;
import com.netease.nmc.nertcsample.beauty.view.ObjectItem;
import com.netease.nmc.nertcsample.beauty.view.SpaceItemDecoration;
import com.netease.nmc.nertcsample.beauty.view.StickerItem;
import com.netease.nmc.nertcsample.beauty.view.StickerOptionsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//  Created by NetEase on 10/15/21.
//  Copyright (c) 2014-2021 NetEase, Inc. All rights reserved.
//
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BaseActivity";

    private TextView mFilterStrengthText;
    private SeekBar mFilterStrengthBar;
    private LinearLayout mStickerOptionsSwitch;
    private RelativeLayout mStickerOptions;
    private RecyclerView mStickerIcons;
    private RecyclerView mStickersRecycleView;
    private RelativeLayout mFilterAndBeautyOptionView;
    private LinearLayout mSelectOptions;
    private RecyclerView mBeautyOptionsRecycleView;
    private RecyclerView mStickerOptionsRecycleView, mFilterOptionsRecycleView;
    private RelativeLayout mFilterIconsRelativeLayout, mFilterStrengthLayout;
    private ImageView mBeautyOptionsSwitchIcon, mStickerOptionsSwitchIcon;
    private TextView mBeautyOptionsSwitchText, mStickerOptionsSwitchText;
    private LinearLayout mBeautyOptionsSwitch, mBaseBeautyOptions, mProfessionalBeautyOptions, mAdvBeautyOptions, mAdvBeautyOptions1, mAdvBeautyOptions2;
    private ArrayList<SeekBar> mBeautyParamsSeekBarList = new ArrayList<SeekBar>();

    private StickerOptionsAdapter mStickerOptionsAdapter;
    private BeautyOptionsAdapter mBeautyOptionsAdapter;

    protected ObjectAdapter mObjectAdapter;
    protected List<ObjectItem> mObjectList;

    protected ArrayList<StickerOptionsItem> mStickerOptionsList;
    protected ArrayList<BeautyOptionsItem> mBeautyOptionsList;

    protected HashMap<String, StickerAdapter> mStickerAdapters = new HashMap<>();
    protected HashMap<String, ArrayList<StickerItem>> mStickerlists = new HashMap<>();
    protected HashMap<String, FilterAdapter> mFilterAdapters = new HashMap<>();
    protected HashMap<String, ArrayList<FilterItem>> mFilterLists = new HashMap<>();

    protected boolean mIsBeautyOpen = false;
    protected int mCurrentFilterIndex = -1;
    protected int mCurrentObjectIndex = -1;
    private boolean mIsStickerOptionsOpen = false;
    private int mCurrentStickerOptionsIndex = -1;
    private int mCurrentStickerPosition = -1;
    private boolean mIsBeautyOptionsOpen = false;
    private int mBeautyOptionsPosition = 0;
    private float[] mBeautifyParams = {
            0.0f, //0 美牙
            0.0f, //1 亮眼
            0.0f, //2 美白
            0.0f, //3 磨皮
            0.0f, //4 小鼻
            0.5f, //5 眼距调整
            0.5f, //6 眼角调整
            0.5f, //7 嘴型调整
            0.0f, //8 大眼
            0.0f, //9 小脸
            0.0f, //10 下巴调整
            0.0f, //11 瘦脸
            //0.0f, //12 红润
            0.5f, //13 长鼻
            0.5f, //14 人中调整
            0.5f, //15 嘴角调整
            0.0f, //16 圆眼
            0.0f, //17 开眼角
            0.0f, //18 V脸
            0.0f, //19 瘦下颌
            0.0f, //20 窄脸
            0.0f, //21 颧骨调整
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NERtcEx.getInstance().stopBeauty();
        resetFaceBeautyParams();
    }

    protected void initFaceBeauty() {
        initFaceBeautyUI();
        initFaceBeautyEvents();
    }

    protected void initFaceBeautyUI() {
        mStickerOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_sticker_options);
        mStickerOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mStickerOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));

        mStickersRecycleView = (RecyclerView) findViewById(R.id.rv_sticker_icons);
        mStickersRecycleView.setLayoutManager(new GridLayoutManager(this, 6));
        mStickersRecycleView.addItemDecoration(new SpaceItemDecoration(0));

        mFilterOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_filter_icons);
        mFilterOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mFilterOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));

        mStickerlists.put("sticker_2d", FileUtils.getStickerFiles(getApplicationContext(), "2D"));
        mStickerlists.put("sticker_3d", FileUtils.getStickerFiles(getApplicationContext(), "3D"));
        mStickerlists.put("sticker_deformation", FileUtils.getStickerFiles(getApplicationContext(), "deformation"));
        mStickerlists.put("sticker_face_morph", FileUtils.getStickerFiles(getApplicationContext(), "face_morph"));
        mStickerlists.put("sticker_particle", FileUtils.getStickerFiles(getApplicationContext(), "particle"));

        mStickerAdapters.put("sticker_2d", new StickerAdapter(mStickerlists.get("sticker_2d"), getApplicationContext()));
        mStickerAdapters.put("sticker_3d", new StickerAdapter(mStickerlists.get("sticker_3d"), getApplicationContext()));
        mStickerAdapters.put("sticker_deformation", new StickerAdapter(mStickerlists.get("sticker_deformation"), getApplicationContext()));
        mStickerAdapters.put("sticker_face_morph", new StickerAdapter(mStickerlists.get("sticker_face_morph"), getApplicationContext()));
        mStickerAdapters.put("sticker_particle", new StickerAdapter(mStickerlists.get("sticker_particle"), getApplicationContext()));

        mStickerOptionsList = new ArrayList<>();
        mStickerOptionsList.add(0, new StickerOptionsItem("sticker_2d",BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.sticker_2d_unselected),BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.sticker_2d_selected)));
        mStickerOptionsList.add(1, new StickerOptionsItem("sticker_3d",BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.sticker_3d_unselected),BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.sticker_3d_selected)));
        mStickerOptionsList.add(2, new StickerOptionsItem("sticker_deformation",BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.sticker_dedormation_unselected),BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.sticker_dedormation_selected)));
        mStickerOptionsList.add(3, new StickerOptionsItem("sticker_face_morph",BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.sticker_face_morph_unselected),BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.sticker_face_morph_selected)));
        mStickerOptionsList.add(4, new StickerOptionsItem("particles",BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.particles_unselected),BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.particles_selected)));

        mStickerOptionsAdapter = new StickerOptionsAdapter(mStickerOptionsList, getApplicationContext());
        mStickersRecycleView.setAdapter(mStickerAdapters.get("sticker_2d"));

        mStickerAdapters.get("sticker_2d").setSelectedPosition(-1);

        findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));

        mFilterAndBeautyOptionView = (RelativeLayout) findViewById(R.id.rv_beauty_and_filter_options);

        mBeautyOptionsRecycleView = (RecyclerView) findViewById(R.id.rv_beauty_options);
        mBeautyOptionsRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        mBeautyOptionsRecycleView.addItemDecoration(new SpaceItemDecoration(0));

        mBeautyOptionsList = new ArrayList<>();
        mBeautyOptionsList.add(0, new BeautyOptionsItem("滤镜"));
        mBeautyOptionsList.add(1, new BeautyOptionsItem("基础美颜"));
        mBeautyOptionsList.add(2, new BeautyOptionsItem("美形"));
        mBeautyOptionsList.add(3, new BeautyOptionsItem("高级"));
        mBeautyOptionsList.add(4, new BeautyOptionsItem("高级1期"));
        mBeautyOptionsList.add(5, new BeautyOptionsItem("高级2期"));

        mBeautyOptionsAdapter = new BeautyOptionsAdapter(mBeautyOptionsList, this);
        mBeautyOptionsRecycleView.setAdapter(mBeautyOptionsAdapter);

        mFilterLists.put("filter_portrait", FileUtils.getFilterFiles(getApplicationContext(), "filter_portrait"));
        mFilterAdapters.put("filter_portrait", new FilterAdapter(mFilterLists.get("filter_portrait"), getApplicationContext()));

        mFilterIconsRelativeLayout = (RelativeLayout) findViewById(R.id.rl_filter_icons);

        mFilterStrengthText = (TextView) findViewById(R.id.tv_filter_strength);

        mFilterStrengthLayout = (RelativeLayout) findViewById(R.id.rv_filter_strength);
        mFilterStrengthBar = (SeekBar) findViewById(R.id.sb_filter_strength);
        mFilterStrengthBar.setProgress(65);
        mFilterStrengthText.setText("65");
        mFilterStrengthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFilterStrengthText.setText(progress+"");
                NERtcEx.getInstance().setBeautyFilterLevel(progress/100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mStickerOptionsRecycleView.setAdapter(mStickerOptionsAdapter);

        mObjectList = FileUtils.getObjectList();
        mObjectAdapter = new ObjectAdapter(mObjectList, this);
        mObjectAdapter.setSelectedPosition(-1);

        mStickersRecycleView.setAdapter(mStickerAdapters.get("sticker_2d"));
        mFilterOptionsRecycleView.setAdapter(mFilterAdapters.get("filter_portrait"));

        mBeautyOptionsSwitch = (LinearLayout) findViewById(R.id.ll_beauty_options_switch);
        mBeautyOptionsSwitch.setOnClickListener(this);

        mBaseBeautyOptions = (LinearLayout) findViewById(R.id.ll_base_beauty_options);
        mBaseBeautyOptions.setOnClickListener(null);
        mProfessionalBeautyOptions = (LinearLayout) findViewById(R.id.ll_professional_beauty_options);
        mProfessionalBeautyOptions.setOnClickListener(null);
        mAdvBeautyOptions = (LinearLayout) findViewById(R.id.ll_adv_beauty_options);
        mAdvBeautyOptions.setOnClickListener(null);
        mAdvBeautyOptions1 = (LinearLayout) findViewById(R.id.ll_adv1_beauty_options);
        mAdvBeautyOptions1.setOnClickListener(null);
        mAdvBeautyOptions2 = (LinearLayout) findViewById(R.id.ll_adv2_beauty_options);
        mAdvBeautyOptions2.setOnClickListener(null);
        mIsBeautyOptionsOpen = false;

        mStickerOptionsSwitch = (LinearLayout) findViewById(R.id.ll_sticker_options_switch);
        mStickerOptionsSwitch.setOnClickListener(this);
        mStickerOptions = (RelativeLayout) findViewById(R.id.rl_sticker_options);
        mStickerIcons = (RecyclerView) findViewById(R.id.rv_sticker_icons);
        mIsStickerOptionsOpen = false;

        mSelectOptions = (LinearLayout)findViewById(R.id.ll_select_options);
        mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));
    }

    protected void initFaceBeautyEvents() {

        //init sticker
        initStickerAdapter("sticker_2d", 0);
        initStickerAdapter("sticker_3d", 1);
        initStickerAdapter("sticker_deformation", 2);
        initStickerAdapter("sticker_face_morph", 3);
        initStickerAdapter("sticker_particle", 4);

        mStickerOptionsAdapter.setClickStickerListener(v -> {
            int position = Integer.parseInt(v.getTag().toString());
            mStickerOptionsAdapter.setSelectedPosition(position);
            mStickersRecycleView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 6));

            mStickerAdapters.get("sticker_2d").setSelectedPosition(-1);
            mStickerAdapters.get("sticker_3d").setSelectedPosition(-1);
            mStickerAdapters.get("sticker_deformation").setSelectedPosition(-1);
            mStickerAdapters.get("sticker_face_morph").setSelectedPosition(-1);
            mStickerAdapters.get("sticker_particle").setSelectedPosition(-1);

            if(mCurrentStickerOptionsIndex == 0){
                mStickerAdapters.get("sticker_2d").setSelectedPosition(mCurrentStickerPosition);
            }
            else if(mCurrentStickerOptionsIndex == 1){
                mStickerAdapters.get("sticker_3d").setSelectedPosition(mCurrentStickerPosition);
            }
            else if(mCurrentStickerOptionsIndex == 2){
                mStickerAdapters.get("sticker_deformation").setSelectedPosition(mCurrentStickerPosition);
            }else if(mCurrentStickerOptionsIndex == 3){
                mStickerAdapters.get("sticker_face_morph").setSelectedPosition(mCurrentStickerPosition);
            }else if(mCurrentStickerOptionsIndex == 4){
                mStickerAdapters.get("sticker_particle").setSelectedPosition(mCurrentStickerPosition);
            }

            if(position == 0){
                mStickersRecycleView.setAdapter(mStickerAdapters.get("sticker_2d"));
            }
            else if(position == 1){
                mStickersRecycleView.setAdapter(mStickerAdapters.get("sticker_3d"));
            }
            else if(position == 2){
                mStickersRecycleView.setAdapter(mStickerAdapters.get("sticker_deformation"));
            }else if(position == 3){
                mStickersRecycleView.setAdapter(mStickerAdapters.get("sticker_face_morph"));
            }else if(position == 4){
                mStickersRecycleView.setAdapter(mStickerAdapters.get("sticker_particle"));
            }
            mStickerOptionsAdapter.notifyDataSetChanged();
        });

        mFilterAdapters.get("filter_portrait").setClickFilterListener(v -> {
            resetFilterView();
            int position = Integer.parseInt(v.getTag().toString());
            mFilterAdapters.get("filter_portrait").setSelectedPosition(position);
            mCurrentFilterIndex = -1;

            if(position == 0){
                NERtcEx.getInstance().removeBeautyFilter();
            } else {
                NERtcEx.getInstance().addBeautyFilter(mFilterLists.get("filter_portrait").get(position).model);
                mCurrentFilterIndex = position;
                mFilterStrengthLayout.setVisibility(View.VISIBLE);
            }

            mFilterAdapters.get("filter_portrait").notifyDataSetChanged();
        });

        mBeautyOptionsAdapter.setClickBeautyListener(v -> {
            int position = Integer.parseInt(v.getTag().toString());
            mBeautyOptionsAdapter.setSelectedPosition(position);
            mBeautyOptionsPosition = position;

            if(position == 0) {
                mFilterIconsRelativeLayout.setVisibility(View.VISIBLE);
                mFilterStrengthLayout.setVisibility(mCurrentFilterIndex > 0 ? View.VISIBLE : View.INVISIBLE);
                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                mProfessionalBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions1.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions2.setVisibility(View.INVISIBLE);
            } else if(position == 1) {
                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                mBaseBeautyOptions.setVisibility(View.VISIBLE);
                mProfessionalBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions1.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions2.setVisibility(View.INVISIBLE);
            } else if(position == 2) {
                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                mProfessionalBeautyOptions.setVisibility(View.VISIBLE);
                mAdvBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions1.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions2.setVisibility(View.INVISIBLE);
            } else if(position == 3) {
                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                mProfessionalBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions.setVisibility(View.VISIBLE);
                mAdvBeautyOptions1.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions2.setVisibility(View.INVISIBLE);
            } else if(position == 4) {
                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                mProfessionalBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions1.setVisibility(View.VISIBLE);
                mAdvBeautyOptions2.setVisibility(View.INVISIBLE);
            } else if(position == 5) {
                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                mProfessionalBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions1.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions2.setVisibility(View.VISIBLE);
            }

            mBeautyOptionsAdapter.notifyDataSetChanged();
        });


        mBeautyParamsSeekBarList.add(0, (SeekBar) findViewById(R.id.sb_beauty_whiteteeth_strength));
        mBeautyParamsSeekBarList.add(1, (SeekBar) findViewById(R.id.sb_beauty_lighteye_strength));
        mBeautyParamsSeekBarList.add(2, (SeekBar) findViewById(R.id.sb_beauty_whiten_strength));
        mBeautyParamsSeekBarList.add(3, (SeekBar) findViewById(R.id.sb_beauty_smooth_strength));
        mBeautyParamsSeekBarList.add(4, (SeekBar) findViewById(R.id.sb_beauty_smallnose_strength));
        mBeautyParamsSeekBarList.add(5, (SeekBar) findViewById(R.id.sb_beauty_eyedis_strength));
        mBeautyParamsSeekBarList.add(6, (SeekBar) findViewById(R.id.sb_beauty_eyeangle_strength));
        mBeautyParamsSeekBarList.add(7, (SeekBar) findViewById(R.id.sb_beauty_mouth_strength));
        mBeautyParamsSeekBarList.add(8, (SeekBar) findViewById(R.id.sb_beauty_enlarge_eye_strength));
        mBeautyParamsSeekBarList.add(9, (SeekBar) findViewById(R.id.sb_beauty_shrink_face_strength));
        mBeautyParamsSeekBarList.add(10, (SeekBar) findViewById(R.id.sb_beauty_shrink_jaw_strength));
        mBeautyParamsSeekBarList.add(11, (SeekBar) findViewById(R.id.sb_beauty_thinface_strength));
        mBeautyParamsSeekBarList.add(12, (SeekBar) findViewById(R.id.sb_beauty_longnose_strength));
        mBeautyParamsSeekBarList.add(13, (SeekBar) findViewById(R.id.sb_beauty_renzhong_strength));
        mBeautyParamsSeekBarList.add(14, (SeekBar) findViewById(R.id.sb_beauty_mouthangle_strength));
        mBeautyParamsSeekBarList.add(15, (SeekBar) findViewById(R.id.sb_beauty_roundeye_strength));
        mBeautyParamsSeekBarList.add(16, (SeekBar) findViewById(R.id.sb_beauty_eyecorner_strength));
        mBeautyParamsSeekBarList.add(17, (SeekBar) findViewById(R.id.sb_beauty_vface_strength));
        mBeautyParamsSeekBarList.add(18, (SeekBar) findViewById(R.id.sb_beauty_underjaw_strength));
        mBeautyParamsSeekBarList.add(19, (SeekBar) findViewById(R.id.sb_beauty_narrowface_strength));
        mBeautyParamsSeekBarList.add(20, (SeekBar) findViewById(R.id.sb_beauty_cheekbone_strength));

        for(int i = 0; i < 21; i++){
            final int index = i;

            updateBeautyParamsStrength(i, (int)(mBeautifyParams[i]*100));
            mBeautyParamsSeekBarList.get(i).setProgress((int)(mBeautifyParams[i]*100));
            mBeautyParamsSeekBarList.get(i).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateBeautyParamsStrength(index, progress);
                    float level = (float)progress/100;
                    mBeautifyParams[index] = level;

                    NERtcBeautyEffectType type = NERtcBeautyEffectType.kNERtcBeautyWhiten;
                    switch (index) {
                        case 0://亮齿
                            type = NERtcBeautyEffectType.kNERtcBeautyWhiteTeeth;
                            break;
                        case 1://亮眼
                            type = NERtcBeautyEffectType.kNERtcBeautyLightEye;
                            break;
                        case 2://美白
                            type = NERtcBeautyEffectType.kNERtcBeautyWhiten;
                            break;
                        case 3://磨皮
                            type = NERtcBeautyEffectType.kNERtcBeautySmooth;
                            break;
                        case 4://廋鼻
                            type = NERtcBeautyEffectType.kNERtcBeautySmallNose;
                            break;
                        case 5://眼距
                            type = NERtcBeautyEffectType.kNERtcBeautyEyeDis;
                            break;
                        case 6://眼角
                            type = NERtcBeautyEffectType.kNERtcBeautyEyeAngle;
                            break;
                        case 7://小嘴
                            type = NERtcBeautyEffectType.kNERtcBeautyMouth;
                            break;
                        case 8://大眼
                            type = NERtcBeautyEffectType.kNERtcBeautyBigEye;
                            break;
                        case 9://小脸
                            type = NERtcBeautyEffectType.kNERtcBeautySmallFace;
                            break;
                        case 10://下巴
                            type = NERtcBeautyEffectType.kNERtcBeautyJaw;
                            break;
                        case 11://瘦脸
                            type = NERtcBeautyEffectType.kNERtcBeautyThinFace;
                            break;
                        case 12://长鼻
                            type = NERtcBeautyEffectType.kNERtcBeautyLongNose;
                            break;
                        case 13://人中调整
                            type = NERtcBeautyEffectType.kNERtcBeautyPhiltrum;
                            break;
                        case 14://嘴角调整
                            type = NERtcBeautyEffectType.kNERtcBeautyMouthAngle;
                            break;
                        case 15://圆眼
                            type = NERtcBeautyEffectType.kNERtcBeautyRoundEye;
                            break;
                        case 16://开眼角
                            type = NERtcBeautyEffectType.kNERtcBeautyEyeCorner;
                            break;
                        case 17://V脸
                            type = NERtcBeautyEffectType.kNERtcBeautyVFace;
                            break;
                        case 18://瘦下颌
                            type = NERtcBeautyEffectType.kNERtcBeautyUnderJaw;
                            break;
                        case 19://窄脸
                            type = NERtcBeautyEffectType.kNERtcBeautyNarrowFace;
                            break;
                        case 20://颧骨调整
                            type = NERtcBeautyEffectType.kNERtcBeautyCheekBone;
                            break;
                        default:
                            break;
                    }
                    NERtcEx.getInstance().setBeautyEffect(type, level);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        mObjectAdapter.setClickObjectListener(v -> {
            int position = Integer.parseInt(v.getTag().toString());

            if(mCurrentObjectIndex == position){
                mCurrentObjectIndex = -1;
                mObjectAdapter.setSelectedPosition(-1);
                mObjectAdapter.notifyDataSetChanged();
            }else {
                mObjectAdapter.setSelectedPosition(position);

                mObjectAdapter.notifyDataSetChanged();

                mCurrentObjectIndex = position;
            }

        });

        findViewById(R.id.rv_close_sticker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStickerAdapters.get("sticker_2d").setSelectedPosition(-1);
                mStickerAdapters.get("sticker_2d").notifyDataSetChanged();
                mStickerAdapters.get("sticker_3d").setSelectedPosition(-1);
                mStickerAdapters.get("sticker_3d").notifyDataSetChanged();

                mStickerAdapters.get("sticker_deformation").setSelectedPosition(-1);
                mStickerAdapters.get("sticker_deformation").notifyDataSetChanged();

                mStickerAdapters.get("sticker_face_morph").setSelectedPosition(-1);
                mStickerAdapters.get("sticker_face_morph").notifyDataSetChanged();

                mCurrentStickerPosition = -1;
                NERtcEx.getInstance().removeBeautySticker();
                NERtcEx.getInstance().removeBeautyMakeup();

                mCurrentObjectIndex = -1;
                mObjectAdapter.setSelectedPosition(-1);
                mObjectAdapter.notifyDataSetChanged();

                findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
            }
        });
    }

    protected void resetFaceBeautyParams() {
        mBeautifyParams = new float[] {
                0.0f, //0 美牙
                0.0f, //1 亮眼
                0.0f, //2 美白
                0.0f, //3 磨皮
                0.0f, //4 小鼻
                0.5f, //5 眼距调整
                0.5f, //6 眼角调整
                0.5f, //7 嘴型调整
                0.0f, //8 大眼
                0.0f, //9 小脸
                0.0f, //10 下巴调整
                0.0f, //11 瘦脸
                //0.0f, //12 红润
                0.5f, //13 长鼻
                0.5f, //14 人中调整
                0.5f, //15 嘴角调整
                0.0f, //16 圆眼
                0.0f, //17 开眼角
                0.0f, //18 V脸
                0.0f, //19 瘦下颌
                0.0f, //20 窄脸
                0.0f, //21 颧骨调整
        };

        for (int i = 0; i < mBeautyParamsSeekBarList.size(); ++i) {
            updateBeautyParamsStrength(i, (int)(mBeautifyParams[i]*100));
            mBeautyParamsSeekBarList.get(i).setProgress((int)(mBeautifyParams[i]*100));
        }
    }

    private void initStickerAdapter(final String stickerClassName, final int index){
        mStickerAdapters.get(stickerClassName).setClickStickerListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(v.getTag().toString());

                if(mCurrentStickerOptionsIndex == index && mCurrentStickerPosition == position){
                    mStickerAdapters.get(stickerClassName).setSelectedPosition(-1);
                    mCurrentStickerOptionsIndex = -1;
                    mCurrentStickerPosition = -1;

                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker_selected));
                    NERtcEx.getInstance().removeBeautySticker();
                    NERtcEx.getInstance().removeBeautyMakeup();

                } else {
                    mCurrentStickerOptionsIndex = index;
                    mCurrentStickerPosition = position;

                    findViewById(R.id.iv_close_sticker).setBackground(getResources().getDrawable(R.drawable.close_sticker));
                    mStickerAdapters.get(stickerClassName).setSelectedPosition(position);

                    if(index == 0) { //2d 贴纸
                        NERtcEx.getInstance().addBeautySticker(mStickerlists.get(stickerClassName).get(position).path);

                    } else if(index == 1) { //3D
                        NERtcEx.getInstance().addBeautySticker(mStickerlists.get(stickerClassName).get(position).path);

                    } else if(index == 3) { //make up
                        NERtcEx.getInstance().addBeautyMakeup(mStickerlists.get(stickerClassName).get(position).path);
                    } else {
                        NERtcEx.getInstance().addBeautyMakeup(mStickerlists.get(stickerClassName).get(position).path);
                    }
                }

                mStickerAdapters.get(stickerClassName).notifyDataSetChanged();
            }
        });
    }

    private void resetFilterView() {

        mFilterAdapters.get("filter_portrait").setSelectedPosition(-1);
        mFilterAdapters.get("filter_portrait").notifyDataSetChanged();
        mFilterStrengthLayout.setVisibility(View.INVISIBLE);
    }

    private void updateBeautyParamsStrength(int index, int strength) {
        switch (index){
            case 0:
                ((TextView)findViewById(R.id.tv_beauty_whiteteeth_strength)).setText(""+strength);
                break;
            case 1:
                ((TextView)findViewById(R.id.tv_beauty_lighteye_strength)).setText(""+strength);
                break;
            case 2:
                ((TextView)findViewById(R.id.tv_beauty_whiten_strength)).setText(""+strength);
                break;
            case 3:
                ((TextView)findViewById(R.id.tv_beauty_smooth_strength)).setText(""+strength);
                break;
            case 4:
                ((TextView)findViewById(R.id.tv_beauty_smallnose_strength)).setText(""+strength);
                break;
            case 5:
                ((TextView)findViewById(R.id.tv_beauty_eyedis_strength)).setText(""+strength);
                break;
            case 6:
                ((TextView)findViewById(R.id.tv_beauty_eyeangle_strength)).setText(""+strength);
                break;
            case 7:
                ((TextView)findViewById(R.id.tv_beauty_mouth_strength)).setText(""+strength);
                break;
            case 8:
                ((TextView)findViewById(R.id.tv_beauty_enlarge_eye_strength)).setText(""+strength);
                break;
            case 9:
                ((TextView)findViewById(R.id.tv_beauty_shrink_face_strength)).setText(""+strength);
                break;
            case 10:
                ((TextView)findViewById(R.id.tv_beauty_shrink_jaw_strength)).setText(""+strength);
                break;
            case 11:
                ((TextView)findViewById(R.id.tv_beauty_thinface_strength)).setText(""+strength);
                break;
            case 12:
                ((TextView)findViewById(R.id.tv_beauty_longnose_strength)).setText(""+strength);
                break;
            case 13:
                ((TextView)findViewById(R.id.tv_beauty_renzhong_strength)).setText(""+strength);
                break;
            case 14:
                ((TextView)findViewById(R.id.tv_beauty_mouthangle_strength)).setText(""+strength);
                break;
            case 15:
                ((TextView)findViewById(R.id.tv_beauty_roundeye_strength)).setText(""+strength);
                break;
            case 16:
                ((TextView)findViewById(R.id.tv_beauty_eyecorner_strength)).setText(""+strength);
                break;
            case 17:
                ((TextView)findViewById(R.id.tv_beauty_vface_strength)).setText(""+strength);
                break;
            case 18:
                ((TextView)findViewById(R.id.tv_beauty_underjaw_strength)).setText(""+strength);
                break;
            case 19:
                ((TextView)findViewById(R.id.tv_beauty_narrowface_strength)).setText(""+strength);
                break;
            case 20:
                ((TextView)findViewById(R.id.tv_beauty_cheekbone_strength)).setText(""+strength);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_sticker_options_switch:
                mSelectOptions.setBackgroundColor(Color.parseColor("#80000000"));

                mStickerOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_sticker_options_switch);
                mBeautyOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_beauty_options_switch);
                mStickerOptionsSwitchText = (TextView) findViewById(R.id.tv_sticker_options_switch);
                mBeautyOptionsSwitchText = (TextView) findViewById(R.id.tv_beauty_options_switch);
                if(mIsStickerOptionsOpen){
                    mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));
                    mStickerOptions.setVisibility(View.INVISIBLE);
                    mStickerIcons.setVisibility(View.INVISIBLE);
                    mStickerOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.sticker));
                    mStickerOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
                    mIsStickerOptionsOpen = false;
                } else {
                    mStickerOptions.setVisibility(View.VISIBLE);
                    mStickerIcons.setVisibility(View.VISIBLE);
                    mStickerOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.sticker_chosed));
                    mStickerOptionsSwitchText.setTextColor(Color.parseColor("#c460e1"));
                    mIsStickerOptionsOpen = true;
                }

                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                mFilterAndBeautyOptionView.setVisibility(View.INVISIBLE);
                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                mProfessionalBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions1.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions2.setVisibility(View.INVISIBLE);
                mBeautyOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.beauty));
                mBeautyOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
                mIsBeautyOptionsOpen = false;

                break;

            case R.id.ll_beauty_options_switch:
                mSelectOptions.setBackgroundColor(Color.parseColor("#80000000"));
                mStickerOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_sticker_options_switch);
                mBeautyOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_beauty_options_switch);
                mStickerOptionsSwitchText = (TextView) findViewById(R.id.tv_sticker_options_switch);
                mBeautyOptionsSwitchText = (TextView) findViewById(R.id.tv_beauty_options_switch);
                if(mIsBeautyOptionsOpen){
                    mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));

                    mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                    mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                    mFilterAndBeautyOptionView.setVisibility(View.INVISIBLE);
                    mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                    mProfessionalBeautyOptions.setVisibility(View.INVISIBLE);
                    mAdvBeautyOptions.setVisibility(View.INVISIBLE);
                    mAdvBeautyOptions1.setVisibility(View.INVISIBLE);
                    mAdvBeautyOptions2.setVisibility(View.INVISIBLE);
                    mBeautyOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.beauty));
                    mBeautyOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
                    mIsBeautyOptionsOpen = false;
                } else {
                    if(mBeautyOptionsPosition == 0){
                        mFilterIconsRelativeLayout.setVisibility(View.VISIBLE);
                        mFilterStrengthLayout.setVisibility(mCurrentFilterIndex > 0 ? View.VISIBLE : View.INVISIBLE);
                    }else if(mBeautyOptionsPosition == 1){
                        mBaseBeautyOptions.setVisibility(View.VISIBLE);
                    }else if(mBeautyOptionsPosition == 2){
                        mProfessionalBeautyOptions.setVisibility(View.VISIBLE);
                    }else if(mBeautyOptionsPosition == 3){
                        mAdvBeautyOptions.setVisibility(View.VISIBLE);
                    }else if(mBeautyOptionsPosition == 4){
                        mAdvBeautyOptions1.setVisibility(View.VISIBLE);
                    }else if(mBeautyOptionsPosition == 5){
                        mAdvBeautyOptions2.setVisibility(View.VISIBLE);
                    }
                    mFilterAndBeautyOptionView.setVisibility(View.VISIBLE);
                    mBeautyOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.beauty_chosed));
                    mBeautyOptionsSwitchText.setTextColor(Color.parseColor("#c460e1"));
                    mIsBeautyOptionsOpen = true;

                    mStickerOptions.setVisibility(View.INVISIBLE);
                    mStickerIcons.setVisibility(View.INVISIBLE);
                }

                mStickerOptionsSwitchIcon.setImageDrawable(getResources().getDrawable(R.drawable.sticker));
                mStickerOptionsSwitchText.setTextColor(Color.parseColor("#ffffff"));
                mIsStickerOptionsOpen = false;

                break;

            default:
                mSelectOptions.setBackgroundColor(Color.parseColor("#00000000"));
                mStickerOptions.setVisibility(View.INVISIBLE);
                mStickerIcons.setVisibility(View.INVISIBLE);

                mStickerOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_sticker_options_switch);
                mBeautyOptionsSwitchIcon = (ImageView) findViewById(R.id.iv_beauty_options_switch);
                mStickerOptionsSwitchText = (TextView) findViewById(R.id.tv_sticker_options_switch);
                mBeautyOptionsSwitchText = (TextView) findViewById(R.id.tv_beauty_options_switch);

                mIsStickerOptionsOpen = false;

                mFilterIconsRelativeLayout.setVisibility(View.INVISIBLE);
                mFilterStrengthLayout.setVisibility(View.INVISIBLE);
                mFilterAndBeautyOptionView.setVisibility(View.INVISIBLE);
                mBaseBeautyOptions.setVisibility(View.INVISIBLE);
                mProfessionalBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions1.setVisibility(View.INVISIBLE);
                mAdvBeautyOptions2.setVisibility(View.INVISIBLE);
                mIsBeautyOptionsOpen = false;
                break;
        }
    }

}
