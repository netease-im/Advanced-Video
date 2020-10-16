# NERtcSample-VideoStream-Android-Java

## 功能介绍

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现视频推流。

## 环境准备，运行示例项目，多人视频通话功能实现

这个开源示例项目基于多人视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[多人视频通话](https://github.com/netease-im/Basic-Video-Call/blob/master/Group-Video/NERtcSample-GroupVideoCall-Android-Java/README.md)

## 功能实现

### 管理推流任务

推流任务以 **String** 作为唯一标识，以 **NERtcLiveStreamTaskInfo** 作为信息

#### 管理推流 API

| api | usage |
| - | - |
| addLiveStreamTask | 添加推流任务 |
| updateLiveStreamTask | 更新推流任务 |
| removeLiveStreamTask | 删除推流任务 |

#### 推流配置

创建推流任务信息

```java
    private static NERtcLiveStreamTaskInfo createLiveStreamTask(String taskId, String pushUrl) {
        NERtcLiveStreamTaskInfo task = new NERtcLiveStreamTaskInfo();
        task.taskId = taskId; // 推流任务ID
        task.url = pushUrl; // 直播推流地址
        task.serverRecordEnabled = false; // 关闭服务器录制功能
        task.liveMode = kNERtcLsModeVideo; // 直播推流视频模式
        return task;
    }
```

创建推流任务视频布局

```java
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
```

创建推流任务成员视频布局

```java
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
```

Sample code中的 **Config** 类提供了推流配置的参数，展示了一个2 x 2的布局配置。

```java
    /**
     *              2 x 2 layout
     *
     *               720x1280
     * \---------------------------------\
     * \      320x480        320x480     \
     * \   \----------\   \----------\   \
     * \   \          \   \          \   \
     * \   \          \   \          \   \
     * \   \----------\   \----------\   \
     * \      320x480        320x480     \
     * \   \----------\   \----------\   \
     * \   \          \   \          \   \
     * \   \          \   \          \   \
     * \   \----------\   \----------\   \
     * \                                 \
     * \---------------------------------\
     */

    // 获取推流视频Rect，仅使用宽高
    static Rect getRectLayout() {}

    // 获取用户视频Rect列表，位置相对于推流视频
    static Rect[] getRectUsers() {}

    // 获取背景色
    static int getBackgroundColor() {}

    // 获取背景图
    static NERtcLiveStreamImageInfo getBackgroundImage() {}
```

#### 开启推流

```java
    // 推流任务ID
    private final String taskId;

    // 推流用户
    private final List<Long> userIds = new ArrayList<>();

    // 当前推流任务
    private NERtcLiveStreamTaskInfo task;

    public void start(String url) {
        task = createLiveStreamTask(taskId, url); // 创建推流配置
        task.layout = createLiveStreamLayout(); // 创建视频布局
        task.layout.userTranscodingList = createUserTranscodingList(userIds, Config.getRectUsers()); // 创建用户视频布局

        // 添加推流任务
        int result = NERtcEx.getInstance().addLiveStreamTask(task, (taskId, result2) -> {
            if (result2 != 0) {
                // 添加失败
            }
        });
        if (result != 0) {
            // 添加失败
        }
    }
```

#### 关闭推流

```java
    public void stop() {
        // 删除推流任务
        int result = NERtcEx.getInstance().removeLiveStreamTask(taskId, (taskId, result2) -> {
            if (result2 != 0) {
                // 删除失败
            }
        });
        if (result != 0) {
            // 删除失败
        }
    }
```

#### 更新推流

当房间内的用户视频开启，关闭，离开时，需要更新推流任务，

```java
    public void update(long userId, boolean add) {
        // 判断推流用户列表是否改变
    
        // 判断是否正在推流

        // 更新推流任务用户布局
        task.layout.userTranscodingList = createUserTranscodingList(userIds, Config.getRectUsers());
        // 更新推流任务
        int result = NERtcEx.getInstance().updateLiveStreamTask(task, (taskId, result2) -> {
            if (result2 != 0) {
                // 更新失败
            }
        });
        if (result != 0) {
            // 更新失败
        }
    }
```

#### 接收推流状态

方法 **NERtcCallbackEx.onLiveStreamState** 提供推流状态通知，状态定义在 **NERtcConstants.LiveStreamState**

```java
    @Override
    public void onLiveStreamState(String taskID, String url, int state) {
        if (state == NERtcConstants.LiveStreamState.STATE_PUSH_FAIL) {
            // 推流失败
        }
    }
```