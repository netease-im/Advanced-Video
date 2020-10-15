# VideoStream-iOS-ObjC

该示例项目演示了如何快速集成[网易云信](https://yunxin.163.com)新一代（G2）音视频SDK，实现旁路推流功能。

**旁路推流**：

将正在进行实时音视频通话时频道的画面同步到云端进行云端混流，并将混流后的频道直播流推流给第三方CDN或者云信视频直播。

 **在这个示例项目中包含了以下功能**：

- 加入房间
- 配置推流地址
- 开始、停止旁路推流
- 离开房间

## 环境准备

- Xcode 10.0+
- iOS真机设备
- 支持模拟器运行，但是部分功能无法使用

## 运行示例项目

获取**APPkey**

- 首先在 [网易云信](https://yunxin.163.com) 注册账号

- 于「应用」一栏中创建您的项目

- 于应用详情页中找到「App Key」管理即可查看Appkey

- 此时您的Appkey缺乏权限，申请试用「音视频通话」，专属客户经理会联系您并为您开通权限

- 将AppKey填写进NTESConfig.h

```objective-c
#define kAppKey @"<#AppKey#>"
```

**获取推流地址**

- 申请试用「直播」产品，专户客户经理会为您提供用于体验的推拉流地址
- 将推流地址填写进NTESConfig.h

```
#define kStreamURL @"<#推流地址#>"
```

**集成实时音视频**SDK

1. 进入**VideoStream/VideoStream-iOS-ObjC**，执行`Pod install`
2. 使用Xcode打开VideoStream-iOS-ObjCxcworkspace，连接iPhone/iPad测试设备，设置有效的开发者签名后即可运行

## 功能实现

1. 引擎初始化，配置音视频相关参数。

   ```objective-c
   - (void)setupRTCEngine
   {
       NSAssert(![kAppKey isEqualToString:@"<#AppKey#>"], @"请设置AppKey");
       NERtcEngine *coreEngine = [NERtcEngine sharedEngine];
       NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
       context.engineDelegate = self;
       context.appKey = kAppKey;
       [coreEngine setupEngineWithContext:context];
       [coreEngine enableLocalAudio:YES];
       [coreEngine enableLocalVideo:YES];
       [coreEngine setParameters:@{kNERtcKeyPublishSelfStreamEnabled: @YES}]; // 打开推流
   }
   ```

2. 加入和离开房间，调用SDK接口加入和退出音视频房间。

   ```objective-c
   /* ---- 加入房间 ---- */
   NSString *channelName = "频道名称"
   uint64_t userId = "用户ID"
   [NERtcEngine.sharedEngine joinChannelWithToken:@""
                                      channelName:channelName
                                            myUid:userId
           completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
           if (error) {
   						//加入失败
           } else {
               //加入成功，添加推流任务。
           }
   }];
   
   /* ---- 离开房间 ---- */
   [NERtcEngine.sharedEngine leaveChannel];
   ```

3. 添加推流任务。

   ```objective-c
   - (void)addLiveStream:(NSString *)streamURL
   {
       NSAssert(![streamURL isEqualToString:@"<#推流地址#>"], @"请设置推流地址");
       self.liveStreamTask = [[NERtcLiveStreamTaskInfo alloc] init];
       self.liveStreamTask.taskID = [NSString stringWithFormat:@"%d",arc4random()/100];;
       self.liveStreamTask.streamURL = streamURL;
       self.liveStreamTask.lsMode = kNERtcLsModeVideo;
       NSInteger layoutWidth = 720;
       NSInteger layoutHeight = 1280;
       //设置整体布局
       NERtcLiveStreamLayout *layout = [[NERtcLiveStreamLayout alloc] init];
       layout.width = layoutWidth; //整体布局宽度
       layout.height = layoutHeight; //整体布局高度
       self.liveStreamTask.layout = layout;
       //设置推流用户的UI布局
       [self reloadUsers];
       
       int ret = [NERtcEngine.sharedEngine addLiveStreamTask:self.liveStreamTask
                                                  compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
           if (errorCode == 0) {
             //推流任务添加成功
           }else {
             //推流任务添加失败
           }
       }];
       if (ret != 0) {
           //推流任务添加失败
       }
   }
   ```

4. 更新推流任务。

   ```objective-c
   - (void)updateLiveStreamTask
   {
       int ret = [NERtcEngine.sharedEngine updateLiveStreamTask:self.liveStreamTask
                                                  compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
       if (errorCode == 0) {
             //推流任务添加成功
           }else {
             //推流任务添加失败
           }                                             
       }];
       if (ret != 0) {
         //更新失败
       }
   }
   ```

5. 移除推流任务。

   ```objective-c
   if (self.liveStreamTask) {
           __weak typeof(self)weakSelf = self;
           int ret = [NERtcEngine.sharedEngine removeLiveStreamTask:self.liveStreamTask.taskID compeltion:^(NSString * _Nonnull taskId, kNERtcLiveStreamError errorCode) {
               if (errorCode == 0) {
                 //移除成功
                   weakSelf.liveStreamTask = nil;
               }
           }];
        if (ret != 0) {
               NSLog(@"移除任务失败");
        }
   
       }
   ```
   
   6.多人视频画面布局设置。
   
   ```objective-c
   /// 设置服务端4人视频画面“田”字布局。
   /// 旁路推流是将多路视频流同步到云端并混合成一路流，客户端可以通过拉流地址获取到多人画面，此UI配置是指定服务端混流后各个画面的布局。
   - (void)reloadUsers
   {
       NSInteger layoutWidth = self.liveStreamTask.layout.width;
       NSInteger userWidth = 320;
       NSInteger userHeight = 480;
       NSInteger horizPadding = (layoutWidth-userWidth*2)/3;
       NSInteger vertPadding = 16;
       NSMutableArray *res = NSMutableArray.array;
       for (NSInteger i = 0; i < self.userList.count; i++) {
           NSInteger column = i % 2;
           NSInteger row = i / 2;
           NSNumber *userID = self.userList[i];
           NERtcLiveStreamUserTranscoding *userTranscoding = [[NERtcLiveStreamUserTranscoding alloc] init];
           userTranscoding.uid = userID.unsignedLongValue;
           userTranscoding.audioPush = YES;
           userTranscoding.videoPush = YES;
           userTranscoding.x = column == 0 ? horizPadding : horizPadding * 2 + userWidth;
           userTranscoding.y = vertPadding * (row + 1) + userHeight * row;
           userTranscoding.width = userWidth;
           userTranscoding.height = userHeight;
           userTranscoding.adaption = kNERtcLsModeVideoScaleCropFill;
           [res addObject:userTranscoding];
       }
       self.liveStreamTask.layout.users = [NSArray arrayWithArray:res];
   }
   ```
   
   
   
   
   
   