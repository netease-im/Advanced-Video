package com.netease.nmc.nertcsample.audiomixing;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.netease.audiomixing.R;
import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.audio.NERtcCreateAudioEffectOption;
import com.netease.lava.nertc.sdk.audio.NERtcCreateAudioMixingOption;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.netease.nmc.nertcsample.audiomixing.DialogRoomActivity.AudioMixingPlayState.STATE_PAUSED;
import static com.netease.nmc.nertcsample.audiomixing.DialogRoomActivity.AudioMixingPlayState.STATE_PLAYING;
import static com.netease.nmc.nertcsample.audiomixing.DialogRoomActivity.AudioMixingPlayState.STATE_STOPPED;


public class DialogRoomActivity extends BasicActivity implements AudioControlDialog.DialogActionsCallBack {


    private static String MUSIC_DIR = "music";
    private static String MUSIC1 = "music1.m4a";
    private static String MUSIC2 = "music2.m4a";
    private static String EFFECT1 = "effect1.wav";
    private static String EFFECT2 = "effect2.wav";

    TextView tvPlayStateHint;

    ImageView ivPlay;//暂停按钮

    ImageView ivNext;//下一首

    ImageView ivMore;

    private int musicIndex = 0;//默认伴音数组下标

    private int[] effectIndex;//音效数组

    private String[] musicPathArray;

    private String[] effectPathArray;

    private Handler mHandler;

    private int audioMixingVolume = 50;

    private int audioEffectVolume = 50;

    private int playMusicState = AudioMixingPlayState.STATE_STOPPED;

    /**
     * 音量控制dialog
     */
    AudioControlDialog controlDialog;

    @Override
    public void setMusicPlay(int index) {
        musicIndex = index;
        playMusicState = STATE_STOPPED;
        neRtcEx.stopAudioMixing();
        if (controlDialog != null) {
            if (index == 0) {
                controlDialog.setTextViewSelected(AudioControlDialog.TV_MUSIC_1, switchMusicState());
            } else if (index == 1) {
                controlDialog.setTextViewSelected(AudioControlDialog.TV_MUSIC_2, switchMusicState());
            }
        }
    }

    @Override
    public void onMusicVolumeChange(int progress) {
        audioMixingVolume = progress;
        neRtcEx.setAudioMixingSendVolume(progress);
        neRtcEx.setAudioMixingPlaybackVolume(progress);
    }

    @Override
    public boolean addEffect(int index) {
        return addAudioEffect(index);
    }

    @Override
    public void onEffectVolumeChange(int progress, int[] index) {
        audioEffectVolume = progress;
        //sample 中简单实用一个seekbar 控制所有effect的音量
        for (int i = 0; i < index.length; i++) {
            if (index[i] == 1) {
                neRtcEx.setEffectSendVolume(index2Id(i), audioEffectVolume);
                neRtcEx.setEffectPlaybackVolume(index2Id(i), audioEffectVolume);
            }
        }
    }

    @Override
    public boolean stopEffect(int index) {
        return neRtcEx.stopEffect(index2Id(index)) == 0;
    }

    /**
     * 伴音播放状态
     */
    public interface AudioMixingPlayState {
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
        tvPlayStateHint = findViewById(R.id.tv_play_content);
        ivPlay = findViewById(R.id.iv_music_play);
        ivPlay.setEnabled(false);
        ivMore = findViewById(R.id.iv_more_option);
        ivMore.setEnabled(false);
        ivNext = findViewById(R.id.iv_next);
        ivNext.setEnabled(false);
    }

    @Override
    protected void initOptions() {
        super.initOptions();
        mHandler = new Handler(getMainLooper());
        initMusicAndEffect();
        ivPlay.setOnClickListener(v -> {
            if(switchMusicState()) {
                ivPlay.setSelected(!ivPlay.isSelected());
            }
        });

        ivMore.setOnClickListener(v -> {
            showDialog();
        });

        ivNext.setOnClickListener(v -> playNextMusic());

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
                if (switchMusicState()) {
                    ivPlay.setSelected(true);
                    ivPlay.setEnabled(true);
                    ivNext.setEnabled(true);
                }
            });
        }).start();
    }

    /**
     * 播放下一首
     */
    private void playNextMusic() {
        if (musicIndex == 0) {
            musicIndex = 1;
        } else {
            musicIndex = 0;
        }
        playMusicState = STATE_STOPPED;
        neRtcEx.stopAudioMixing();
        if (switchMusicState()) {
            Toast.makeText(this, "已经播放下一首", Toast.LENGTH_SHORT).show();
            if (controlDialog != null) {
                if (musicIndex == 0) {
                    controlDialog.setTextViewSelected(AudioControlDialog.TV_MUSIC_1, true);
                } else if (musicIndex == 1) {
                    controlDialog.setTextViewSelected(AudioControlDialog.TV_MUSIC_2, true);
                }
            }
        } else {
            Toast.makeText(this, "播放下一首失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 伴音模式切换
     *
     * @return
     */
    private boolean switchMusicState() {
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
    private boolean addAudioEffect(int index) {
        if (effectPathArray == null || effectPathArray.length <= index) {
            return false;
        }
        NERtcCreateAudioEffectOption option = new NERtcCreateAudioEffectOption();
        option.path = effectPathArray[index];
        option.playbackVolume = audioEffectVolume;
        option.sendVolume = audioEffectVolume;
        option.loopCount = 1;
        neRtcEx.stopEffect(index2Id(index));
        if (effectIndex == null) {
            effectIndex = new int[2];
        }
        effectIndex[index] = 1;
        return neRtcEx.playEffect(index2Id(index), option) == 0;
    }

    @Override
    public void onAudioEffectFinished(int i) {
        super.onAudioEffectFinished(i);
        switch (i) {
            case 1:
                if (controlDialog != null) {
                    controlDialog.setTextViewSelected(AudioControlDialog.TV_EFFECT_1, false);
                }
                if (effectIndex != null) {
                    effectIndex[0] = 0;
                }
                break;
            case 2:
                if (controlDialog != null) {
                    controlDialog.setTextViewSelected(AudioControlDialog.TV_EFFECT_2, false);
                }
                if (effectIndex != null) {
                    effectIndex[1] = 0;
                }
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
            if(musicIndex == 0) {
                if (controlDialog != null) {
                    controlDialog.setTextViewSelected(AudioControlDialog.TV_MUSIC_1, false);
                }
            }else if(musicIndex == 1) {
                if (controlDialog != null) {
                    controlDialog.setTextViewSelected(AudioControlDialog.TV_MUSIC_2, false);
                }
            }
            playNextMusic();
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

    private void showDialog() {
        if (controlDialog == null) {
            controlDialog = new AudioControlDialog();
            controlDialog.setCallBack(this);
        }
        if (ivPlay.isEnabled()) {
            controlDialog.setInitData(musicIndex, effectIndex);
        }
        controlDialog.show(getSupportFragmentManager(), "dialog");
    }


    @Override
    protected void leaveChannel() {
        stopAllEffects();
        neRtcEx.stopAudioMixing();
        super.leaveChannel();
    }

    /**
     * 停止所有音效
     */
    private void stopAllEffects() {
        if (effectIndex != null) {
            for (int index = 0; index < effectIndex.length; index++) {
                if (effectIndex[index] == 1) {
                    neRtcEx.stopEffect(index2Id(index));
                }
            }
        }
    }

    /**
     * effect index to id,id can't be 0
     *
     * @param index
     * @return
     */
    private int index2Id(int index) {
        return index + 1;
    }
}
