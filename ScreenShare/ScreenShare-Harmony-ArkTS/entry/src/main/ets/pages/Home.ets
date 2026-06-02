import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import AbilityAccessCtrl from '@ohos.abilityAccessCtrl';
import common from '@ohos.app.ability.common';
import { BusinessError } from '@ohos.base';

@Entry
@Component
struct Home {
  @State uid: string = `${Math.floor(Math.random() * 90000) + 10000}`;
  @State channelName: string = '';

  aboutToAppear(): void {
    this.requestPermissions();
  }

  private requestPermissions(): void {
    const context = getContext(this) as common.UIAbilityContext;
    const atManager = AbilityAccessCtrl.createAtManager();
    atManager.requestPermissionsFromUser(context, [
      'ohos.permission.MICROPHONE',
      'ohos.permission.CAMERA'
    ]).then((): void => {
      console.info('ScreenShareHome', 'permissions requested');
    }).catch((err: BusinessError): void => {
      console.error('ScreenShareHome', `request permissions failed: ${err.code}, ${err.message}`);
    });
  }

  private joinRoom(): void {
    const trimmedUid = this.uid.trim();
    const trimmedChannel = this.channelName.trim();
    if (trimmedUid.length === 0) {
      promptAction.showToast({ message: '请输入用户 ID' });
      return;
    }
    if (trimmedChannel.length === 0) {
      promptAction.showToast({ message: '请输入房间号' });
      return;
    }

    const uidNumber = Number(trimmedUid);
    if (!Number.isInteger(uidNumber) || uidNumber < 0) {
      promptAction.showToast({ message: '用户 ID 必须为非负整数' });
      return;
    }

    router.pushUrl({
      url: 'pages/ScreenShareRoom',
      params: {
        uid: trimmedUid,
        channelName: trimmedChannel
      }
    });
  }

  build() {
    Row() {
      Column() {
        Text('屏幕共享')
          .fontSize(34)
          .fontWeight(FontWeight.Bold)
          .fontColor('#162033')
          .width('100%')
          .textAlign(TextAlign.Start)

        Text('NERTC HarmonyOS Demo')
          .fontSize(15)
          .fontColor('#65758B')
          .margin({ top: 8 })
          .width('100%')
          .textAlign(TextAlign.Start)

        Column() {
          Text('用户 ID')
            .fontSize(14)
            .fontColor('#334155')
            .width('100%')
            .textAlign(TextAlign.Start)
          TextInput({ text: this.uid, placeholder: '请输入用户 ID' })
            .type(InputType.Number)
            .height(48)
            .fontSize(16)
            .backgroundColor('#F8FAFC')
            .borderRadius(8)
            .margin({ top: 8 })
            .onChange((value: string): void => {
              this.uid = value;
            })

          Text('房间号')
            .fontSize(14)
            .fontColor('#334155')
            .width('100%')
            .textAlign(TextAlign.Start)
            .margin({ top: 20 })
          TextInput({ text: this.channelName, placeholder: '请输入房间号' })
            .height(48)
            .fontSize(16)
            .backgroundColor('#F8FAFC')
            .borderRadius(8)
            .margin({ top: 8 })
            .onChange((value: string): void => {
              this.channelName = value;
            })

          Button('加入房间')
            .width('100%')
            .height(50)
            .fontSize(17)
            .fontColor(Color.White)
            .backgroundColor('#176B87')
            .borderRadius(8)
            .margin({ top: 28 })
            .onClick((): void => {
              this.joinRoom();
            })
        }
        .width('100%')
        .padding(20)
        .backgroundColor(Color.White)
        .borderRadius(8)
        .margin({ top: 28 })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 60 })
    }
    .height('100%')
    .width('100%')
    .backgroundColor('#EEF4F7')
  }
}
