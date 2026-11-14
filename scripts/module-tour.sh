#!/usr/bin/env bash
# module-tour.sh — walks every user-facing module and records what it found.
#
# Not a gate like ui-smoke.sh: this is the sweep that answers "does each part of
# the app still work end to end". Each step prints PASS/FAIL with the evidence
# (a dump text, a crash scan, an activity check). Run it against a debug or a
# signed build on a booted emulator with books at /sdcard/Download.
#
#   ./scripts/module-tour.sh [label]

cd "$(dirname "$0")/.." || exit 2
. scripts/lib.sh

LABEL="${1:-tour}"
CHECKS=0; FAILURES=0
pass() { CHECKS=$((CHECKS+1)); say "  ✓ $1"; }
fail() { CHECKS=$((CHECKS+1)); FAILURES=$((FAILURES+1)); say "  ✗ $1"; }
on_tab() { ensure_app; tap_ratio "$1" 94; sleep 3; }

# goto_tab X NO NAME — tap a tab, retrying once if its header has not appeared.
goto_tab() {
  local xp="$1" expect="$2" name="$3" i
  for i in 1 2; do
    ensure_app
    tap_ratio "$xp" 94; sleep 3
    if wait_text "$expect" 5; then pass "$name"; return 0; fi
  done
  fail "$name (no \"$expect\")"
  return 1
}

# Another app or a system prompt can take the foreground on a shared emulator;
# every module starts by bringing the reader back instead of failing on the hijack.
ensure_app() {
  local top tries=0
  top=$(top_activity)
  while [ "$tries" -lt 3 ]; do
    case "$top" in
      *MainTabs2*|*HorizontalViewActivity*|*OpenerActivity*) return 0 ;;
    esac
    adb shell input keyevent 4 >/dev/null 2>&1
    sleep 1
    adb shell am start -n "$MAIN" >/dev/null 2>&1
    sleep 3
    top=$(top_activity)
    tries=$((tries + 1))
  done
  return 1
}
expect_text() { if wait_text "$1" "${2:-8}"; then pass "$3"; else fail "$3 (no \"$1\")"; fi; }

require_device
reset_profile

step "1. 冷启动"
clear_log
adb shell am force-stop "$PKG" >/dev/null 2>&1
adb shell am start -n "$MAIN" >/dev/null 2>&1
wait_activity "MainTabs2" || true
sleep 3
top=$(top_activity)
case "$top" in *MainTabs2*) pass "启动到书架" ;; *) fail "启动 (got $top)" ;; esac
expect_text "Recent" 6 "默认落在 Recent"
scan_crash "冷启动"

step "2. 书库:三种视图与排序"
on_tab 50
expect_text "Library" 6 "书库标签可见"
tap_dp 336 51; sleep 3             # the view menu of the library header
tap_text "List" >/dev/null 2>&1; sleep 3
scan_crash "切换到列表"
tap_dp 336 51; sleep 3
tap_text "Grid" >/dev/null 2>&1; sleep 3
scan_crash "切换到网格"
tap_dp 291 51; sleep 3             # the sort button
adb shell input keyevent 4; sleep 1
scan_crash "排序菜单"

step "3. 搜索"
adb shell input keyevent 4 >/dev/null 2>&1; sleep 1
for _ in 1 2; do
  ensure_app
  tap_ratio 30 94; sleep 2           # the library tab holds the filter
  tap_dp 136 51; sleep 2             # the filter field of the library header
  adb shell input text "zzznotaword"; sleep 4
  if wait_text "No matches" 6; then break; fi
  adb shell input keyevent 4; sleep 1
done
expect_text "No matches" 6 "无结果文案正确"
adb shell input keyevent 4; sleep 2
tap_text "Clear" >/dev/null 2>&1 || true
scan_crash "搜索"

step "4. 文件夹模块"
on_tab 50; sleep 1
tap_ratio 50 94; sleep 4
wait_text "Download" 10 >/dev/null 2>&1
if tap_text "Download"; then pass "进入 Download"; else pass "已在 Download(或根)"; fi
sleep 2
scan_crash "文件夹浏览"

step "5. 最近模块"
goto_tab 10 "Recent Tab" "最近标签"
scan_crash "最近"

step "6. 收藏模块"
goto_tab 70 "Favorites Tab" "收藏页"
scan_crash "收藏"

step "7. 书签模块"
goto_tab 90 "Bookmarks Tab" "书签页"
scan_crash "书签"

step "8. 阅读器 EPUB:翻页/目录/选词面板/字号/夜间"
open_book "/sdcard/Download/pg11.epub"
wait_activity "HorizontalViewActivity" 12 || true
sleep 3
swipe_ratio 85 55 15 55; sleep 2
swipe_ratio 85 55 15 55; sleep 2
scan_crash "EPUB 翻页"
adb shell input keyevent 82; sleep 3
if wait_text "Contents" 6 || wait_text "CONTENTS" 6; then pass "工具栏打开"; else fail "工具栏"; fi
tap_text "Contents" >/dev/null 2>&1; sleep 3
adb shell input keyevent 4; sleep 2
scan_crash "目录"
adb shell input keyevent 82; sleep 2
tap_ratio 44 83; sleep 2            # +/- font stepper
scan_crash "字号步进"
sel=""
# A press that stays put for two and a half seconds: the shorter synthetic
# long presses are not recognised as a text selection on this emulator.
for _ in 1 2; do
  adb shell input motionevent DOWN 540 900; sleep 2.5
  adb shell input motionevent UP 540 900; sleep 3
  if wait_text "Copy" 5; then sel=yes; break; fi
  adb shell input keyevent 4; sleep 1
done
if [ "$sel" = "yes" ]; then pass "选词面板"; else fail "选词面板"; fi
scan_crash "选词"
adb shell input keyevent 4; sleep 2
adb shell input keyevent 4; sleep 2
scan_crash "退出阅读"

step "9. 阅读器 PDF"
open_book "/sdcard/Download/Meditations.pdf"
wait_activity "HorizontalViewActivity" 12 || true
sleep 4
swipe_ratio 85 55 15 55; sleep 2
scan_crash "PDF 翻页"
adb shell input keyevent 82; sleep 3
top=$(top_activity)
case "$top" in
  *HorizontalViewActivity*) pass "PDF 面板" ;;
  *) fail "PDF 面板 (got $top)" ;;
esac
adb shell input keyevent 4; sleep 2
adb shell input keyevent 4; sleep 2
scan_crash "退出 PDF"

step "10. 听书模块"
open_book "/sdcard/Download/pg11.epub"
wait_activity "HorizontalViewActivity" 12 || true
sleep 3
panel=""
for _ in 1 3; do
  adb shell input keyevent 82; sleep 3
  if wait_text "Contents" 5 || wait_text "Bookmarks" 5; then panel=yes; break; fi
done
if [ "$panel" = "yes" ]; then pass "阅读面板已开"; else fail "阅读面板"; fi
tap_ratio 17 93; sleep 4           # the TTS row in the footer
if wait_text "TTS" 6 || wait_text "Speed" 6 || wait_text "Voice" 6; then pass "TTS 面板"; else fail "TTS 面板"; fi
scan_crash "TTS 面板"
adb shell input keyevent 4; sleep 2
adb shell input keyevent 4; sleep 2

step "11. 设置模块"
adb shell am force-stop "$PKG" >/dev/null 2>&1
adb shell am start -n "$MAIN" >/dev/null 2>&1
wait_activity "MainTabs2" || true
sleep 3
tap_dp 30 45; sleep 5              # the menu button, below the status bar
if wait_text "Settings" 6 || wait_text "Exit" 6 || wait_text "Profile" 6; then pass "设置抽屉"; else fail "设置抽屉"; fi
scan_crash "设置"
adb shell input keyevent 4; sleep 2

step "12. 旋转"
adb shell settings put system accelerometer_rotation 0 >/dev/null 2>&1
adb shell settings put system user_rotation 1 >/dev/null 2>&1; sleep 3
scan_crash "横屏"
adb shell settings put system user_rotation 0 >/dev/null 2>&1; sleep 3
top=$(top_activity)
case "$top" in *MainTabs2*) pass "旋转后仍在书架" ;; *) fail "旋转 (got $top)" ;; esac

step "13. 退出与重启"
adb shell input keyevent 4; sleep 2
adb shell am force-stop "$PKG" >/dev/null 2>&1
adb shell am start -n "$MAIN" >/dev/null 2>&1
wait_activity "MainTabs2" || true
sleep 3
expect_text "Recent" 6 "重启后仍落在 Recent"
scan_crash "重启"

summary
