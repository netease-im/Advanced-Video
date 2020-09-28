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

    private final String taskId;

    private final List<Long> userIds = new ArrayList<>();

    private Runnable callback;

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
        task = createLiveStreamTask(taskId, url);
        task.layout = createLiveStreamLayout();
        task.layout.userTranscodingList = createUserTranscodingList(userIds, Config.getRectUsers());

        int result = NERtcEx.getInstance().addLiveStreamTask(task, (taskId, result1) -> {
            hintResult("add", result1);
            if (result1 != 0) {
                task = null;
            }
            if (result1 == 0) {
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

        int result = NERtcEx.getInstance().removeLiveStreamTask(taskId, (taskId, result2) -> {
            hintResult("remove", result2);
        });
        hintResult("remove", result);
    }

    public void updateUser(long userId, boolean start) {
        boolean changed = false;
        if (start) {
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
        task.layout.userTranscodingList = createUserTranscodingList(userIds, Config.getRectUsers());
        int result = NERtcEx.getInstance().updateLiveStreamTask(task, (taskId, result2) -> {
            hintResult("update", result2);
        });
        hintResult("update", result);
    }

    public void updateState(int state) {
        hintState(state);
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
        task.taskId = taskId;
        task.url = pushUrl;
        task.serverRecordEnabled = false;
        task.liveMode = kNERtcLsModeVideo;
        return task;
    }

    private static NERtcLiveStreamLayout createLiveStreamLayout() {
        Rect rectLayout = Config.getRectLayout();
        NERtcLiveStreamImageInfo backgroundImage = Config.getBackgroundImage();
        int backgroundColor = Config.getBackgroundColor();

        NERtcLiveStreamLayout layout = new NERtcLiveStreamLayout();
        layout.width = rectLayout.width();
        layout.height = rectLayout.height();
        layout.backgroundImg = backgroundImage;
        layout.backgroundColor = backgroundColor;
        return layout;
    }

    private static ArrayList<NERtcLiveStreamUserTranscoding> createUserTranscodingList(List<Long> userIds, Rect[] rectUsers) {
        ArrayList<NERtcLiveStreamUserTranscoding> userTranscodingList = new ArrayList<>();
        for (int i = 0; i < userIds.size() && i < rectUsers.length; i++) {
            NERtcLiveStreamUserTranscoding userTranscoding = new NERtcLiveStreamUserTranscoding();
            userTranscoding.uid = userIds.get(i);
            userTranscoding.audioPush = true;
            userTranscoding.videoPush = true;
            userTranscoding.adaption = kNERtcLsModeVideoScaleCropFill;
            Rect rectUser = rectUsers[i];
            userTranscoding.x = rectUser.left;
            userTranscoding.y = rectUser.top;
            userTranscoding.width = rectUser.width();
            userTranscoding.height = rectUser.height();
            userTranscodingList.add(userTranscoding);
        }
        return userTranscodingList;
    }
}
