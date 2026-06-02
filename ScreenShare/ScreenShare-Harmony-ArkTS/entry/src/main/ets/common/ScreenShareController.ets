import { NERtcCallbackEx, NERtcConstants, NERtcSDK } from '@nertc/nertc_sdk';
import promptAction from '@ohos.promptAction';
import common from '@ohos.app.ability.common';
import backgroundTaskManager from '@ohos.resourceschedule.backgroundTaskManager';
import wantAgent, { WantAgent } from '@ohos.app.ability.wantAgent';
import avSession from '@ohos.multimedia.avsession';
import { BusinessError } from '@ohos.base';
import { Config } from './Config';

const TAG: string = 'ScreenShareController';
const BUNDLE_NAME: string = 'com.netease.nertc.screenshare.harmony';

export interface RemoteSlot {
  uid: bigint;
  streamType: 'video' | 'screen';
  canvasId: string;
}

export interface ControllerDelegate {
  onJoined(uid: bigint): void;
  onLeft(): void;
  onRemoteSlotsChanged(slots: RemoteSlot[]): void;
  onLocalScreenStatusChanged(started: boolean, statusText: string): void;
  onLog(message: string): void;
}

class ScreenShareCallback extends NERtcCallbackEx {
  private controller: ScreenShareController;

  constructor(controller: ScreenShareController) {
    super();
    this.controller = controller;
  }

  onJoinChannel(result: number, channelId: bigint, elapsed: bigint, uid: bigint): void {
    this.controller.handleJoinChannel(result, channelId, elapsed, uid);
  }

  onLeaveChannel(result: number): void {
    this.controller.handleLeaveChannel(result);
  }

  onUserJoined(uid: bigint): void {
    this.controller.log(`远端用户加入: ${uid.toString()}`);
  }

  onUserLeave(uid: bigint, reason: number): void {
    this.controller.handleUserLeave(uid, reason);
  }

  onUserAudioStart(uid: bigint): void {
    this.controller.log(`远端音频开启: ${uid.toString()}`);
  }

  onUserAudioStop(uid: bigint): void {
    this.controller.log(`远端音频停止: ${uid.toString()}`);
  }

  onUserVideoStart(
    uid: bigint,
    maxProfile: NERtcConstants.NERtcVideoProfileType,
    streamType?: NERtcConstants.NERtcVideoStreamType
  ): void {
    if (streamType !== undefined && streamType !== NERtcConstants.NERtcVideoStreamType.kNERtcVideoStreamTypeMain) {
      return;
    }
    this.controller.handleRemoteVideoStart(uid);
  }

  onUserVideoStop(uid: bigint, streamType?: NERtcConstants.NERtcVideoStreamType): void {
    if (streamType !== undefined && streamType !== NERtcConstants.NERtcVideoStreamType.kNERtcVideoStreamTypeMain) {
      return;
    }
    this.controller.handleRemoteVideoStop(uid);
  }

  onUserSubStreamVideoStart(uid: bigint, maxProfile: NERtcConstants.NERtcVideoProfileType): void {
    this.controller.handleRemoteScreenStart(uid);
  }

  onUserSubStreamVideoStop(uid: bigint): void {
    this.controller.handleRemoteScreenStop(uid);
  }

  onScreenCaptureStatusChanged(status: NERtcConstants.NERtcScreenCaptureStatus): void {
    this.controller.handleScreenCaptureStatus(status);
  }

  onDisconnect(reason: number): void {
    this.controller.log(`SDK 断开连接: ${reason}`);
    this.controller.releaseAfterDisconnect();
  }

  onClientRoleChange(oldRole: number, newRole: number): void {
    this.controller.log(`角色变化: ${oldRole} -> ${newRole}`);
  }

  onRecvSEIMsg(userId: bigint, data: Uint8Array, dataSize: number): void {
  }

  onError(code: number): void {
    this.controller.log(`SDK 错误: ${code}`);
  }

  onWarning(code: number): void {
    this.controller.log(`SDK 警告: ${code}`);
  }
}

export class ScreenShareController {
  private static instance: ScreenShareController = new ScreenShareController();
  private delegate?: ControllerDelegate;
  private initialized: boolean = false;
  private joined: boolean = false;
  private localVideoEnabled: boolean = false;
  private localVideoPausedForBackground: boolean = false;
  private localScreenEnabled: boolean = false;
  private localScreenRequested: boolean = false;
  private callback: ScreenShareCallback = new ScreenShareCallback(this);
  private remoteSlots: RemoteSlot[] = [];
  private context?: common.UIAbilityContext;
  private mediaSession: avSession.AVSession | null = null;
  private backgroundRunning: boolean = false;
  private backgroundStarting: boolean = false;
  private backgroundStopPending: boolean = false;

  static getInstance(): ScreenShareController {
    return ScreenShareController.instance;
  }

  setDelegate(delegate?: ControllerDelegate): void {
    this.delegate = delegate;
    this.notifyRemoteSlotsChanged();
  }

  init(context: common.UIAbilityContext): boolean {
    this.context = context;
    if (this.initialized) {
      return true;
    }

    if (Config.APP_KEY.indexOf('请填入') >= 0 || Config.APP_KEY.length === 0) {
      this.toast('请先在 Config.ets 中填写 RTC APP_KEY');
      return false;
    }

    const option: NERtcConstants.NERtcOption = {
      logLevel: NERtcConstants.LogLevel.INFO
    };
    try {
      NERtcSDK.getInstance().init(context, Config.APP_KEY, this.callback, option);
      NERtcSDK.getInstance().enableLocalAudio(true);
      this.initialized = true;
      this.log('SDK 初始化完成');
      return true;
    } catch (err) {
      this.toast(`SDK 初始化失败: ${JSON.stringify(err)}`);
      return false;
    }
  }

  joinRoom(context: common.UIAbilityContext, channelName: string, uid: bigint): boolean {
    if (!this.init(context)) {
      return false;
    }
    if (this.joined) {
      this.toast('已经在房间中');
      return true;
    }

    const ret = NERtcSDK.getInstance().joinChannel(Config.TOKEN, channelName, uid);
    this.log(`joinChannel ret: ${ret}`);
    if (ret !== NERtcConstants.ErrorCode.NO_ERROR) {
      this.toast(`加入房间失败: ${ret}`);
      return false;
    }
    return true;
  }

  setupLocalVideoCanvas(canvasId: string): void {
    if (!this.initialized || canvasId.length === 0) {
      return;
    }
    const ret = NERtcSDK.getInstance().setupLocalVideoCanvas(this.createCanvas(canvasId));
    this.log(`setupLocalVideoCanvas ret: ${ret}`);
  }

  setupLocalScreenCanvas(canvasId: string): void {
    if (!this.initialized || canvasId.length === 0) {
      return;
    }
    const ret = NERtcSDK.getInstance().setupLocalSubStreamVideoCanvas(this.createCanvas(canvasId));
    this.log(`setupLocalSubStreamVideoCanvas ret: ${ret}`);
  }

  toggleLocalVideo(): boolean {
    if (!this.joined) {
      this.toast('请先加入房间');
      return this.localVideoEnabled;
    }

    const next = !this.localVideoEnabled;
    const enableRet = this.setLocalVideoCapture(next);
    if (enableRet === NERtcConstants.ErrorCode.NO_ERROR) {
      this.localVideoEnabled = next;
      this.localVideoPausedForBackground = false;
    } else {
      this.toast(`本地视频操作失败: ${enableRet}`);
    }
    return this.localVideoEnabled;
  }

  handleAppBackground(): void {
    if (!this.initialized || !this.joined || !this.localVideoEnabled || this.localVideoPausedForBackground) {
      return;
    }

    const ret = this.setLocalVideoCapture(false);
    if (ret === NERtcConstants.ErrorCode.NO_ERROR) {
      this.localVideoPausedForBackground = true;
      this.log('app background, local camera paused');
    } else {
      this.log(`app background, pause local camera failed: ${ret}`);
    }
  }

  handleAppForeground(): void {
    if (!this.initialized || !this.joined || !this.localVideoEnabled || !this.localVideoPausedForBackground) {
      return;
    }

    const ret = this.setLocalVideoCapture(true);
    if (ret === NERtcConstants.ErrorCode.NO_ERROR) {
      this.localVideoPausedForBackground = false;
      this.log('app foreground, local camera resumed');
    } else {
      this.log(`app foreground, resume local camera failed: ${ret}`);
    }
  }

  startScreenShare(): boolean {
    if (!this.joined) {
      this.toast('请先加入房间');
      return false;
    }

    this.enableBackgroundRunning(true);
    NERtcSDK.getInstance().setupLocalSubStreamVideoCanvas(this.createCanvas('local-screen-view'));

    const config = new NERtcConstants.NERtcScreenConfiguration();
    config.maxProfile = NERtcConstants.NERtcVideoProfileType.kNERtcVideoProfileHD1080P;
    config.framerate = NERtcConstants.NERtcVideoFrameRate.kNERtcVideoFrameRateFps15;
    config.minFramerate = 0;
    config.bitrate = 0;
    config.minBitrate = 0;
    config.contentPrefer = NERtcConstants.NERtcSubStreamContentPrefer.kNERtcSubStreamContentPreferMotion;
    config.degradationPreference = NERtcConstants.NERtcDegradationPreference.kNERtcDegradationDefault;
    config.preferColorSpaceRange =
      NERtcConstants.NERtcVideoColorSpaceRange.VIDEO_COLOR_SPACE_RANGE_INVALID;

    const ret = NERtcSDK.getInstance().startScreenCapture(config);
    this.log(`startScreenCapture ret: ${ret}`);
    if (ret !== NERtcConstants.ErrorCode.NO_ERROR) {
      this.enableBackgroundRunning(false);
      this.toast(`开启屏幕共享失败: ${ret}`);
      return false;
    }
    this.localScreenRequested = true;
    return true;
  }

  stopScreenShare(): boolean {
    if (!this.initialized) {
      return false;
    }
    const ret = NERtcSDK.getInstance().stopScreenCapture();
    this.log(`stopScreenCapture ret: ${ret}`);
    if (ret === NERtcConstants.ErrorCode.NO_ERROR) {
      this.localScreenEnabled = false;
      this.localScreenRequested = false;
      NERtcSDK.getInstance().setupLocalSubStreamVideoCanvas(null);
      this.enableBackgroundRunning(false);
      this.delegate?.onLocalScreenStatusChanged(false, '屏幕共享已停止');
      return true;
    }
    this.toast(`停止屏幕共享失败: ${ret}`);
    return this.localScreenEnabled;
  }

  leaveRoom(): void {
    if (!this.initialized) {
      this.release();
      return;
    }
    if (this.localScreenEnabled || this.localScreenRequested) {
      NERtcSDK.getInstance().stopScreenCapture();
      NERtcSDK.getInstance().setupLocalSubStreamVideoCanvas(null);
      this.localScreenEnabled = false;
      this.localScreenRequested = false;
      this.enableBackgroundRunning(false);
    }
    if (this.localVideoEnabled) {
      NERtcSDK.getInstance().enableLocalVideo(false, NERtcConstants.NERtcVideoStreamType.kNERtcVideoStreamTypeMain);
      NERtcSDK.getInstance().setupLocalVideoCanvas(null);
      this.localVideoEnabled = false;
      this.localVideoPausedForBackground = false;
    }
    if (this.joined) {
      const ret = NERtcSDK.getInstance().leaveChannel();
      this.log(`leaveChannel ret: ${ret}`);
    } else {
      this.release();
    }
  }

  release(): void {
    try {
      NERtcSDK.getInstance().release();
    } catch (err) {
      console.error(TAG, `release failed: ${JSON.stringify(err)}`);
    }
    this.initialized = false;
    this.joined = false;
    this.localVideoEnabled = false;
    this.localVideoPausedForBackground = false;
    this.localScreenEnabled = false;
    this.localScreenRequested = false;
    this.remoteSlots = [];
    this.enableBackgroundRunning(false);
    this.notifyRemoteSlotsChanged();
  }

  releaseAfterDisconnect(): void {
    this.release();
    this.delegate?.onLeft();
  }

  handleJoinChannel(result: number, channelId: bigint, elapsed: bigint, uid: bigint): void {
    this.log(`onJoinChannel result:${result}, channel:${channelId.toString()}, uid:${uid.toString()}`);
    if (result === NERtcConstants.ErrorCode.NO_ERROR) {
      this.joined = true;
      this.delegate?.onJoined(uid);
    } else {
      this.toast(`加入房间失败: ${result}`);
    }
  }

  handleLeaveChannel(result: number): void {
    this.log(`onLeaveChannel result:${result}`);
    this.release();
    this.delegate?.onLeft();
  }

  handleUserLeave(uid: bigint, reason: number): void {
    this.log(`远端用户离开: ${uid.toString()}, reason:${reason}`);
    this.removeRemoteSlot(uid, 'video');
    this.removeRemoteSlot(uid, 'screen');
  }

  handleRemoteVideoStart(uid: bigint): void {
    const slot = this.ensureRemoteSlot(uid, 'video');
    this.log(`remote video start uid:${uid.toString()}, canvas:${slot.canvasId}`);
  }

  handleRemoteVideoStop(uid: bigint): void {
    NERtcSDK.getInstance().subscribeRemoteVideo(
      uid,
      false,
      NERtcConstants.NERtcRemoteVideoSubscribeType.kNERtcRemoteVideoSubscribeTypeHigh,
      NERtcConstants.NERtcVideoStreamType.kNERtcVideoStreamTypeMain
    );
    NERtcSDK.getInstance().setupRemoteVideoCanvas(null, uid);
    this.removeRemoteSlot(uid, 'video');
  }

  handleRemoteScreenStart(uid: bigint): void {
    const slot = this.ensureRemoteSlot(uid, 'screen');
    this.log(`remote screen start uid:${uid.toString()}, canvas:${slot.canvasId}`);
  }

  handleRemoteScreenStop(uid: bigint): void {
    NERtcSDK.getInstance().subscribeRemoteSubStreamVideo(uid, false);
    NERtcSDK.getInstance().setupRemoteSubStreamVideoCanvas(null, uid);
    this.removeRemoteSlot(uid, 'screen');
  }

  handleScreenCaptureStatus(status: NERtcConstants.NERtcScreenCaptureStatus): void {
    let statusText = '屏幕共享状态变化';
    if (status === NERtcConstants.NERtcScreenCaptureStatus.START) {
      this.localScreenEnabled = true;
      this.localScreenRequested = false;
      this.enableBackgroundRunning(true);
      statusText = '屏幕共享已开始';
    } else if (status === NERtcConstants.NERtcScreenCaptureStatus.STOP) {
      this.localScreenEnabled = false;
      this.localScreenRequested = false;
      statusText = '屏幕共享已停止';
      NERtcSDK.getInstance().setupLocalSubStreamVideoCanvas(null);
      this.enableBackgroundRunning(false);
    } else if (status === NERtcConstants.NERtcScreenCaptureStatus.CANCEL) {
      this.localScreenEnabled = false;
      this.localScreenRequested = false;
      statusText = '用户取消屏幕采集授权';
      NERtcSDK.getInstance().setupLocalSubStreamVideoCanvas(null);
      this.enableBackgroundRunning(false);
    } else if (status === NERtcConstants.NERtcScreenCaptureStatus.ABORT) {
      this.localScreenEnabled = false;
      this.localScreenRequested = false;
      statusText = '屏幕共享异常中断';
      NERtcSDK.getInstance().setupLocalSubStreamVideoCanvas(null);
      this.enableBackgroundRunning(false);
    }
    this.log(`onScreenCaptureStatusChanged: ${status}`);
    this.delegate?.onLocalScreenStatusChanged(this.localScreenEnabled, statusText);
    this.toast(statusText);
  }

  attachRemoteSlot(slot: RemoteSlot): void {
    if (!this.initialized) {
      return;
    }
    if (slot.streamType === 'screen') {
      const canvasRet = NERtcSDK.getInstance().setupRemoteSubStreamVideoCanvas(this.createCanvas(slot.canvasId), slot.uid);
      const subRet = NERtcSDK.getInstance().subscribeRemoteSubStreamVideo(slot.uid, true);
      this.log(`attach remote screen uid:${slot.uid.toString()}, canvas ret:${canvasRet}, subscribe ret:${subRet}`);
      return;
    }

    const canvasRet = NERtcSDK.getInstance().setupRemoteVideoCanvas(this.createCanvas(slot.canvasId), slot.uid);
    const subRet = NERtcSDK.getInstance().subscribeRemoteVideo(
      slot.uid,
      true,
      NERtcConstants.NERtcRemoteVideoSubscribeType.kNERtcRemoteVideoSubscribeTypeHigh,
      NERtcConstants.NERtcVideoStreamType.kNERtcVideoStreamTypeMain
    );
    this.log(`attach remote video uid:${slot.uid.toString()}, canvas ret:${canvasRet}, subscribe ret:${subRet}`);
  }

  log(message: string): void {
    console.info(TAG, message);
    this.delegate?.onLog(message);
  }

  private createCanvas(canvasId: string): NERtcConstants.NERtcVideoCanvas {
    const canvas = new NERtcConstants.NERtcVideoCanvas();
    canvas.canvasId = canvasId;
    canvas.scalingMode = NERtcConstants.NERtcVideoScalingMode.kNERtcVideoScaleFit;
    canvas.mirrorMode = NERtcConstants.NERtcVideoMirrorMode.kNERtcVideoMirrorModeDisabled;
    return canvas;
  }

  private setLocalVideoCapture(enable: boolean): number {
    const canvasRet = NERtcSDK.getInstance().setupLocalVideoCanvas(enable ? this.createCanvas('local-video-view') : null);
    const enableRet = NERtcSDK.getInstance().enableLocalVideo(
      enable,
      NERtcConstants.NERtcVideoStreamType.kNERtcVideoStreamTypeMain
    );
    this.log(`setupLocalVideoCanvas ret: ${canvasRet}, enableLocalVideo(${enable}) ret: ${enableRet}`);
    return enableRet;
  }

  private ensureRemoteSlot(uid: bigint, streamType: 'video' | 'screen'): RemoteSlot {
    const found = this.remoteSlots.find((slot: RemoteSlot) => slot.uid === uid && slot.streamType === streamType);
    if (found) {
      return found;
    }
    const slot: RemoteSlot = {
      uid,
      streamType,
      canvasId: `remote-${streamType}-${uid.toString()}`
    };
    this.remoteSlots.push(slot);
    this.notifyRemoteSlotsChanged();
    return slot;
  }

  private removeRemoteSlot(uid: bigint, streamType: 'video' | 'screen'): void {
    const nextSlots = this.remoteSlots.filter((slot: RemoteSlot) => {
      return !(slot.uid === uid && slot.streamType === streamType);
    });
    if (nextSlots.length !== this.remoteSlots.length) {
      this.remoteSlots = nextSlots;
      this.notifyRemoteSlotsChanged();
    }
  }

  private notifyRemoteSlotsChanged(): void {
    this.delegate?.onRemoteSlotsChanged(this.remoteSlots.slice());
  }

  private enableBackgroundRunning(enable: boolean): void {
    if (enable) {
      this.createAVSession();
      this.startBackgroundRunning();
      return;
    }
    this.stopBackgroundRunning();
    this.destroyAVSession();
  }

  private createAVSession(): void {
    if (!this.context || this.mediaSession) {
      return;
    }
    avSession.createAVSession(this.context, TAG, 'video').then((session: avSession.AVSession): void => {
      this.mediaSession = session;
      this.log('createAVSession succeed');
    }).catch((err: BusinessError): void => {
      console.error(TAG, `createAVSession failed, code:${err.code}, message:${err.message}`);
    });
  }

  private destroyAVSession(): void {
    if (!this.mediaSession) {
      return;
    }
    const session = this.mediaSession;
    session.deactivate().then((): Promise<void> => {
      return session.destroy();
    }).then((): void => {
      if (this.mediaSession === session) {
        this.mediaSession = null;
      }
      this.log('destroyAVSession succeed');
    }).catch((err: BusinessError): void => {
      console.error(TAG, `destroyAVSession failed, code:${err.code}, message:${err.message}`);
      if (this.mediaSession === session) {
        this.mediaSession = null;
      }
    });
  }

  private startBackgroundRunning(): void {
    if (!this.context || this.backgroundRunning || this.backgroundStarting) {
      return;
    }

    this.backgroundStarting = true;
    this.backgroundStopPending = false;
    const context = this.context;
    const wantAgentInfo: wantAgent.WantAgentInfo = {
      wants: [
        {
          bundleName: BUNDLE_NAME,
          abilityName: 'EntryAbility'
        }
      ],
      actionType: wantAgent.OperationType.START_ABILITY,
      requestCode: 0,
      wantAgentFlags: [wantAgent.WantAgentFlags.UPDATE_PRESENT_FLAG]
    };

    wantAgent.getWantAgent(wantAgentInfo).then((wantAgentObj: WantAgent): Promise<backgroundTaskManager.ContinuousTaskNotification> => {
      const backgroundModes: string[] = ['audioRecording', 'audioPlayback'];
      return backgroundTaskManager.startBackgroundRunning(context, backgroundModes, wantAgentObj);
    }).then((): void => {
      this.backgroundStarting = false;
      this.backgroundRunning = true;
      this.log('startBackgroundRunning succeed');
      if (this.backgroundStopPending) {
        this.stopBackgroundRunning();
      }
    }).catch((err: BusinessError): void => {
      this.backgroundStarting = false;
      this.backgroundRunning = false;
      this.backgroundStopPending = false;
      console.error(TAG, `startBackgroundRunning failed, code:${err.code}, message:${err.message}`);
      this.toast(`启动后台长时任务失败: ${err.code}`);
    });
  }

  private stopBackgroundRunning(): void {
    if (!this.context) {
      return;
    }
    if (this.backgroundStarting && !this.backgroundRunning) {
      this.backgroundStopPending = true;
      return;
    }
    if (!this.backgroundRunning) {
      this.backgroundStopPending = false;
      return;
    }

    this.backgroundRunning = false;
    this.backgroundStopPending = false;
    backgroundTaskManager.stopBackgroundRunning(this.context).then((): void => {
      this.log('stopBackgroundRunning succeed');
    }).catch((err: BusinessError): void => {
      console.error(TAG, `stopBackgroundRunning failed, code:${err.code}, message:${err.message}`);
    });
  }

  private toast(message: string): void {
    promptAction.showToast({
      message,
      duration: 2000
    });
  }
}
