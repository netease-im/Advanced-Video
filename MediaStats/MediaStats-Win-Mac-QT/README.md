# MediaStats-Win-Mac-Qt

这个开源示例项目演示了如何快速集成 网易云信 新一代（G2）音视频 SDK，实现通话过程中数据统计分析功能。

## 环境准备，运行示例项目，一对一通话功能实现
1. 这个开源示例项目基于一对一视频通话，关于**环境准备**，**运行示例项目**，**功能实现**章节请参考[1对1音视频通话](https://github.com/netease-im/Basic-Video-Call/tree/master/One-to-One-Video/NERtcSample-1to1-Windows_macOS-Qt)




## 使用方式

如果需要使用质量透明的功能，需要通过

```c++
 @return
    - 0: 方法调用成功；
    - 其他: 方法调用失败。
    */
    virtual int setStatsObserver(IRtcMediaStatsObserver *observer) = 0;

```



## IRtcMediaStatsObserver 接口预览

```c++
  /** 当前通话统计回调。

    SDK 定期向 App 报告当前通话的统计信息，每 2 秒触发一次。

     @param stats NERTC 引擎统计数据: NERtcStats
     */
    virtual void onRtcStats(const NERtcStats &stats) {
        (void)stats;
    }    

    /** 本地音频流统计信息回调。

    该回调描述本地设备发送音频流的统计信息，每 2 秒触发一次。

     @param stats 本地音频流统计信息。详见 NERtcAudioSendStats.
     */
    virtual void onLocalAudioStats(const NERtcAudioSendStats &stats) {
        (void)stats;
    }

    /** 通话中远端音频流的统计信息回调。

     该回调描述远端用户在通话中端到端的音频流统计信息，每 2 秒触发一次。

     @param stats 每个远端用户音频统计信息的数组。详见 NERtcAudioRecvStats.
     @param user_count stats 数组的大小。
     */
    virtual void onRemoteAudioStats(const NERtcAudioRecvStats *stats, unsigned int user_count) {
        (void)stats;
        (void)user_count;
    }

    /** 本地视频流统计信息回调。

    该回调描述本地设备发送视频流的统计信息，每 2 秒触发一次。

     @param stats 本地视频流统计信息。详见 NERtcVideoSendStats.
     */
    virtual void onLocalVideoStats(const NERtcVideoSendStats &stats) {
        (void)stats;
    }

    /** 通话中远端视频流的统计信息回调。

     该回调描述远端用户在通话中端到端的视频流统计信息，每 2 秒触发一次。

     @param stats 每个远端用户视频统计信息的数组。详见 NERtcVideoRecvStats.
     @param user_count stats 数组的大小。
     */
    virtual void onRemoteVideoStats(const NERtcVideoRecvStats *stats, unsigned int user_count) {
        (void)stats;
        (void)user_count;
    }

    /** 通话中每个用户的网络上下行质量报告回调。

     该回调描述每个用户在通话中的网络状态，每 2 秒触发一次，只上报状态有变更的成员。

     @param infos 每个用户 ID 和网络上下行质量信息的数组: NERtcNetworkQualityInfo
     @param user_count infos 数组的大小，即用户数。
     */
    virtual void onNetworkQuality(const NERtcNetworkQualityInfo *infos, unsigned int user_count) {
        (void)infos;
        (void)user_count;
    }
```


