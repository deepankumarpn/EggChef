#!/bin/bash

# Script to uninstall and install all variants of EggChef
# Usage: ./install_all_variants.sh

# Add Android SDK platform-tools to PATH for adb
if [ -d "$HOME/Library/Android/sdk/platform-tools" ]; then
    export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"
elif [ -d "/Users/$USER/Library/Android/sdk/platform-tools" ]; then
    export PATH="/Users/$USER/Library/Android/sdk/platform-tools:$PATH"
fi

PACKAGE_BASE="deepankumarpn.github.io.eggchef"

# Auto-detect JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    # Try macOS java_home utility first
    if command -v /usr/libexec/java_home &> /dev/null; then
        JAVA_HOME_DETECTED=$(/usr/libexec/java_home 2>/dev/null)
        if [ -n "$JAVA_HOME_DETECTED" ] && [ -d "$JAVA_HOME_DETECTED" ]; then
            export JAVA_HOME="$JAVA_HOME_DETECTED"
            echo "Auto-detected JAVA_HOME: $JAVA_HOME"
        fi
    fi
    # If still not set, try common paths
    if [ -z "$JAVA_HOME" ]; then
        for java_path in \
            "/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
            "/Applications/Android Studio.app/Contents/jre/Contents/Home" \
            "$PROGRAMFILES/Android/Android Studio/jbr" \
            "/c/Program Files/Android/Android Studio/jbr" \
            "$PROGRAMFILES/Android/Android Studio/jre" \
            "/c/Program Files/Android/Android Studio/jre" \
            /usr/lib/jvm/java-* \
            /usr/local/opt/openjdk/libexec/openjdk.jdk/Contents/Home \
            /Library/Java/JavaVirtualMachines/*/Contents/Home; do
            if [ -d "$java_path" ]; then
                export JAVA_HOME="$java_path"
                echo "Auto-detected JAVA_HOME: $JAVA_HOME"
                break
            fi
        done
    fi
    if [ -z "$JAVA_HOME" ]; then
        echo "ERROR: Could not find Java installation. Please set JAVA_HOME."
        exit 1
    fi
fi

echo "========================================="
echo "Uninstalling and Installing All Variants"
echo "========================================="

# Check if device is connected
if ! adb devices | tr -d '\r' | grep -q "device$"; then
    echo "❌ No Android device connected!"
    echo "Please connect a device or start an emulator."
    exit 1
fi

echo "✓ Device connected"
echo ""

# Step 1: Uninstall all variants
echo "Step 1: Uninstalling all variants..."
echo "-----------------------------------"

uninstall_package() {
    local package=$1
    local name=$2
    echo -n "Uninstalling $name ($package)... "
    if adb uninstall "$package" > /dev/null 2>&1; then
        echo "✓ Done"
    else
        echo "⚠ Not installed or failed"
    fi
}

uninstall_package "${PACKAGE_BASE}.debug" "Debug"
uninstall_package "${PACKAGE_BASE}" "Release"

echo ""
echo "Step 2: Building all variants..."
echo "--------------------------------"
echo "Running: ./gradlew assembleDebug assembleRelease"
./gradlew assembleDebug assembleRelease

if [ $? -ne 0 ]; then
    echo "❌ Build failed! Please fix the errors and try again."
    exit 1
fi

echo ""
echo "✓ Build completed successfully"
echo ""

# Debug: Show what APKs were actually created
echo "APKs found in build directory:"
find app/build/outputs/apk -name "*.apk" -type f | while read apk; do
    echo "  - $apk"
done
echo ""

echo "Step 3: Installing all variants..."
echo "----------------------------------"
success_count=0
failed_count=0

install_variant() {
    local package=$1
    local name=$2
    local apk_dir=$3

    echo "Processing $name..."
    echo "  Package: $package"
    echo "  APK directory: $apk_dir"

    if [ ! -d "$apk_dir" ]; then
        echo "  ❌ APK directory not found!"
        echo ""
        return 1
    fi

    local apk_path=$(find "$apk_dir" -name "*.apk" -type f | head -1)

    if [ -z "$apk_path" ]; then
        echo "  ❌ No APK found in directory!"
        echo ""
        return 1
    fi

    echo "  ✓ APK found: $apk_path"
    echo -n "  Installing... "
    local install_output=$(adb install -r "$apk_path" 2>&1)
    local install_result=$?

    if [ $install_result -eq 0 ]; then
        echo "✓ Success"
        echo ""
        return 0
    else
        echo "❌ Failed"
        echo "  Error: $install_output"
        echo ""
        return 1
    fi
}

# Install all variants
install_variant "${PACKAGE_BASE}.debug" "Debug" "app/build/outputs/apk/debug"
[ $? -eq 0 ] && ((success_count++)) || ((failed_count++))

install_variant "${PACKAGE_BASE}" "Release" "app/build/outputs/apk/release"
[ $? -eq 0 ] && ((success_count++)) || ((failed_count++))

echo ""
echo "========================================="
echo "Summary:"
echo "  ✓ Successfully installed: $success_count"
if [ $failed_count -gt 0 ]; then
    echo "  ❌ Failed: $failed_count"
fi
echo "  Total variants: $((success_count + failed_count))"
echo "========================================="

# List installed variants
echo ""
echo "Installed variants on device:"
echo "-----------------------------"

check_installed() {
    local package=$1
    local name=$2
    if adb shell pm list packages | grep -q "$package"; then
        echo "  ✓ $name ($package)"
    fi
}

check_installed "${PACKAGE_BASE}.debug" "Debug"
check_installed "${PACKAGE_BASE}" "Release"

echo ""
echo "Done! 🎉"
