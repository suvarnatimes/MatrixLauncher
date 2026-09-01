#!/bin/bash
set -e

echo "========================================================"
echo "    MatrixLauncher: CLI Build & USB Testing Script     "
echo "========================================================"

# Step 1: Check for Java
if ! command -v java &> /dev/null; then
    echo "[!] Java is not installed on this machine."
    echo "[>] To install OpenJDK 17 on Linux (Ubuntu/Debian), run:"
    echo "    sudo apt update && sudo apt install -y openjdk-17-jdk"
    exit 1
fi

echo "[✓] Java found: $(java -version 2>&1 | head -n 1)"

# Step 2: Set Android SDK path
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
mkdir -p "$ANDROID_HOME/licenses"

# Ensure standard SDK licenses are accepted
printf "\n24333f8a63cbd8324867c90d4f88eab7173a0b04\nd56f5187479451eabf01fb787143c00f31f437e1\n89337d125679718437f48a1c0bdafc7e043b5dd4\n" > "$ANDROID_HOME/licenses/android-sdk-license"
printf "\n84831b9409646a918e30573bab4c9c91346d8abd\n" > "$ANDROID_HOME/licenses/android-sdk-preview-license"

echo "[✓] Android SDK configured at: $ANDROID_HOME"

# Step 3: Build the Debug APK
echo ""
echo "[*] Building MatrixLauncher Debug APK with Gradle..."
chmod +x ./gradlew
./gradlew assembleDebug

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ ! -f "$APK_PATH" ]; then
    echo "[!] Build failed: APK not found at $APK_PATH"
    exit 1
fi

echo "[✓] APK successfully compiled: $APK_PATH ($(du -h "$APK_PATH" | cut -f1))"

# Step 4: Check ADB Device Connection
echo ""
echo "[*] Checking connected Android devices via USB..."
if ! command -v adb &> /dev/null; then
    echo "[!] ADB is not installed. Install it via: sudo apt install -y adb"
    exit 1
fi

DEVICE_COUNT=$(adb devices | grep -v "List" | grep "device$" | wc -l || true)

if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo "[!] No Android device detected with USB Debugging enabled."
    echo ""
    echo "--- How to Enable USB Debugging on your Phone ---"
    echo "1. On your phone: Open Settings -> About Phone."
    echo "2. Tap 'Build Number' 7 times until Developer Options is unlocked."
    echo "3. Go back to Settings -> System / Developer Options."
    echo "4. Enable 'USB Debugging' (and 'Install via USB' if on MIUI/ColorOS)."
    echo "5. Plug in your USB cable and select 'Always allow from this computer'."
    echo "6. Re-run this script: ./scripts/build_and_install.sh"
    exit 1
fi

echo "[✓] Found $DEVICE_COUNT connected device(s)!"

# Step 5: Install APK to phone
echo ""
echo "[*] Installing MatrixLauncher APK onto connected phone..."
adb install -r "$APK_PATH"

echo "[✓] Installed successfully!"

# Step 6: Launch MatrixLauncher on device
echo ""
echo "[*] Launching MatrixLauncher on your phone..."
adb shell am start -n com.matrixlauncher/.MainActivity

echo ""
echo "========================================================"
echo " [SUCCESS] MatrixLauncher is now running on your phone! "
echo "========================================================"
echo "Tips:"
echo " • To set as default launcher: Open Launcher Settings -> 'SET AS DEFAULT LAUNCHER'."
echo " • To view real-time logs: adb logcat -s MatrixLauncher:V"
