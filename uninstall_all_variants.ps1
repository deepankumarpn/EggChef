#!/usr/bin/env pwsh

# Script to uninstall all variants of EggChef
# Usage: .\uninstall_all_variants.ps1

$PACKAGE_BASE = "deepankumarpn.github.io.eggchef"

Write-Host "========================================="
Write-Host "Uninstalling All Variants"
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

Write-Host "Uninstalling all variants..."
Write-Host "----------------------------"

$successCount = 0
$notInstalledCount = 0

function Uninstall-Package {
    param($Package, $Name)
    Write-Host -NoNewline "Uninstalling $Name... "
    $result = adb uninstall $Package 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Done" -ForegroundColor Green
        $script:successCount++
    } else {
        Write-Host "Not installed" -ForegroundColor Yellow
        $script:notInstalledCount++
    }
}

# Uninstall all variants
Uninstall-Package -Package "$PACKAGE_BASE.debug" -Name "Debug"
Uninstall-Package -Package "$PACKAGE_BASE" -Name "Release"

Write-Host ""
Write-Host "========================================="
Write-Host "Summary:"
Write-Host "  Successfully uninstalled: $successCount" -ForegroundColor Green
if ($notInstalledCount -gt 0) {
    Write-Host "  Not installed: $notInstalledCount" -ForegroundColor Yellow
}
Write-Host "  Total variants: $($successCount + $notInstalledCount)"
Write-Host "========================================="

# Verify no variants are left on device
Write-Host ""
Write-Host "Verifying removal..."
Write-Host "--------------------"
$remaining = 0

function Check-Removed {
    param($Package, $Name)
    $packages = adb shell pm list packages 2>&1
    if ($packages -match $Package) {
        Write-Host "  $Name ($Package) - Still installed!" -ForegroundColor Yellow
        $script:remaining++
    }
}

Check-Removed -Package "$PACKAGE_BASE.debug" -Name "Debug"
Check-Removed -Package "$PACKAGE_BASE" -Name "Release"

if ($remaining -eq 0) {
    Write-Host "  All variants successfully removed!" -ForegroundColor Green
}

Write-Host ""
Write-Host "Done!" -ForegroundColor Green
