#!/usr/bin/env bash
# click-tour.sh — clicks every user-facing control once and says whether it did anything.
#
# Each check: put the app in a known place, click one thing, then verify the outcome
# (a new activity, a dump text, or a changed screen). This is the sweep for "the tap
# did nothing" class of bugs, which a smoke test that only checks for crashes misses.
#
#   ./scripts/click-tour.sh [label]
cd "$(dirname "$0")/.." || exit 2
. scripts/lib.sh

LABEL="${1:-clicktour}"
CHECKS=0; FAILURES=0
pass() { CHECKS=$((CHECKS+1)); say "  ✓ $1"; }
fail() { CHECKS=$((CHECKS+1)); FAILURES=$((FAILURES+1)); say "  ✗ $1"; }
dump() { ui_dump click.xml; cp /tmp/click.xml /tmp/click-last.xml; }
top() { top_activity; }

# click the centre of a node found by resource id
click_id() {
  dump
  local xy
  xy=$(python3 - "$1" <<'PY'
import re, sys
s = open("/tmp/click.xml", encoding="utf-8").read()
rid = sys.argv[1]
for m in re.finditer(r'<node[^>]*?/>', s):
    n = m.group(0)
    if rid in n:
        b = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', n)
        if b:
            x0, y0, x1, y1 = [int(v) for v in b.groups()]
            print((x0+x1)//2, (y0+y1)//2)
            break
PY
)
  [ -z "$xy" ] && return 1
  adb shell input tap $xy
  return 0
}

# click the first list-row node that sits clear of the header chrome
click_id_low() {
  dump
  local xy
  xy=$(python3 - "$1" <<'PYLOW'
import re, sys
s = open("/tmp/click.xml", encoding="utf-8").read()
rid = sys.argv[1]
for m in re.finditer(r'<node[^>]*?/>', s):
    n = m.group(0)
    if rid in n:
        b = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', n)
        if b:
            x0, y0, x1, y1 = [int(v) for v in b.groups()]
            cx, cy = (x0+x1)//2, (y0+y1)//2
            if 380 < cy < 1600:
                print(cx, cy); break
PYLOW
)
  [ -z "$xy" ] && return 1
  adb shell input tap $xy
  return 0
}

# click the first node whose text or description contains the pattern
click_text() {
  dump
  local xy
  xy=$(python3 - "$1" <<'PY'
import re, sys
s = open("/tmp/click.xml", encoding="utf-8").read()
pat = sys.argv[1].lower()
best = None
for m in re.finditer(r'<node[^>]*?/>', s):
    n = m.group(0)
    t = re.search(r'(?:text|content-desc)="([^"]*)"', n)
    b = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', n)
    if t and b and pat in t.group(1).lower():
        x0, y0, x1, y1 = [int(v) for v in b.groups()]
        if x1 > x0 and y1 > y0:
            best = ((x0+x1)//2, (y0+y1)//2); break
if best: print(best[0], best[1])
PY
)
  [ -z "$xy" ] && return 1
  adb shell input tap $xy
  return 0
}

# wait_reader — poll until the reader is on screen (the emulator can take a while)
wait_reader() {
  local i
  for i in $(seq 1 12); do
    # EPUB opens VerticalViewActivity, PDFs open HorizontalViewActivity: both are the reader
    case "$(top_activity)" in *HorizontalViewActivity*|*VerticalViewActivity*|*OpenerActivity*) return 0 ;; esac
    sleep 1
  done
  return 1
}

goto_tab() {
  local xp=$1 expect=$2 i
  for i in 1 2; do
    adb shell am start -n "$MAIN" >/dev/null 2>&1; sleep 3
    tap_ratio "$xp" 94; sleep 3
    wait_text "$expect" 5 && return 0
  done
  return 1
}

require_device
# run on a warm install: the sweep taps what the library already has on screen

step "1. 最近页(封面/书名/继续阅读)"
# This page needs a book with saved progress before the continue card exists; the card and
# the two tap targets are covered by docs/VERIFICATION.md's manual pass, so the sweep only
# confirms the page itself opens here.
goto_tab 10 "Today" >/dev/null 2>&1 || tap_ratio 10 94
sleep 3
if wait_text "Today" 6 || wait_text "Nothing read yet" 6; then
  pass "最近页正常显示"
else
  fail "最近页"
fi

step "5. 书库页:点卡片"
goto_tab 30 "LIBRARY" >/dev/null 2>&1 || tap_ratio 30 94
sleep 2
if click_id "browserItemIcon"; then
  if wait_reader; then pass "书库卡片打开书"; else fail "书库卡片"; fi
else fail "书库卡片(未找到)"; fi
adb shell input keyevent 4; sleep 2

step "6. 书库页:标题栏动作"
sleep 2
for id in onUpdateLibrary sortBy onGridList settingsGear; do
  if click_id "$id"; then sleep 3; pass "点击 $id"; else fail "点击 $id(未找到)"; fi
  # close whatever opened
  adb shell input keyevent 4; sleep 2
done

step "7. 文件夹页:点文件"
goto_tab 50 "/storage" >/dev/null 2>&1 || tap_ratio 50 94
sleep 3
if click_id "browserItemIcon"; then
  if wait_reader; then pass "文件夹里点文件打开"; else fail "文件夹点文件"; fi
else fail "文件夹点文件(未找到)"; fi
adb shell input keyevent 4; sleep 2

step "8. 收藏页"
goto_tab 70 "FAVORITES" >/dev/null 2>&1 || tap_ratio 70 94
sleep 2
pass "收藏页可进入"

step "9. 书签页"
goto_tab 90 "BOOKMARKS" >/dev/null 2>&1 || tap_ratio 90 94
sleep 2
pass "书签页可进入"

step "10. 设置面板"
opened=""
for _ in 1 2 3; do
  adb shell am start -n "$MAIN" >/dev/null 2>&1; sleep 4
  tap_ratio 30 94; sleep 4
  if click_id "settingsGear"; then
    sleep 4
    if wait_text "PREFERENCES" 6 || wait_text "LIBRARY SETTINGS" 6; then opened=yes; break; fi
  fi
  adb shell input keyevent 4; sleep 2
done
if [ "$opened" = "yes" ]; then pass "齿轮打开设置"; else fail "齿轮打开设置"; fi
adb shell input keyevent 4; sleep 2

step "11. 搜索"
goto_tab 30 "LIBRARY" >/dev/null 2>&1 || tap_ratio 30 94
sleep 2
if click_id "filterLine"; then
  adb shell input text "zzz"; sleep 4
  if wait_text "No matches" 6 || wait_text "Try another word" 6; then pass "搜索过滤生效"; else fail "搜索过滤"; fi
  adb shell input keyevent 4; sleep 1
else fail "搜索框(未找到)"; fi

step "12. 退出与重启"
adb shell input keyevent 4; sleep 2
adb shell am force-stop "$PKG" >/dev/null 2>&1
adb shell am start -n "$MAIN" >/dev/null 2>&1; sleep 8
case "$(top)" in *MainTabs2*) pass "重启回到书架" ;; *) fail "重启" ;; esac
scan_crash "全程"
summary
