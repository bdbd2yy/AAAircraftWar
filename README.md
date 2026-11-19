# AAAircraftWar

AAAircraftWar 是使用 Java、SurfaceView 和 Canvas 实现的原生 Android 飞机射击游戏。

## 游戏玩法

- 拖动屏幕控制飞机移动，飞机会自动射击。
- 击毁敌机获得分数，拾取道具可恢复生命、增强火力或影响场上的敌机。
- 游戏结束后轻触屏幕重新开始。切到后台时暂停，返回前台后继续。
- 游戏画面会根据屏幕尺寸等比例缩放。

## 开发与运行

使用 Android Studio 打开仓库，配置 Android SDK Platform 37，选择 API 30 或更高版本的设备并点击 **Run**。终端的 `JAVA_HOME` 应与 Android Studio 的 Gradle JDK 一致。

```sh
./gradlew assembleDebug testDebugUnitTest lintDebug
./gradlew connectedDebugAndroidTest
```

第一条命令构建调试 APK、运行本地单元测试并执行 Android Lint。第二条命令需要已连接的模拟器或设备。APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。

## 项目文档

- [游戏架构与验证](docs/project/gameplay-and-testing.md)
