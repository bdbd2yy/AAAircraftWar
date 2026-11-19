# 游戏架构与验证

## 游戏行为

应用启动后直接进入简单模式。背景持续滚动，玩家拖动控制飞机并自动射击。击毁敌机获得分数，碰撞会扣减生命。生命耗尽后轻触屏幕即可开启新一局。切到后台会暂停游戏，返回前台后继续当前一局。Activity 重建时从新一局开始。

## 代码结构

- `MainActivity` 创建游戏视图，并将 Activity 的暂停和恢复事件转发给视图。
- `BaseGame` 通过 `SurfaceView` 和 `Canvas` 绘制画面，负责触摸输入和游戏线程。渲染与触摸使用相同的坐标变换。
- `GameEngine` 负责敌机生成、射击、碰撞、计分和道具效果。`EasyGame` 配置简单模式，`GameClock` 随游戏更新推进。
- `ImageManager` 从 `app/src/main/res/drawable/` 加载并缓存位图。游戏使用 `GameConfig` 定义的 512 × 768 逻辑画面。

## 验证

在仓库根目录运行 `./gradlew testDebugUnitTest lintDebug` 执行本地单元测试和 Android Lint。连接 API 30 或更高版本的设备后，运行 `./gradlew connectedDebugAndroidTest`。其中 `GameEngineTest` 覆盖计分、机体碰撞、出界清理、临时火力恢复、重开及敌机生成和射击周期。

手动验证时，拖动飞机到画面四周确认边界限制，击毁敌机后检查分数，耗尽生命后轻触重开，再切换应用前后台确认游戏暂停并恢复。
