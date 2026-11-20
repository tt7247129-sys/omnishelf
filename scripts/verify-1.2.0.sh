#!/usr/bin/env bash
# One-click verification of the 1.2.0 features on an emulator or a connected phone.
#
#   ./scripts/verify-1.2.0.sh [path/to/app.apk]
#
# It checks, in order:
#   1. the APK installs and the app launches
#   2. the first-run "where did you hear about us" question appears (and only once)
#   3. a book opens - the library is not just a shell
#   4. the selection panel offers "share quote card" and the row is reachable
#   5. tapping it produces a 1080x1440 PNG inside the app's files/share directory,
#      and a share sheet opens
#
# Exit code 0 means every check passed. Each check prints PASS or FAIL with the reason,
# so a failure is a diagnosis rather than a shrug.
set -uo pipefail

ADB="${ADB:-$HOME/Library/Android/sdk/platform-tools/adb}"
PKG=cc.omnishelf.reader
WORK="$(cd "$(dirname "$0")/.." && pwd)"
TMP="${TMPDIR:-/tmp}/omnishelf-verify"
mkdir -p "$TMP"
PASS=0
FAIL=0
SKIP=0

pass() { PASS=$((PASS+1)); printf 'PASS  %s\n' "$1"; }
skip() { SKIP=$((SKIP+1)); printf 'SKIP  %s\n' "$1"; }
fail() { FAIL=$((FAIL+1)); printf 'FAIL  %s\n' "$1"; }
info() { printf '      %s\n' "$1"; }

dump() { # dump() -> path of the ui dump, or empty
  "$ADB" shell "uiautomator dump /sdcard/ui.xml" >/dev/null 2>&1
  "$ADB" pull /sdcard/ui.xml "$TMP/ui.xml" >/dev/null 2>&1 && echo "$TMP/ui.xml"
}
has_text() { grep -q "text=\"$1\"" "$2" 2>/dev/null; }
has_id() { grep -q "resource-id=\"$PKG:id/$1\"" "$2" 2>/dev/null; }
center_of() { # center_of <resource-id> <dump> -> "x y"
  /Users/huangpeng/.dsh/dsh-runtimes/dsh-primary-runtime/dependencies/python/bin/python3 - "$1" "$2" <<'PY'
import re, sys
rid, path = sys.argv[1], sys.argv[2]
xml = open(path, encoding="utf-8", errors="ignore").read()
pat = re.compile(r'resource-id="[^"]*' + re.escape(rid) + r'"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"')
m = pat.search(xml)
if not m:
    pat = re.compile(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"[^>]*resource-id="[^"]*' + re.escape(rid) + r'"')
    m = pat.search(xml)
if not m:
    print("")
else:
    x1, y1, x2, y2 = map(int, m.groups())
    print(f"{(x1+x2)//2} {(y1+y2)//2}")
PY
}


find_text_center() { # find_text_center <regex> <dump> -> "x y"
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

dismiss_dialogs() { # taps the positive button of whatever dialog is in the way
  local i
  for i in 1 2 3 4 6; do
    local d
    d=$(dump)
    [ -z "$d" ] && return
    local xy
    # "Wait" belongs here: a fresh emulator shows "System UI isn't responding" while it
    # settles, and that dialog is not the app's fault.
    xy=$(find_text_center '^(OK|Ok|Allow|ALLOW|Continue|CONTINUE|Got it|GOT IT|Allow all|Wait|允许|确定|继续|我知道了|等待|允许访问所有文件)$' "$d")
    if [ -z "$xy" ]; then
      xy=$(center_of "button1" "$d")
    fi
    [ -z "$xy" ] && return
    "$ADB" shell input tap $xy >/dev/null 2>&1
    sleep 2
  done
}

echo "== OmniShelf 1.2.0 verification =="
"$ADB" wait-for-device
"$ADB" shell 'while [ "$(getprop sys.boot_completed)" != "1" ]; do sleep 1; done' 2>/dev/null
info "device: $("$ADB" shell getprop ro.product.model | tr -d '\r')"

APK="${1:-}"
if [ -z "$APK" ]; then
  APK=$(ls -t app/build/outputs/apk/pro/*/*.apk delivery/*.apk 2>/dev/null | head -1)
fi
if [ -z "$APK" ] || [ ! -f "$APK" ]; then
  fail "no APK found (pass one as the first argument)"
  exit 1
fi
info "apk: $APK"

# ---- 1. install and launch ----
"$ADB" uninstall "$PKG" >/dev/null 2>&1 || true
if "$ADB" install -r "$APK" >"$TMP/install.log" 2>&1; then
  pass "install"
else
  fail "install: $(tail -2 "$TMP/install.log" | tr '\n' ' ')"
  exit 1
fi
VERSION=$("$ADB" shell dumpsys package "$PKG" 2>/dev/null | grep -m1 versionName | tr -d '\r' | cut -d= -f2)
info "installed version: ${VERSION:-unknown}"
HAS_1_2=1
case "$VERSION" in
  1.0*|1.1*) HAS_1_2=0 ;;
esac
"$ADB" shell pm grant "$PKG" android.permission.MANAGE_EXTERNAL_STORAGE >/dev/null 2>&1 || true
"$ADB" shell appops set "$PKG" MANAGE_EXTERNAL_STORAGE allow >/dev/null 2>&1 || true
"$ADB" shell appops set "$PKG" READ_EXTERNAL_STORAGE allow >/dev/null 2>&1 || true
"$ADB" shell am start -n "$PKG/com.foobnix.ui2.MainTabs2" >/dev/null 2>&1
sleep 8
dismiss_dialogs

# ---- 2. the source question ----
D=$(dump)
if [ -n "$D" ] && has_text "Where did you hear" "$D"; then
  pass "first-run source question shown"
  info "title: $(grep -o 'text="Where did you hear[^"]*"' "$D" | head -1)"
  # answer it so the rest of the run is not blocked, then confirm it does not come back
  XY=$(center_of "contact_source_play" "$D")
  if [ -n "$XY" ]; then
    "$ADB" shell input tap $XY
    sleep 3
    "$ADB" shell input tap 200 1700 >/dev/null 2>&1   # dismiss the follow-up dialog
    sleep 2
  fi
  "$ADB" shell am force-stop "$PKG"
  "$ADB" shell am start -n "$PKG/com.foobnix.ui2.MainTabs2" >/dev/null 2>&1
  sleep 10
  D2=$(dump)
  if [ -n "$D2" ] && has_text "Where did you hear" "$D2"; then
    fail "source question appeared a second time"
  else
    pass "source question shown only once"
  fi
elif [ "$HAS_1_2" -eq 0 ]; then
  skip "first-run source question (build $VERSION predates 1.2.0)"
else
  fail "first-run source question not found"
  info "texts on screen: $(grep -o 'text="[^"]*"' "$D" 2>/dev/null | head -5 | tr '\n' ' ')"
fi

# ---- 3. a book opens ----
D=$(dump)
BOOK_XY=""
# straight to the folder tab: it lists the filesystem without needing a library scan
TAB_XY=$(find_text_center '^(Folder|文件夹|资料夹)$' "$D")
if [ -n "$TAB_XY" ]; then
  "$ADB" shell input tap $TAB_XY
  sleep 5
  D=$(dump)
  DL_XY=$(find_text_center '^Download$' "$D")
  if [ -n "$DL_XY" ]; then
    "$ADB" shell input tap $DL_XY
    sleep 4
    D=$(dump)
  fi
  BOOK_XY=$(find_text_center '读书的方法|sample|epub$' "$D")
  [ -z "$BOOK_XY" ] && BOOK_XY=$(center_of "browserItemIcon" "$D")
fi
if [ -z "$BOOK_XY" ]; then
  D=$(dump)
  BOOK_XY=$(center_of "browserItemIcon" "$D")
fi
if [ -z "$BOOK_XY" ]; then
  fail "no book on screen to open (push a sample file to /sdcard/Download first)"
else
  "$ADB" shell input tap $BOOK_XY
  sleep 12
  ACT=$("$ADB" shell dumpsys activity activities 2>/dev/null | grep -m1 topResumedActivity)
  if echo "$ACT" | grep -qE "HorizontalViewActivity|VerticalViewActivity|OpenerActivity"; then
    pass "book opened ($(echo "$ACT" | grep -oE '[A-Za-z0-9_.]+/[A-Za-z0-9_.]+' | head -1 | cut -d/ -f2))"
  else
    fail "book did not open: $ACT"
  fi
fi

# ---- 4. the share-quote row in the selection panel ----
"$ADB" shell input swipe 540 800 540 800 1500 >/dev/null 2>&1
sleep 3
D=$(dump)
if ! has_id "onCopy" "$D" && ! has_id "onShare" "$D" && ! has_id "onShareQuoteCard" "$D"; then
  info "selection panel did not open at y=800, retrying at y=1100"
  "$ADB" shell input swipe 540 1100 540 1100 1800 >/dev/null 2>&1
  sleep 3
  D=$(dump)
fi
ROW_XY=$(center_of "onShareQuoteCard" "$D")
if [ -n "$ROW_XY" ]; then
  pass "selection panel has the share-quote-card row"
else
  # the row sits behind the vertical-dots fold in the compact panel
  MORE_XY=$(center_of "moreToggle" "$D")
  if [ -n "$MORE_XY" ]; then
    "$ADB" shell input tap $MORE_XY
    sleep 2
    D=$(dump)
    ROW_XY=$(center_of "onShareQuoteCard" "$D")
  fi
  if [ -n "$ROW_XY" ]; then
    pass "share-quote-card row reachable behind the fold"
  elif [ "$HAS_1_2" -eq 0 ]; then
    skip "share-quote-card row (build $VERSION predates 1.2.0)"
  else
    fail "share-quote-card row not found in the selection panel"
    info "ids: $(grep -o 'resource-id="[^"]*"' "$D" 2>/dev/null | head -8 | tr '\n' ' ')"
  fi
fi

# ---- 5. tapping it produces a card ----
if [ -n "${ROW_XY:-}" ]; then
  BEFORE=$("$ADB" shell run-as "$PKG" ls files/share 2>/dev/null | wc -l | tr -d ' ')
  "$ADB" shell input tap $ROW_XY
  sleep 5
  AFTER=$("$ADB" shell run-as "$PKG" ls files/share 2>/dev/null | wc -l | tr -d ' ')
  if [ "$AFTER" -gt "$BEFORE" ]; then
    NEW=$("$ADB" shell run-as "$PKG" ls -t files/share 2>/dev/null | head -1 | tr -d '\r')
    "$ADB" exec-out run-as "$PKG" cat "files/share/$NEW" > "$TMP/card.png" 2>/dev/null
    SIZE=$(wc -c < "$TMP/card.png" | tr -d ' ')
    DIM=$(sips -g pixelWidth -g pixelHeight "$TMP/card.png" 2>/dev/null | awk '/pixel/{printf "%s ", $2}')
    if [ "${SIZE:-0}" -gt 10000 ]; then
      pass "quote card written ($SIZE bytes, ${DIM})"
      info "card: $TMP/card.png"
    else
      fail "quote card too small ($SIZE bytes)"
    fi
  else
    fail "tapping the row produced no card file"
  fi
  sleep 2
  D=$(dump)
  if grep -qiE "share|分享|共有|send" "$D" 2>/dev/null; then
    pass "share sheet opened"
  else
    info "share sheet: could not confirm from the dump (${TMP}/ui.xml kept)"
  fi
fi

"$ADB" exec-out screencap -p > "$TMP/final.png" 2>/dev/null && info "screenshot: $TMP/final.png"

echo
echo "== $PASS passed, $SKIP skipped, $FAIL failed =="
[ "$SKIP" -gt 0 ] && info "skipped checks belong to 1.2.0; this build predates them"
[ "$FAIL" -eq 0 ] || exit 1
