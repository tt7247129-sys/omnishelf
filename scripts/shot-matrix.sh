#!/usr/bin/env bash
# shot-matrix.sh — the visual baseline.
#
# Captures the same eight screens every time so a redesign can be compared
# against the previous stage without hunting for angles.
#
#   ./scripts/shot-matrix.sh before-t0
#   ./scripts/shot-matrix.sh after-t0

cd "$(dirname "$0")/.." || exit 2
. scripts/lib.sh

LABEL="${1:-matrix}"
require_device

step "library"
adb shell am force-stop "$PKG" >/dev/null 2>&1
adb shell am start -n "$MAIN" >/dev/null 2>&1
sleep 6
shot library
scan_crash "library"

step "library drawer"
tap_ratio 4 5; sleep 2; shot library-drawer
adb shell input keyevent 4; sleep 1

step "reader with text"
open_book "/sdcard/Download/pg11.epub"
swipe_ratio 80 55 20 55; sleep 2
shot reader-text
scan_crash "reader"

step "reader controls"
tap_ratio 50 50; sleep 2
shot reader-controls
scan_crash "reader controls"

step "reader controls, scrolled panel"
swipe_ratio 50 78 50 45; sleep 1
shot reader-controls-scrolled

step "settings"
adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
tap_ratio 4 5; sleep 2
shot settings-entry
adb shell input keyevent 4; sleep 1

summary
