#!/usr/bin/env pwsh

Write-Host "Complete Clean Reinstall of All Variants" -ForegroundColor Cyan
Write-Host "=========================================="
Write-Host ""

# Auto-detect JAVA_HOME if not set
if (-not $env:JAVA_HOME) {
    $javaSearchPaths = @(
        "$env:ProgramFiles\Android\Android Studio\jbr",
        "$env:ProgramFiles\Android\Android Studio\jre",
        "$env:ProgramFiles\Java\jdk*",
        "$env:ProgramFiles\Eclipse Adoptium\jdk*",
        "$env:ProgramFiles\Microsoft\jdk*"
    )
    foreach ($searchPath in $javaSearchPaths) {
        $found = Get-Item $searchPath -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($found) {
            $env:JAVA_HOME = $found.FullName
            Write-Host "Auto-detected JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Green
            break
        }
    }
    if (-not $env:JAVA_HOME) {
        Write-Host "ERROR: Could not find Java installation. Please set JAVA_HOME." -ForegroundColor Red
        exit 1
    }
}

# Check if device is connected
Write-Host "Checking for connected devices..."
$devices = adb devices 2>&1
$connected = @($devices -match "\bdevice\s*$")
if ($connected.Count -eq 0) {
    Write-Host "No device connected. Please start your emulator or connect a device." -ForegroundColor Red
    exit 1
}

Write-Host "Device connected" -ForegroundColor Green
Write-Host ""

# Step 1: Uninstall all variants
Write-Host "Step 1: Uninstalling all existing variants..."
& "$PSScriptRoot\uninstall_all_variants.ps1"
Write-Host ""

# Step 2: Clear launcher cache
Write-Host "Step 2: Clearing launcher cache..."
adb shell pm clear com.google.android.apps.nexuslauncher 2>$null
if ($LASTEXITCODE -ne 0) {
    adb shell pm clear com.android.launcher3 2>$null
}
if ($LASTEXITCODE -ne 0) {
    adb shell pm clear com.google.android.launcher 2>$null
}
if ($LASTEXITCODE -ne 0) {
    Write-Host "Could not clear launcher cache automatically" -ForegroundColor Yellow
}
Write-Host ""

# Step 3: Clean project
Write-Host "Step 3: Cleaning project..."

# Stop Gradle daemons first to release file locks
Write-Host "Stopping Gradle daemons..."
& .\gradlew.bat --stop 2>$null
Start-Sleep -Seconds 2

& .\gradlew.bat clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "Gradle clean failed, attempting manual cleanup..." -ForegroundColor Yellow
    if (Test-Path "app\build") {
        Remove-Item -Recurse -Force "app\build" -ErrorAction SilentlyContinue
    }
    if (Test-Path "build") {
        Remove-Item -Recurse -Force "build" -ErrorAction SilentlyContinue
    }
    if (Test-Path "app\build") {
        Write-Host "WARNING: Could not fully clean build directory. Close Android Studio and retry." -ForegroundColor Red
    } else {
        Write-Host "Manual cleanup successful" -ForegroundColor Green
    }
} else {
    Write-Host "Project cleaned" -ForegroundColor Green
}
Write-Host ""

# Step 4: Build APKs
Write-Host "Step 4: Building all variants..."
& .\gradlew.bat assembleDebug assembleRelease -x lint
if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed" -ForegroundColor Red
    exit 1
}
Write-Host "Build successful" -ForegroundColor Green
Write-Host ""

# Step 5: Install all variants
Write-Host "Step 5: Installing all variants..."
& "$PSScriptRoot\install_all_variants.ps1"
Write-Host ""

# Step 6: Restart launcher
Write-Host "Step 6: Restarting launcher to refresh app list..."
adb shell am force-stop com.google.android.apps.nexuslauncher 2>$null
if ($LASTEXITCODE -ne 0) {
    adb shell am force-stop com.android.launcher3 2>$null
}
if ($LASTEXITCODE -ne 0) {
    adb shell am force-stop com.google.android.launcher 2>$null
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host "Done! All variants reinstalled cleanly." -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""
Write-Host "If you still see old app names:" -ForegroundColor Yellow
Write-Host "   1. Manually restart your emulator/device"
Write-Host "   2. Or run: adb reboot"
Write-Host ""
