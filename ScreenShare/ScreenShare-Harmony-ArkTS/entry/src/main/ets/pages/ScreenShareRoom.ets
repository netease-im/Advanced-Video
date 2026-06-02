import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import common from '@ohos.app.ability.common';
import { NERtcVideoView } from '@nertc/nertc_sdk';
import {
  ControllerDelegate,
  RemoteSlot,
  ScreenShareController
} from '../common/ScreenShareController';

interface RoomParams {
  uid: string;
  channelName: string;
}

class RoomDelegate implements ControllerDelegate {
  private page: ScreenShareRoom;

  constructor(page: ScreenShareRoom) {
    this.page = page;
  }

  onJoined(uid: bigint): void {
    this.page.onJoined(uid);
  }

  onLeft(): void {
    this.page.onLeft();
  }

  onRemoteSlotsChanged(slots: RemoteSlot[]): void {
    this.page.onRemoteSlotsChanged(slots);
  }

  onLocalScreenStatusChanged(started: boolean, statusText: string): void {
    this.page.onLocalScreenStatusChanged(started, statusText);
  }

  onLog(message: string): void {
    this.page.onLog(message);
  }
}

@Entry
@Component
struct ScreenShareRoom {
  private controller: ScreenShareController = ScreenShareController.getInstance();
  private localVideoCanvasId: string = 'local-video-view';
  private localScreenCanvasId: string = 'local-screen-view';
  private uid: string = '';
  private channelName: string = '';

  @State joined: boolean = false;
  @State localVideoStarted: boolean = false;
  @State localScreenStarted: boolean = false;
  @State statusText: string = '正在加入房间';
  @State remoteSlots: RemoteSlot[] = [];
  @State latestLog: string = '';

  private delegate: ControllerDelegate = new RoomDelegate(this);

  onJoined(uid: bigint): void {
    this.joined = true;
    this.statusText = `已加入房间，UID: ${uid.toString()}`;
  }

  onLeft(): void {
    router.back();
  }

  onRemoteSlotsChanged(slots: RemoteSlot[]): void {
    this.remoteSlots = slots;
  }

  onLocalScreenStatusChanged(started: boolean, statusText: string): void {
    this.localScreenStarted = started;
    this.statusText = statusText;
  }

  onLog(message: string): void {
    this.latestLog = message;
  }

  onPageShow(): void {
    const params = router.getParams() as RoomParams;
    this.uid = params.uid;
    this.channelName = params.channelName;
    this.controller.setDelegate(this.delegate);
    const context = getContext(this) as common.UIAbilityContext;
    this.controller.joinRoom(context, this.channelName, BigInt(this.uid));
  }

  aboutToDisappear(): void {
    this.controller.setDelegate(undefined);
  }

  onBackPress(): boolean {
    this.leaveRoom();
    return true;
  }

  private toggleLocalVideo(): void {
    this.localVideoStarted = this.controller.toggleLocalVideo();
  }

  private toggleScreenShare(): void {
    if (this.localScreenStarted) {
      this.localScreenStarted = this.controller.stopScreenShare();
    } else {
      const requested = this.controller.startScreenShare();
      if (requested) {
        this.statusText = '等待系统屏幕采集授权';
      }
    }
  }

  private leaveRoom(): void {
    this.controller.leaveRoom();
  }

  @Builder
  private VideoTile(title: string, canvasId: string, onLoad: () => void) {
    Stack({ alignContent: Alignment.BottomStart }) {
      NERtcVideoView({
        canvasId,
        onLoad,
        onDestroy: (): void => {
        }
      })
        .width('100%')
        .height('100%')

      Text(title)
        .fontSize(13)
        .fontColor(Color.White)
        .backgroundColor('#00000080')
        .padding({ left: 8, right: 8, top: 4, bottom: 4 })
        .borderRadius(4)
        .margin(8)
    }
    .width('100%')
    .aspectRatio(1.35)
    .backgroundColor('#111827')
    .borderRadius(8)
    .clip(true)
  }

  @Builder
  private RemoteTiles() {
    if (this.remoteSlots.length === 0) {
      Column() {
        Text('等待远端视频或屏幕共享')
          .fontSize(15)
          .fontColor('#64748B')
      }
      .width('100%')
      .height(120)
      .justifyContent(FlexAlign.Center)
      .backgroundColor('#F8FAFC')
      .borderRadius(8)
    } else {
      ForEach(this.remoteSlots, (slot: RemoteSlot) => {
        this.VideoTile(
          `${slot.streamType === 'screen' ? '远端屏幕' : '远端视频'} ${slot.uid.toString()}`,
          slot.canvasId,
          (): void => {
            this.controller.attachRemoteSlot(slot);
          }
        )
      }, (slot: RemoteSlot): string => `${slot.streamType}-${slot.uid.toString()}`)
    }
  }

  build() {
    Column() {
      Row() {
        Column() {
          Text(`房间: ${this.channelName}`)
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor('#162033')
          Text(this.statusText)
            .fontSize(13)
            .fontColor('#65758B')
            .margin({ top: 4 })
        }
        .alignItems(HorizontalAlign.Start)
        .layoutWeight(1)

        Button('离开')
          .height(36)
          .fontSize(14)
          .fontColor(Color.White)
          .backgroundColor('#B42318')
          .borderRadius(8)
          .onClick((): void => {
            this.leaveRoom();
          })
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 14, bottom: 12 })
      .backgroundColor(Color.White)

      Scroll() {
        Column({ space: 12 }) {
          Grid() {
            GridItem() {
              this.VideoTile('本地视频', this.localVideoCanvasId, (): void => {
                this.controller.setupLocalVideoCanvas(this.localVideoCanvasId);
              })
            }
            GridItem() {
              this.VideoTile('本地屏幕', this.localScreenCanvasId, (): void => {
                this.controller.setupLocalScreenCanvas(this.localScreenCanvasId);
              })
            }
          }
          .columnsTemplate('1fr 1fr')
          .columnsGap(12)
          .rowsGap(12)
          .width('100%')

          Column({ space: 12 }) {
            Text('远端画面')
              .fontSize(16)
              .fontWeight(FontWeight.Medium)
              .fontColor('#162033')
              .width('100%')
              .textAlign(TextAlign.Start)
            this.RemoteTiles()
          }
          .width('100%')
          .padding(14)
          .backgroundColor(Color.White)
          .borderRadius(8)

          if (this.latestLog.length > 0) {
            Text(this.latestLog)
              .fontSize(12)
              .fontColor('#64748B')
              .width('100%')
              .maxLines(2)
              .textOverflow({ overflow: TextOverflow.Ellipsis })
          }
        }
        .width('100%')
        .padding(16)
      }
      .layoutWeight(1)

      Row({ space: 10 }) {
        Button(this.localVideoStarted ? '关闭视频' : '开启视频')
          .layoutWeight(1)
          .height(46)
          .fontSize(15)
          .fontColor(Color.White)
          .backgroundColor(this.localVideoStarted ? '#475569' : '#176B87')
          .borderRadius(8)
          .enabled(this.joined)
          .onClick((): void => {
            this.toggleLocalVideo();
          })

        Button(this.localScreenStarted ? '停止共享' : '屏幕共享')
          .layoutWeight(1)
          .height(46)
          .fontSize(15)
          .fontColor(Color.White)
          .backgroundColor(this.localScreenStarted ? '#B42318' : '#1B7F5C')
          .borderRadius(8)
          .enabled(this.joined)
          .onClick((): void => {
            this.toggleScreenShare();
          })
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 12, bottom: 18 })
      .backgroundColor(Color.White)
    }
    .height('100%')
    .width('100%')
    .backgroundColor('#EEF4F7')
    .onAppear((): void => {
      if (this.uid.length === 0 || this.channelName.length === 0) {
        promptAction.showToast({ message: '房间参数缺失' });
      }
    })
  }
}
