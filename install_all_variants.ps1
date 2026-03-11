#!/usr/bin/env pwsh

# Script to uninstall and install all variants of EggChef
# Usage: .\install_all_variants.ps1

$PACKAGE_BASE = "deepankumarpn.github.io.eggchef"

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

Write-Host "========================================="
Write-Host "Uninstalling and Installing All Variants"
Write-Host "========================================="

# Check if device is connected
$devices = adb devices 2>&1
$connected = @($devices -match "\bdevice\s*$")
if ($connected.Count -eq 0) {
    Write-Host "No Android device connected!" -ForegroundColor Red
    Write-Host "Please connect a device or start an emulator."
    exit 1
}

Write-Host "Device connected" -ForegroundColor Green
Write-Host ""

# Step 1: Uninstall all variants
Write-Host "Step 1: Uninstalling all variants..."
Write-Host "-----------------------------------"

function Uninstall-Package {
    param($Package, $Name)
    Write-Host -NoNewline "Uninstalling $Name ($Package)... "
    $result = adb uninstall $Package 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Done" -ForegroundColor Green
    } else {
        Write-Host "Not installed or failed" -ForegroundColor Yellow
    }
}

Uninstall-Package -Package "$PACKAGE_BASE.debug" -Name "Debug"
Uninstall-Package -Package "$PACKAGE_BASE" -Name "Release"

Write-Host ""
Write-Host "Step 2: Building all variants..."
Write-Host "--------------------------------"
Write-Host "Running: ./gradlew assembleDebug assembleRelease"
& .\gradlew.bat assembleDebug assembleRelease

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed! Please fix the errors and try again." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Build completed successfully" -ForegroundColor Green
Write-Host ""

# Show what APKs were actually created
Write-Host "APKs found in build directory:"
Get-ChildItem -Path "app\build\outputs\apk" -Filter "*.apk" -Recurse | ForEach-Object {
    Write-Host "  - $($_.FullName)"
}
Write-Host ""

Write-Host "Step 3: Installing all variants..."
Write-Host "----------------------------------"
$successCount = 0
$failedCount = 0

function Install-Variant {
    param($Package, $Name, $ApkDir)

    Write-Host "Processing $Name..."
    Write-Host "  Package: $Package"
    Write-Host "  APK directory: $ApkDir"

    if (-not (Test-Path $ApkDir)) {
        Write-Host "  APK directory not found!" -ForegroundColor Red
        Write-Host ""
        return $false
    }

    $apk = Get-ChildItem -Path $ApkDir -Filter "*.apk" | Select-Object -First 1

    if (-not $apk) {
        Write-Host "  No APK found in directory!" -ForegroundColor Red
        Write-Host ""
        return $false
    }

    Write-Host "  APK found: $($apk.FullName)" -ForegroundColor Green
    Write-Host -NoNewline "  Installing... "
    $result = adb install -r $apk.FullName 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Host "Success" -ForegroundColor Green
        Write-Host ""
        return $true
    } else {
        Write-Host "Failed" -ForegroundColor Red
        Write-Host "  Error: $result"
        Write-Host ""
        return $false
    }
}

# Install all variants
if (Install-Variant -Package "$PACKAGE_BASE.debug" -Name "Debug" -ApkDir "app\build\outputs\apk\debug") {
    $successCount++
} else {
    $failedCount++
}

if (Install-Variant -Package "$PACKAGE_BASE" -Name "Release" -ApkDir "app\build\outputs\apk\release") {
    $successCount++
} else {
    $failedCount++
}

Write-Host ""
Write-Host "========================================="
Write-Host "Summary:"
Write-Host "  Successfully installed: $successCount" -ForegroundColor Green
if ($failedCount -gt 0) {
    Write-Host "  Failed: $failedCount" -ForegroundColor Red
}
Write-Host "  Total variants: $($successCount + $failedCount)"
Write-Host "========================================="

# List installed variants
Write-Host ""
Write-Host "Installed variants on device:"
Write-Host "-----------------------------"

function Check-Installed {
    param($Package, $Name)
    $packages = adb shell pm list packages 2>&1
    if ($packages -match $Package) {
        Write-Host "  $Name ($Package)" -ForegroundColor Green
    }
}

Check-Installed -Package "$PACKAGE_BASE.debug" -Name "Debug"
Check-Installed -Package "$PACKAGE_BASE" -Name "Release"

Write-Host ""
Write-Host "Done!" -ForegroundColor Green
