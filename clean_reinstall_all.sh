#!/bin/bash

echo "🧹 Complete Clean Reinstall of All Variants"
echo "=========================================="
echo ""

# Add Android SDK platform-tools to PATH for adb
if [ -d "$HOME/Library/Android/sdk/platform-tools" ]; then
    export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"
elif [ -d "/Users/$USER/Library/Android/sdk/platform-tools" ]; then
    export PATH="/Users/$USER/Library/Android/sdk/platform-tools:$PATH"
fi

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Auto-detect JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    # Try macOS java_home utility first
    if command -v /usr/libexec/java_home &> /dev/null; then
        JAVA_HOME_DETECTED=$(/usr/libexec/java_home 2>/dev/null)
        if [ -n "$JAVA_HOME_DETECTED" ] && [ -d "$JAVA_HOME_DETECTED" ]; then
            export JAVA_HOME="$JAVA_HOME_DETECTED"
            echo -e "${GREEN}Auto-detected JAVA_HOME: $JAVA_HOME${NC}"
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
                echo -e "${GREEN}Auto-detected JAVA_HOME: $JAVA_HOME${NC}"
                break
            fi
        done
    fi
    if [ -z "$JAVA_HOME" ]; then
        echo -e "${RED}ERROR: Could not find Java installation. Please set JAVA_HOME.${NC}"
        exit 1
    fi
fi

# Check if device is connected
echo "Checking for connected devices..."
DEVICE_COUNT=$(adb devices | tr -d '\r' | grep -v "List of devices" | grep "device$" | wc -l | tr -d ' ')

if [ "$DEVICE_COUNT" -eq "0" ]; then
    echo -e "${RED}❌ No device connected. Please start your emulator or connect a device.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Device connected${NC}"
echo ""

# Step 1: Uninstall all variants
echo "Step 1: Uninstalling all existing variants..."
./uninstall_all_variants.sh
echo ""

# Step 2: Clear launcher cache
echo "Step 2: Clearing launcher cache..."
adb shell pm clear com.google.android.apps.nexuslauncher 2>/dev/null || \
adb shell pm clear com.android.launcher3 2>/dev/null || \
adb shell pm clear com.google.android.launcher 2>/dev/null || \
echo -e "${YELLOW}⚠ Could not clear launcher cache automatically${NC}"
echo ""

# Step 3: Clean project
echo "Step 3: Cleaning project..."

# Stop Gradle daemons first to release file locks
echo "Stopping Gradle daemons..."
./gradlew --stop 2>/dev/null
sleep 2

if ! ./gradlew clean; then
    echo -e "${YELLOW}⚠ Gradle clean failed, attempting manual cleanup...${NC}"
    rm -rf app/build build
    if [ -d "app/build" ]; then
        echo -e "${RED}❌ WARNING: Could not fully clean build directory. Close Android Studio and retry.${NC}"
    else
        echo -e "${GREEN}✓ Manual cleanup successful${NC}"
    fi
else
    echo -e "${GREEN}✓ Project cleaned${NC}"
fi
echo ""

# Step 4: Build APKs
echo "Step 4: Building all variants..."
./gradlew assembleDebug assembleRelease -x lint
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Build successful${NC}"
echo ""

# Step 5: Install all variants
echo "Step 5: Installing all variants..."
./install_all_variants.sh
echo ""

# Step 6: Restart launcher
echo "Step 6: Restarting launcher to refresh app list..."
adb shell am force-stop com.google.android.apps.nexuslauncher 2>/dev/null || \
adb shell am force-stop com.android.launcher3 2>/dev/null || \
adb shell am force-stop com.google.android.launcher 2>/dev/null || \
echo -e "${YELLOW}⚠ Could not force-stop launcher${NC}"

echo ""
echo -e "${GREEN}=========================================="
echo -e "✨ Done! All variants reinstalled cleanly."
echo -e "==========================================${NC}"
echo ""
echo -e "${YELLOW}📱 If you still see old app names:${NC}"
echo "   1. Manually restart your emulator/device"
echo "   2. Or run: adb reboot"
echo ""

