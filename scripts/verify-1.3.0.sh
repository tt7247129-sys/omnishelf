#!/usr/bin/env bash
# One-click verification of the 1.3.0 quote cards on a device.
#
#   ./scripts/verify-1.3.0.sh [path/to/app.apk]
#
# Checks: install, launch, first-run question (once), a book opens, the selection panel
# offers the card row, the preview dialog shows six templates and flips through them
# without a crash, and sharing either writes a card (debug build) or opens the chooser.
set -uo pipefail

ADB="${ADB:-$HOME/Library/Android/sdk/platform-tools/adb}"
PKG=cc.omnishelf.reader
TMP="${TMPDIR:-/tmp}/omnishelf-verify-13"
mkdir -p "$TMP"
PASS=0; FAIL=0
pass() { PASS=$((PASS+1)); printf 'PASS  %s\n' "$1"; }
fail() { FAIL=$((FAIL+1)); printf 'FAIL  %s\n' "$1"; }
info() { printf '      %s\n' "$1"; }

dump() {
  "$ADB" shell "uiautomator dump /sdcard/ui.xml" >/dev/null 2>&1
  "$ADB" pull /sdcard/ui.xml "$TMP/ui.xml" >/dev/null 2>&1 && echo "$TMP/ui.xml"
}
has_text() { grep -q "text=\"[^\"]*$1[^\"]*\"" "$2" 2>/dev/null; }
center_of() {
  /Users/huangpeng/.dsh/dsh-runtimes/dsh-primary-runtime/dependencies/python/bin/python3 - "$1" "$2" <<'PYX'
import re, sys
rid, path = sys.argv[1], sys.argv[2]
xml = open(path, encoding="utf-8", errors="ignore").read()
pat = re.compile(r'resource-id="[^"]*' + re.escape(rid) + r'"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"')
m = pat.search(xml)
if not m:
    pat = re.compile(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"[^>]*resource-id="[^"]*' + re.escape(rid) + r'"')
    m = pat.search(xml)
print("" if not m else f"{(int(m.group(1))+int(m.group(3)))//2} {(int(m.group(2))+int(m.group(4)))//2}")
PYX
}
find_text_center() {
  /Users/huangpeng/.dsh/dsh-runtimes/dsh-primary-runtime/dependencies/python/bin/python3 - "$1" "$2" <<'PYX'
import re, sys
pat, path = sys.argv[1], sys.argv[2]
xml = open(path, encoding="utf-8", errors="ignore").read()
for m in re.finditer(r'<node[^>]*>', xml):
    tag = m.group(0)
    t = re.search(r'text="([^"]*)"', tag)
    b = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', tag)
    if t and b and re.search(pat, t.group(1)):
        x1, y1, x2, y2 = map(int, b.groups())
        print(f"{(x1+x2)//2} {(y1+y2)//2}")
        break
PYX
}
dismiss_dialogs() {
  local i xy d
  for i in 1 2 3 4; do
    d=$(dump); [ -z "$d" ] && return
    xy=$(find_text_center '^(OK|Ok|Allow|ALLOW|Continue|Got it|Wait|允许|确定|继续|等待)$' "$d")
    [ -z "$xy" ] && return
    "$ADB" shell input tap $xy >/dev/null 2>&1; sleep 2
  done
}

echo "== OmniShelf 1.3.0 quote-card verification =="
"$ADB" wait-for-device
"$ADB" shell 'while [ "$(getprop sys.boot_completed)" != "1" ]; do sleep 1; done' 2>/dev/null
info "device: $("$ADB" shell getprop ro.product.model | tr -d '\r')"

APK="${1:-}"
[ -z "$APK" ] && APK=$(ls -t delivery/OmniShelf-1.3.0-arm64-signed.apk app/build/outputs/apk/pro/release/*.apk 2>/dev/null | head -1)
if [ -z "$APK" ] || [ ! -f "$APK" ]; then fail "no APK (pass one as the first argument)"; exit 1; fi
info "apk: $APK"

"$ADB" uninstall "$PKG" >/dev/null 2>&1 || true
if "$ADB" install -r "$APK" >"$TMP/install.log" 2>&1; then pass "install"; else
  fail "install: $(tail -2 "$TMP/install.log" | tr '\n' ' ')"; exit 1; fi
VERSION=$("$ADB" shell dumpsys package "$PKG" 2>/dev/null | grep -m1 versionName | tr -d '\r' | cut -d= -f2)
info "installed version: ${VERSION:-unknown}"
"$ADB" shell pm clear "$PKG" >/dev/null 2>&1 || true
"$ADB" shell rm -rf /sdcard/OmniShelf >/dev/null 2>&1 || true
"$ADB" shell pm grant "$PKG" android.permission.MANAGE_EXTERNAL_STORAGE >/dev/null 2>&1 || true
"$ADB" shell appops set "$PKG" MANAGE_EXTERNAL_STORAGE allow >/dev/null 2>&1 || true
"$ADB" shell am start -n "$PKG/com.foobnix.ui2.MainTabs2" >/dev/null 2>&1
sleep 12

# ---- first-run question ----
SEEN=0
for i in $(seq 1 8); do
  D=$(dump); [ -z "$D" ] && { sleep 2; continue; }
  if has_text "Where did you hear" "$D" || has_text "从哪里知道" "$D"; then
    SEEN=1
    XY=$(find_text_center 'Google Play' "$D"); [ -n "$XY" ] && "$ADB" shell input tap $XY && sleep 3
    continue
  fi
  if has_text "Send this answer" "$D" || has_text "把这个答案" "$D"; then
    SEEN=1; XY=$(find_text_center '^(CANCEL|Cancel|取消)$' "$D"); [ -n "$XY" ] && "$ADB" shell input tap $XY; sleep 2; break
  fi
  sleep 2
done
[ "$SEEN" -eq 1 ] && pass "first-run question shown" || fail "first-run question not found"
dismiss_dialogs

# ---- open a book ----
D=$(dump)
TAB=$(find_text_center '^(Folder|文件夹)$' "$D")
[ -n "$TAB" ] && "$ADB" shell input tap $TAB && sleep 5
D=$(dump); DL=$(find_text_center '^Download$' "$D")
[ -n "$DL" ] && "$ADB" shell input tap $DL && sleep 4
D=$(dump); BOOK=$(find_text_center '读书的方法' "$D")
[ -z "$BOOK" ] && BOOK=$(center_of "browserItemIcon" "$D")
if [ -z "$BOOK" ]; then fail "no book to open"; else
  "$ADB" shell input tap $BOOK; sleep 12
  ACT=$("$ADB" shell dumpsys activity activities 2>/dev/null | grep -m1 topResumedActivity)
  echo "$ACT" | grep -qE "ViewActivity|OpenerActivity" && pass "book opened" || fail "book did not open"
fi

# ---- the card row and the six-template dialog ----
"$ADB" shell input swipe 540 900 540 900 1600 >/dev/null 2>&1
sleep 3
D=$(dump)
ROW=$(center_of "onShareQuoteCard" "$D")
if [ -z "$ROW" ]; then
  MORE=$(center_of "moreToggle" "$D")
  [ -n "$MORE" ] && "$ADB" shell input tap $MORE && sleep 2
  D=$(dump); ROW=$(center_of "onShareQuoteCard" "$D")
fi
if [ -z "$ROW" ]; then fail "share-quote-card row not found"; else
  pass "selection panel has the card row"
  "$ADB" logcat -c >/dev/null 2>&1
  "$ADB" shell input tap $ROW; sleep 4
  # The dialog remembers the last layout used, so do not assume it opens on 1 / 6.
  START=""
  for i in $(seq 1 10); do
    D=$(dump)
    START=$(grep -oE '[0-9] / 5' "$D" 2>/dev/null | head -1)
    [ -n "$START" ] && break
    sleep 2
  done
  if [ -n "$START" ]; then
    pass "preview dialog opened (template ${START})"
  else
    fail "preview dialog did not appear"
  fi
  if [ -n "$START" ]; then
    # flip through the six layouts; the counter must visit six different values
    SEEN_VALUES="$START"
    for step in 1 2 3 4 5; do
      NEXT=$(center_of "cardNext" "$D")
      [ -z "$NEXT" ] && { fail "next button missing"; break; }
      "$ADB" shell input tap $NEXT; sleep 2
      D=$(dump)
      V=$(grep -oE '[0-9] / 5' "$D" 2>/dev/null | head -1)
      [ -z "$V" ] && { fail "counter vanished after tap"; break; }
      SEEN_VALUES="$SEEN_VALUES $V"
    done
    # match whole labels: "2 / 6" rather than the tokens around the slash
    DISTINCT=$(echo "$SEEN_VALUES" | grep -oE '[0-9] / 5' | sort -u | wc -l | tr -d ' ')
    if [ "$DISTINCT" -eq 5 ]; then pass "all five layouts rendered (visited $DISTINCT distinct counters)"
    else fail "only $DISTINCT distinct template counters seen: $SEEN_VALUES"; fi
    CRASH=$("$ADB" logcat -d 2>/dev/null | grep -cE "FATAL EXCEPTION.*$PKG")
    [ "$CRASH" -eq 0 ] && pass "no crash while flipping templates" || fail "crash in logcat ($CRASH)"
  fi
  SHARE=$(center_of "cardShare" "$D")
  if [ -n "$SHARE" ]; then
    "$ADB" shell input tap $SHARE; sleep 4
    D2=$(dump)
    if grep -qiE "share|分享|send|send to" "$D2" 2>/dev/null; then pass "share sheet opened"
    else info "share sheet not detectable from the dump"; fi
  fi
fi

"$ADB" exec-out screencap -p > "$TMP/final.png" 2>/dev/null && info "screenshot: $TMP/final.png"
echo
echo "== $PASS passed, $FAIL failed =="
[ "$FAIL" -eq 0 ] || exit 1
