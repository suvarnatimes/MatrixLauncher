# MatrixLauncher

> A production-ready, battery-efficient, minimalist Android Launcher with a retro-futuristic **dot-matrix / LED grid** aesthetic.

![Build Status](https://github.com/suvarnatimes/MatrixLauncher/actions/workflows/build.yml/badge.svg)

---

## Download Latest Test APK

Go to the [**Releases**](https://github.com/suvarnatimes/MatrixLauncher/releases) page and download the latest `.apk` file to test on your Android phone.

---

## Features
- `5×7` retro LED dot-matrix clock with battery status bar
- Universal search bar with in-line arithmetic calculator
- Dynamic Android App Shortcuts on long-press
- Fuzzy search across all apps
- Weather & upcoming calendar event glance
- CRT scanline shader (Android 13+ / AGSL)
- Mindful pause countdown for distraction apps
- Home scratchpad sticky note
- Hidden apps with biometric protection
- Full gesture customisation (swipe up/down/left/right, double-tap)
- JSON config export/import
- Multi-profile & Work profile support

## Tech Stack
- **Language**: Kotlin 2.x
- **UI**: Jetpack Compose + Material 3
- **Architecture**: Clean Architecture + MVI
- **DI**: Hilt
- **DB**: Room + DataStore
- **Graphics**: AGSL RuntimeShader (API 33+), Canvas fallback

## Build Instructions
```bash
# Clone the project
git clone https://github.com/suvarnatimes/MatrixLauncher.git
cd MatrixLauncher

# Build debug APK
./gradlew assembleDebug

# Install to connected USB phone
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## License
MIT
