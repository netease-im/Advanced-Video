import { AbilityConstant, UIAbility, Want } from '@kit.AbilityKit';
import hilog from '@ohos.hilog';
import window from '@ohos.window';
import { ScreenShareController } from '../common/ScreenShareController';

export default class EntryAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    hilog.info(0x0000, 'ScreenShareDemo', '%{public}s', 'Ability onCreate');
  }

  onDestroy(): void {
    hilog.info(0x0000, 'ScreenShareDemo', '%{public}s', 'Ability onDestroy');
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    windowStage.loadContent('pages/Home', (err): void => {
      if (err.code) {
        hilog.error(0x0000, 'ScreenShareDemo', 'loadContent failed: %{public}s', JSON.stringify(err));
      }
    });
    windowStage.getMainWindowSync().setWindowKeepScreenOn(true);
  }

  onWindowStageDestroy(): void {
    hilog.info(0x0000, 'ScreenShareDemo', '%{public}s', 'Ability onWindowStageDestroy');
  }

  onForeground(): void {
    hilog.info(0x0000, 'ScreenShareDemo', '%{public}s', 'Ability onForeground');
    ScreenShareController.getInstance().handleAppForeground();
  }

  onBackground(): void {
    hilog.info(0x0000, 'ScreenShareDemo', '%{public}s', 'Ability onBackground');
    ScreenShareController.getInstance().handleAppBackground();
  }
}
