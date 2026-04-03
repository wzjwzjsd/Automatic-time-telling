# Android 编译环境说明

本项目使用 Docker 容器进行编译，确保编译环境的一致性和可重复性。

## 🐳 Android 编译容器

### 容器镜像信息

- **基础镜像**: Ubuntu 22.04
- **JDK 版本**: OpenJDK 17
- **Gradle 版本**: 8.2
- **Android SDK**: API Level 34
- **Build Tools**: 34.0.0
- **镜像大小**: 约 1.89GB

### 构建编译容器

```bash
# 在项目根目录执行
cd /home/fri/code
./build-android-container.sh
```

或者手动构建：

```bash
sudo docker build -t android-build:latest -f Dockerfile.android-build .
```

## 📦 编译项目

### 使用编译脚本（推荐）

```bash
cd /home/fri/code
./compile-android.sh
```

### 手动编译

```bash
cd /home/fri/code/Automatic-time-telling

# 使用 Docker 容器编译
sudo docker run --rm \
    -v $(pwd):/app:rw \
    -w /app \
    android-build:latest \
    gradle assembleDebug
```

### 编译输出

编译成功后，APK 文件位置：
```
/home/fri/code/Automatic-time-telling/app/build/outputs/apk/debug/app-debug.apk
```

## 🔧 技术栈

- **语言**: Kotlin
- **架构**: MVVM + Jetpack Lifecycle
- **UI**: ViewBinding + Material Design
- **后台任务**: AlarmManager + Foreground Service
- **语音**: android.speech.tts.TextToSpeech
- **协程**: kotlinx-coroutines-android

### 核心依赖

```gradle
// AndroidX
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.fragment:fragment-ktx:1.6.2'

// WorkManager
implementation 'androidx.work:work-runtime-ktx:2.9.0'

// Lifecycle
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
```

## ⚙️ 环境要求

### 系统要求

- Linux (Debian/Ubuntu)
- Docker 20.10+
- 至少 10GB 可用磁盘空间
- 至少 4GB 内存

### 网络配置

如果在中国大陆地区，需要配置 HTTP 代理来访问 Docker Hub 和 Gradle 仓库：

```bash
# 编辑 Docker 代理配置
sudo systemctl edit docker.service
```

添加以下内容：

```ini
[Service]
Environment="HTTP_PROXY=http://127.0.0.1:12334"
Environment="HTTPS_PROXY=http://127.0.0.1:12334"
Environment="NO_PROXY=localhost,127.0.0.1,.local"
```

重启 Docker：

```bash
sudo systemctl daemon-reload
sudo systemctl restart docker
```

## 🚀 快速开始

1. **克隆项目**
   ```bash
   git clone https://github.com/wzjwzjsd/Automatic-time-telling.git
   cd Automatic-time-telling
   ```

2. **构建编译容器**（如果还没有）
   ```bash
   cd /home/fri/code
   ./build-android-container.sh
   ```

3. **编译项目**
   ```bash
   ./compile-android.sh
   ```

4. **安装到设备**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

## 📝 常见问题

### Q: 编译失败，提示找不到某些类？

A: 检查 `app/build.gradle` 中的依赖是否完整，确保添加了所有必要的依赖项。

### Q: Docker 拉取镜像超时？

A: 确保已正确配置 HTTP 代理，或者尝试使用国内镜像源。

### Q: Gradle 下载依赖很慢？

A: 容器中已配置阿里云 Maven 镜像，如果仍然很慢，可以检查网络连接或代理设置。

### Q: 如何在容器外查看编译产物？

A: 编译产物在挂载的卷中，可以直接在宿主机的项目目录下找到。

## 📄 License

MIT License
