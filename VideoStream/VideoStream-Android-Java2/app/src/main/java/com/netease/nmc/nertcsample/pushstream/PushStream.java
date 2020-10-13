package com.netease.nmc.nertcsample.pushstream;

import android.content.Context;
import android.graphics.Rect;
import android.widget.Toast;

import com.netease.lava.nertc.sdk.NERtcConstants;
import com.netease.lava.nertc.sdk.NERtcEx;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamImageInfo;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamLayout;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamTaskInfo;
import com.netease.lava.nertc.sdk.live.NERtcLiveStreamUserTranscoding;
import com.netease.nmc.nertcsample.R;

import java.util.ArrayList;
import java.util.List;

import static com.netease.lava.nertc.sdk.live.NERtcLiveStreamTaskInfo.NERtcLiveStreamMode.kNERtcLsModeVideo;
import static com.netease.lava.nertc.sdk.live.NERtcLiveStreamUserTranscoding.NERtcLiveStreamVideoScaleMode.kNERtcLsModeVideoScaleCropFill;

public class PushStream {
    private final Context context;

    // 推流任务ID
    private final String taskId;

    // 推流用户
    private final List<Long> userIds = new ArrayList<>();

    private Runnable callback;

    // 当前推流任务
    private NERtcLiveStreamTaskInfo task;

    public PushStream(Context context, String taskId, long userId, Runnable callback) {
        this.context = context;
        this.taskId = taskId;
        this.userIds.add(userId);
        this.callback = callback;
    }

    public boolean isStarted() {
        return task != null;
    }

    public void start(String url) {
        task = createLiveStreamTask(taskId, url); // 创建推流配置
        task.layout = createLiveStreamLayout(); // 创建视频布局
        task.layout.userTranscodingList = createUserTranscodingList(userIds, Config.getRectUsers()); // 创建用户视频布局

        // 添加推流任务
        int result = NERtcEx.getInstance().addLiveStreamTask(task, (taskId, result2) -> {
            hintResult("add", result2);
            if (result2 != 0) {
                task = null;
            }
            if (result2 == 0) {
                notifyStateChange();
            }
        });
        hintResult("add", result);
        if (result != 0) {
            task = null;
        }
    }

    public void stop() {
        if (task == null) {
            return;
        }
        task = null;

        notifyStateChange();

        // 删除推流任务
        int result = NERtcEx.getInstance().removeLiveStreamTask(taskId, (taskId, result2) -> {
            hintResult("remove", result2);
        });
        hintResult("remove", result);
    }

    public void update(long userId, boolean add) {
        boolean changed = false;
        if (add) {
            if (!userIds.contains(userId)) {
                userIds.add(userId);
                changed = true;
            }
        } else {
            changed = userIds.remove(userId);
        }
        if (!changed) {
            return;
        }
        if (task == null) {
            return;
        }
        // 更新推流任务用户布局
        task.layout.userTranscodingList = createUserTranscodingList(userIds, Config.getRectUsers());
        // 更新推流任务
        int result = NERtcEx.getInstance().updateLiveStreamTask(task, (taskId, result2) -> {
            hintResult("update", result2);
        });
        hintResult("update", result);
    }

    public void updateState(int state) {
        hintState(state);

        // 推流失败，停止推流
        if (state == NERtcConstants.LiveStreamState.STATE_PUSH_FAIL) {
            stop();
        }
    }

    private void notifyStateChange() {
        if (callback != null) {
            callback.run();
        }
    }

    private void hintResult(String action, int result) {
        if (result == 0) {
            return;
        }
        Toast.makeText(context, context.getString(R.string.push_stream_error, action, result), Toast.LENGTH_SHORT).show();
    }

    private void hintState(int state) {
        int strId = 0;
        switch (state) {
            case NERtcConstants.LiveStreamState.STATE_PUSHING:
                strId = R.string.push_stream_state_pushing;
                break;
            case NERtcConstants.LiveStreamState.STATE_PUSH_FAIL:
                strId = R.string.push_stream_state_failed;
                break;
            case NERtcConstants.LiveStreamState.STATE_PUSH_STOPPED:
                strId = R.string.push_stream_state_stopped;
                break;
            default:
                break;
        }
        if (strId != 0) {
            Toast.makeText(context, strId, Toast.LENGTH_SHORT).show();
        }
    }

    private static NERtcLiveStreamTaskInfo createLiveStreamTask(String taskId, String pushUrl) {
        NERtcLiveStreamTaskInfo task = new NERtcLiveStreamTaskInfo();
        task.taskId = taskId; // 推流任务ID
        task.url = pushUrl; // 直播推流地址
        task.serverRecordEnabled = false; // 关闭服务器录制功能
        task.liveMode = kNERtcLsModeVideo; // 直播推流视频模式
        return task;
    }

    private static NERtcLiveStreamLayout createLiveStreamLayout() {
        Rect rectLayout = Config.getRectLayout();
        NERtcLiveStreamImageInfo backgroundImage = Config.getBackgroundImage();
        int backgroundColor = Config.getBackgroundColor();

        NERtcLiveStreamLayout layout = new NERtcLiveStreamLayout();
        layout.width = rectLayout.width(); // 视频推流宽度
        layout.height = rectLayout.height(); // 视频推流高度
        layout.backgroundImg = backgroundImage; // 视频推流背景图
        layout.backgroundColor = backgroundColor; // 视频推流背景色
        return layout;
    }

    private static ArrayList<NERtcLiveStreamUserTranscoding> createUserTranscodingList(List<Long> userIds, Rect[] rectUsers) {
        ArrayList<NERtcLiveStreamUserTranscoding> userTranscodingList = new ArrayList<>();
        for (int i = 0; i < userIds.size() && i < rectUsers.length; i++) {
            NERtcLiveStreamUserTranscoding userTranscoding = new NERtcLiveStreamUserTranscoding();
            userTranscoding.uid = userIds.get(i);  // 用户uid
            userTranscoding.audioPush = true; // 推送该用户音频流
            userTranscoding.videoPush = true; // 推送该用户视频流
            userTranscoding.adaption = kNERtcLsModeVideoScaleCropFill; // 视频流裁剪模式
            Rect rectUser = rectUsers[i];
            userTranscoding.x = rectUser.left; // 离主画面左边距
            userTranscoding.y = rectUser.top; // 离主画面上边距
            userTranscoding.width = rectUser.width(); // 在主画面的显示宽度
            userTranscoding.height = rectUser.height(); // 在主画面的显示高度
            userTranscodingList.add(userTranscoding);
        }
        return userTranscodingList;
    }
}
