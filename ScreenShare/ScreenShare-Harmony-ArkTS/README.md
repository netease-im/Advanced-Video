# ScreenShare HarmonyOS ArkTS

这是 NERTC HarmonyOS 屏幕共享 demo，流程对齐同级 `ScreenShare-Android-Java`：

- 首页输入用户 ID 和房间号。
- 入房后可开启/关闭本地摄像头主流。
- 入房后可开启/停止屏幕共享辅流。
- 本端屏幕共享设置本地辅流画布预览。
- 收到远端主流/辅流回调后设置远端画布并订阅。

## 使用前配置

在 `entry/src/main/ets/common/Config.ets` 中填写业务自己的 RTC App Key：

```ts
export namespace Config {
  export const APP_KEY: string = "your app key";
  export const TOKEN: string = "";
}
```

如果控制台开启了安全模式，请把业务服务端生成的 Token 填到 `TOKEN`，或按业务需要替换成动态获取 Token 的逻辑。

## 工程说明

- `entry/oh-package.json5`：通过 OHPM 官方仓库依赖 NERTC HarmonyOS SDK。
- `entry/src/main/ets/pages/Home.ets`：输入用户 ID、房间号并跳转入房。
- `entry/src/main/ets/pages/ScreenShareRoom.ets`：通话 UI，本地视频、本地屏幕、远端主流/辅流画布。
- `entry/src/main/ets/common/ScreenShareController.ets`：SDK 初始化、入房、屏幕共享、远端订阅和回调处理。

## 构建验证

已用 DevEco Studio 自带 hvigor 构建通过：

```bash
/Applications/DevEco-Studio.app/Contents/tools/hvigor/bin/hvigorw --mode module -p product=default assembleHap --analyze=normal --parallel --incremental --daemon
```

当前工程未配置签名，命令行构建会跳过签名。真机安装时请在 DevEco Studio 中配置签名证书，或在 `build-profile.json5` 中补充 `signingConfigs`。
