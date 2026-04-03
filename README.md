# 整点自动报时应用 - Android 10

## 项目概述
这是一个专为 Android 10 设计的整点自动报时应用，使用 Kotlin 开发，采用 MVVM 架构。

## 核心功能
- ✅ 每小时整点自动语音报时
- ✅ 系统原生 TTS 中文语音合成
- ✅ 前台服务保活（Android 10+ 要求）
- ✅ 精确闹钟调度（AlarmManager）
- ✅ 开机自启动支持
- ✅ 忽略电池优化请求
- ✅ 测试报时功能
- ✅ 报时记录保存

## 技术栈
- **语言**: Kotlin
- **架构**: MVVM + Jetpack Lifecycle
- **UI**: ViewBinding + Material Design
- **后台任务**: AlarmManager + Foreground Service
- **语音**: android.speech.tts.TextToSpeech
- **协程**: kotlinx-coroutines-android

## 项目结构
```
app/
├── src/main/
│   ├── java/com/example/hourlychime/
│   │   ├── MainActivity.kt              # 主界面
│   │   ├── viewmodel/
│   │   │   └── MainViewModel.kt         # ViewModel
│   │   ├── service/
│   │   │   └── ChimeForegroundService.kt # 前台服务
│   │   ├── receiver/
│   │   │   ├── HourlyChimeReceiver.kt   # 整点广播接收器
│   │   │   └── BootReceiver.kt          # 开机广播接收器
│   │   └── util/                        # 工具类
│   ├── res/
│   │   ├── layout/activity_main.xml     # 主界面布局
│   │   ├── values/
│   │   │   ├── strings.xml              # 字符串资源
│   │   │   └── themes.xml               # 主题样式
│   │   └── mipmap-*/                    # 应用图标
│   └── AndroidManifest.xml              # 应用清单
├── build.gradle                          # 模块构建配置
└── proguard-rules.pro                    # 混淆规则
```

## 关键权限说明
```xml
<!-- 前台服务权限 (Android 10+) -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />

<!-- 精确闹钟权限 -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />

<!-- 忽略电池优化 -->
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />

<!-- 开机自启动 -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

## 编译运行

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34 (API Level 34)
- 最低支持 Android 10 (API Level 29)

### 构建步骤
1. 用 Android Studio 打开项目
2. 同步 Gradle 文件
3. 连接 Android 10+ 设备或启动模拟器
4. 点击 Run 按钮

### 命令行构建
```bash
./gradlew assembleDebug
```

## 使用说明
1. 首次启动应用
2. 点击"允许后台运行"按钮，授予忽略电池优化权限
3. 打开"启用整点报时"开关
4. 应用将在每个整点自动播报时间
5. 可点击"测试报时"立即体验

## Android 10 特殊适配
- 使用 `FOREGROUND_SERVICE_MEDIA_PLAYBACK` 类型的前台服务
- 必须显示持久通知栏图标
- 需要用户手动授予精确闹钟权限（Android 12+）
- 建议用户关闭电池优化以保证后台运行

## 注意事项
- 首次使用需确保系统 TTS 引擎已安装中文语音包
- 部分厂商定制系统可能需要额外设置白名单
- 建议在测试设备上验证后台保活效果

## 后续优化方向
- [ ] 添加自定义报时声音选项
- [ ] 支持静音时段设置
- [ ] 添加多语言支持
- [ ] 增加桌面小部件
- [ ] 支持 Wear OS

## License
MIT License
