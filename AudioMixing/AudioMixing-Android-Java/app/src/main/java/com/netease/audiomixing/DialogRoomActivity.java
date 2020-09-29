package com.netease.audiomixing;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.netease.lava.nertc.base.CommonUtil;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.audio.NERtcCreateAudioEffectOption;
import com.netease.lava.nertc.sdk.audio.NERtcCreateAudioMixingOption;
import com.netease.lava.nertc.sdk.video.NERtcVideoView;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.netease.audiomixing.DialogRoomActivity.AudioMixingPlayState.STATE_PAUSED;
import static com.netease.audiomixing.DialogRoomActivity.AudioMixingPlayState.STATE_PLAYING;
import static com.netease.audiomixing.DialogRoomActivity.AudioMixingPlayState.STATE_STOPPED;


public class DialogRoomActivity extends BasicActivity{


    private static String MUSIC_DIR = "music";
    private static String MUSIC1 = "music1.m4a";
    private static String MUSIC2 = "music2.m4a";
    private static String EFFECT1 = "effect1.wav";
    private static String EFFECT2 = "effect2.wav";

    TextView tvMusic1;

    TextView tvMusic2;

    TextView tvEffect1;

    TextView tvEffect2;

    TextView tvPlayStateHint;

    SeekBar sbrMusicVolume;

    SeekBar sbrEffectVolume;

    ImageView ivPlay;//暂停按钮

    ImageView ivMore;

    RelativeLayout rlyMusicControl;


    private int musicIndex = 0;//默认伴音数组下标

    private String[] musicPathArray;

    private String[] effectPathArray;

    private Handler mHandler;

    private int audioMixingVolume = 50;

    private int audioEffectVolume = 50;

    private int playMusicState = AudioMixingPlayState.STATE_STOPPED;

    /**
     * 伴音播放状态
     */
    interface AudioMixingPlayState {
        /**
         * 停止，未播放
         */
        int STATE_STOPPED = 0;

        /**
         * 播放中
         */
        int STATE_PLAYING = 1;

        /**
         * 暂停
         */
        int STATE_PAUSED = 2;
    }

    public static void startDialog(Context context,String roomId,long userId){
        Intent intent = new Intent(context,DialogRoomActivity.class);
        intent.putExtra(ROOM_ID,roomId);
        intent.putExtra(USER_ID,userId);
        context.startActivity(intent);
    }

    @Override
    protected int getPanelLayoutId() {
        return R.layout.mixing_panel_layout;
    }

    @Override
    protected void initPanelViews(View panel) {
        super.initPanelViews(panel);
        tvMusic1 = findViewById(R.id.tv_music_1);
        tvMusic2 = findViewById(R.id.tv_music_2);
        tvEffect1 = findViewById(R.id.tv_audio_effect_1);
        tvEffect2 = findViewById(R.id.tv_audio_effect_2);
        sbrMusicVolume = findViewById(R.id.music_song_volume_control);
        sbrEffectVolume = findViewById(R.id.audio_effect_volume_control);
        rlyMusicControl = findViewById(R.id.rl_music_action_container);
        tvPlayStateHint = findViewById(R.id.tv_play_content);
        ivPlay = findViewById(R.id.iv_music_play);
        ivPlay.setEnabled(false);
        ivMore = findViewById(R.id.iv_more_option);
        ivMore.setEnabled(false);
    }

    @Override
    protected void initOptions() {
        super.initOptions();
        mHandler = new Handler(getMainLooper());
        initMusicAndEffect();
        //======================伴音控制=======================
        ivPlay.setOnClickListener(v -> {
            if(switchMusicState()) {
                ivPlay.setSelected(!ivPlay.isSelected());
            }
        });
        tvMusic1.setOnClickListener(v -> {
            if(!tvMusic1.isSelected()) {
                musicIndex = 0;
                playMusicState = STATE_STOPPED;
                neRtcEx.stopAudioMixing();
                tvMusic1.setSelected(switchMusicState());
            }
            tvMusic2.setSelected(false);
        });

        tvMusic2.setOnClickListener(v -> {
            if(!tvMusic2.isSelected()) {
                musicIndex = 1;
                playMusicState = STATE_STOPPED;
                neRtcEx.stopAudioMixing();
                tvMusic2.setSelected(switchMusicState());
            }

            tvMusic1.setSelected(false);
        });

        ivMore.setOnClickListener(v -> {
            rlyMusicControl.setVisibility(View.VISIBLE);
        });

        sbrMusicVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioMixingVolume = progress;
                neRtcEx.setAudioMixingSendVolume(progress);
                neRtcEx.setAudioMixingPlaybackVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //====================音效控制======================
        tvEffect1.setOnClickListener(v -> {
            if(!tvEffect1.isSelected()){
                tvEffect1.setSelected(addAudioEffect(0));
            }
        });

        tvEffect2.setOnClickListener(v -> {
            if(!tvEffect2.isSelected()){
                tvEffect2.setSelected(addAudioEffect(1));
            }
        });

        sbrEffectVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioEffectVolume = progress;
                //sample 中简单实用一个seekbar 控制所有effect的音量
                if(tvEffect1.isSelected()){
                    neRtcEx.setEffectSendVolume(index2Id(0),audioEffectVolume);
                    neRtcEx.setEffectPlaybackVolume(index2Id(0),audioEffectVolume);
                }
                if(tvEffect2.isSelected()){
                    neRtcEx.setEffectSendVolume(index2Id(1),audioEffectVolume);
                    neRtcEx.setEffectPlaybackVolume(index2Id(1),audioEffectVolume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    /**
     * 初始化伴音和音效
     */
    private void initMusicAndEffect(){
        new Thread(() -> {
            String root = ensureMusicDirectory();
            effectPathArray = new String[]{extractMusicFile(root, EFFECT1), extractMusicFile(root, EFFECT2)};
            musicPathArray = new String[]{extractMusicFile(root, MUSIC1),extractMusicFile(root, MUSIC2)};
            mHandler.post(() -> {
                ivMore.setEnabled(true);
                if(switchMusicState()) {
                    ivPlay.setSelected(true);
                    ivPlay.setEnabled(true);
                    tvMusic1.setSelected(true);
                }
            });
        }).start();
    }

    /**
     * 伴音模式切换
     * @return
     */
    private boolean switchMusicState(){
        int stateOld = playMusicState;
        int stateNew;
        int result;
        if (stateOld == STATE_PLAYING) {
            stateNew = STATE_PAUSED;
            result = neRtcEx.pauseAudioMixing();
        } else if (stateOld == STATE_PAUSED) {
            stateNew = STATE_PLAYING;
            result = neRtcEx.resumeAudioMixing();
        } else {
            stateNew = STATE_PLAYING;
            NERtcCreateAudioMixingOption option = new NERtcCreateAudioMixingOption();
            option.path = musicPathArray[musicIndex];
            option.playbackVolume = audioMixingVolume;
            option.sendVolume = audioMixingVolume;
            option.loopCount = 1;
            result = neRtcEx.startAudioMixing(option);
        }
        if (result == 0) {
            playMusicState = stateNew;
            updatePlayStateHint();
        }
        return result == 0;
    }

    /**
     * 更新播放状态文案
     */
    private void updatePlayStateHint(){
        switch (playMusicState){
            case STATE_PLAYING:
                tvPlayStateHint.setText(getPlayStateString("正在播放"));
                break;
            case STATE_PAUSED:
                tvPlayStateHint.setText(getPlayStateString("已经暂停"));
                break;
            case STATE_STOPPED:
                tvPlayStateHint.setText(R.string.music_play_states_stop);
                break;
        }
    }

    /**
     * 获取播放状态文案
     * @param state
     * @return
     */
    private CharSequence getPlayStateString(String state){
        String music = null;
        if(musicIndex == 0){
            music = "音乐1";
        }else if(musicIndex == 1){
            music = "音乐2";
        }
        if(TextUtils.isEmpty(music)){
            return "";
        }
        SpannableString spannableString = new SpannableString(music+state);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ffa410")),0,music.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 音效添加，音效同时可以有多个
     * @param index
     * @return
     */
    private boolean addAudioEffect(int index){
        if(effectPathArray == null || effectPathArray.length <= index){
            return false;
        }
        NERtcCreateAudioEffectOption option = new NERtcCreateAudioEffectOption();
        option.path = effectPathArray[index];
        option.playbackVolume = audioEffectVolume;
        option.sendVolume = audioEffectVolume;
        option.loopCount = 1;
        neRtcEx.stopEffect(index2Id(index));
        return neRtcEx.playEffect(index2Id(index),option) == 0;
    }

    @Override
    public void onAudioEffectFinished(int i) {
        super.onAudioEffectFinished(i);
        switch (i){
            case 1:
                tvEffect1.setSelected(false);
                break;
            case 2:
                tvEffect2.setSelected(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAudioMixingStateChanged(int reason) {
        super.onAudioMixingStateChanged(reason);
        if(reason == NERtcConstants.ErrorCode.OK){
            playMusicState = STATE_STOPPED;
            tvPlayStateHint.setText(R.string.music_play_states_stop);
            if(musicIndex == 0){
                tvMusic1.setSelected(false);
            }else if(musicIndex == 1){
                tvEffect2.setSelected(false);
            }
        }
    }

    private String extractMusicFile(String path, String name) {
        copyAssetToFile(this, MUSIC_DIR + "/" + name, path, name);
        return new File(path, name).getAbsolutePath();
    }

    private void copyAssetToFile(Context context, String assetsName,
                                       String savePath, String saveName) {

        File dir = new File(savePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File destFile = new File(dir, saveName);
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(assetsName);
            if (destFile.exists() && inputStream.available() == destFile.length()) {
                return;
            }
            destFile.deleteOnExit();
            outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[4096];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuiet(inputStream);
            closeQuiet(outputStream);
        }
    }

    private void closeQuiet(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String ensureMusicDirectory() {
        File dir = getExternalFilesDir(MUSIC_DIR);
        if (dir == null) {
            dir = getDir(MUSIC_DIR, 0);
        }
        if (dir != null) {
            dir.mkdirs();
            return dir.getAbsolutePath();
        }
        return "";
    }



    @Override
    protected void leaveChannel() {
        neRtcEx.stopAllEffects();
        neRtcEx.stopAudioMixing();
        super.leaveChannel();
    }

    @Override
    public void onBackPressed() {
        if(rlyMusicControl.getVisibility() == View.VISIBLE){
            rlyMusicControl.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    /**
     * effect index to id,id can't be 0
     * @param index
     * @return
     */
    private int index2Id(int index){
        return index + 1;
    }
}
