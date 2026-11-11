#!/usr/bin/env bash
# Shared helpers for the emulator QA scripts.
# Every script sources this file; nothing here touches application code.

set -uo pipefail

ADB="${ADB:-/tmp/android-sdk/platform-tools/adb}"
PKG="${PKG:-cc.omnishelf.reader}"
MAIN="${MAIN:-cc.omnishelf.reader/com.foobnix.ui2.MainTabs2}"
OPENER="${OPENER:-cc.omnishelf.reader/com.foobnix.OpenerActivity}"
SHOTS="${SHOTS:-docs/ui-shots}"

FAILURES=0
CHECKS=0

say()  { printf '%s\n' "$*"; }
step() { printf '\n\033[1m== %s\033[0m\n' "$*"; }

adb()  { "$ADB" "$@"; }

require_device() {
  local n
  n=$(adb devices | grep -c "device$" || true)
  [ "$n" -ge 1 ] || { say "no device attached"; exit 2; }
}

screen_size() {
  adb shell wm size | awk -F': ' '{print $2}' | tail -1
}

# tap_ratio X Y  — tap at X% / Y% of the screen, so coordinates survive a
# resolution change between emulator profiles.
tap_ratio() {
  local size w h
  size=$(screen_size)
  w=${size%x*}; h=${size#*x}
  adb shell input tap $(( w * $1 / 100 )) $(( h * $2 / 100 ))
}

swipe_ratio() {
  local size w h
  size=$(screen_size)
  w=${size%x*}; h=${size#*x}
  adb shell input swipe $(( w * $1 / 100 )) $(( h * $2 / 100 )) \
                        $(( w * $3 / 100 )) $(( h * $4 / 100 )) 220
}

shot() {
  local name="$1" dir="${SHOTS}/${LABEL:-run}"
  mkdir -p "$dir"
  adb shell screencap -p "/sdcard/qa-${name}.png" >/dev/null 2>&1
  adb pull "/sdcard/qa-${name}.png" "${dir}/${name}.png" >/dev/null 2>&1
  adb shell rm -f "/sdcard/qa-${name}.png" >/dev/null 2>&1
}

clear_log() { adb logcat -c >/dev/null 2>&1; }

# scan_crash NAME — fail the run if the app logged a fatal error or an ANR.
scan_crash() {
  local label="$1" hits
  hits=$(adb logcat -d 2>/dev/null | grep -E \
    "FATAL EXCEPTION|ANR in ${PKG}|Force finishing activity ${PKG}|has died" \
    | grep -v "START u0" || true)
  CHECKS=$((CHECKS + 1))
  if [ -n "$hits" ]; then
    FAILURES=$((FAILURES + 1))
    say "  ✗ ${label}: crash or ANR detected"
    printf '%s\n' "$hits" | head -12 | sed 's/^/      /'
    return 1
  fi
  say "  ✓ ${label}"
  return 0
}

# check NAME EXPECTED ACTUAL
check() {
  CHECKS=$((CHECKS + 1))
  if [ "$2" = "$3" ]; then
    say "  ✓ $1"
  else
    FAILURES=$((FAILURES + 1))
    say "  ✗ $1 (expected '$2', got '$3')"
  fi
}

top_activity() {
  adb shell dumpsys activity activities 2>/dev/null \
    | grep -m1 -oE "${PKG}/[A-Za-z0-9_.]+" || echo "none"
}

open_book() {
  local path="$1"
  adb shell am force-stop "$PKG" >/dev/null 2>&1
  sleep 1
  adb shell am start -a android.intent.action.VIEW \
    -d "file://${path}" -t "*/*" -n "$OPENER" >/dev/null 2>&1
  sleep 6
}

summary() {
  printf '\n---------------------------------------------\n'
  printf '%s: %d checks, %d failures\n' "${LABEL:-run}" "$CHECKS" "$FAILURES"
  printf 'screenshots: %s/%s\n' "$SHOTS" "${LABEL:-run}"
  [ "$FAILURES" -eq 0 ] || exit 1
}
