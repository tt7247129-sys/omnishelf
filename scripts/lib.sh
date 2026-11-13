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
  # Straight off the framebuffer: on a freshly booted device /sdcard is sometimes not
  # writable by shell yet, and a failed pull used to leave an empty file behind.
  adb exec-out screencap -p > "${dir}/${name}.png" 2>/dev/null || true
}

clear_log() { adb logcat -c >/dev/null 2>&1; }

# scan_crash NAME — fail the run if the app logged a fatal error or an ANR.
scan_crash() {
  local label="$1" hits
  # Only this process counts: the emulator kills system components constantly and
  # those lines used to be reported as our crashes.
  # A wedged emulator shows "Process system isn't responding", which never names our
  # package - and the run then reported zero failures over a screenshot of that dialog.
  hits=$(adb logcat -d 2>/dev/null \
    | grep -E "FATAL EXCEPTION|ANR in |Force finishing activity ${PKG}|isn't responding" -A2 \
    | grep -E "${PKG}|FATAL EXCEPTION|ANR in|isn't responding" || true)
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

# wait_text PATTERN [TIMEOUT_S] — block until a visible node's text contains the pattern.
wait_text() {
  local pattern="$1" limit="${2:-15}" i
  for i in $(seq 1 "$limit"); do
    adb shell "uiautomator dump /sdcard/qa-wait.xml" >/dev/null 2>&1 || true
    adb pull /sdcard/qa-wait.xml /tmp/qa-wait.xml >/dev/null 2>&1 || true
    adb shell rm -f /sdcard/qa-wait.xml >/dev/null 2>&1 || true
    if python3 - "$pattern" <<'PY'
import re, sys
try:
    s = open("/tmp/qa-wait.xml", encoding="utf-8").read()
except Exception:
    sys.exit(1)
pat = sys.argv[1]
sys.exit(0 if re.search('text="[^"]*' + re.escape(pat) + '[^"]*"', s, re.I) else 1)
PY
    then
      return 0
    fi
    sleep 2
  done
  return 1
}

# tap_text PATTERN — tap the first visible node whose text contains the pattern.
# Uses uiautomator, so it is slow (2-4s); for one-off navigation, not per-check.
tap_text() {
  local pattern="$1" x y
  adb shell "uiautomator dump /sdcard/qa-tap.xml" >/dev/null 2>&1 || true
  adb pull /sdcard/qa-tap.xml /tmp/qa-tap.xml >/dev/null 2>&1 || true
  adb shell rm -f /sdcard/qa-tap.xml >/dev/null 2>&1 || true
  x=$(python3 - "$pattern" <<'PY'
import re, sys
try:
    s = open("/tmp/qa-tap.xml", encoding="utf-8").read()
except Exception:
    sys.exit(1)
pat = sys.argv[1]
best = None
for m in re.finditer(r'<node[^>]*/>', s):
    t = re.search(r'text="([^"]*)"', m.group(0))
    b = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', m.group(0))
    if t and pat.lower() in t.group(1).lower() and b:
        x1, y1, x2, y2 = map(int, b.groups())
        # The same word often sits in the breadcrumb up top and in a list row below;
        # the row is the thing to tap, so prefer the lowest match.
        if best is None or y1 > best[1]:
            best = ((x1 + x2) // 2, (y1 + y2) // 2, y1)
if best:
    print(best[0], best[1])
PY
)
  [ -n "$x" ] && { set -- $x; adb shell input tap "$1" "$2"; return 0; }
  return 1
}

# wait_activity PATTERN [TIMEOUT_S] — block until the resumed activity matches.
# The screenshots used to be taken while the app was still starting, which produced
# convincing black rectangles and a green "0 failures".
wait_activity() {
  local pattern="$1" limit="${2:-25}" i top
  for i in $(seq 1 "$limit"); do
    top=$(top_activity)
    case "$top" in
      *"$pattern"*) sleep 2; return 0 ;;
    esac
    sleep 1
  done
  say "  ! timed out waiting for $pattern (now: $(top_activity))"
  return 1
}

top_activity() {
  # The resumed activity, not the first entry of the stack: the history section
  # lists records in stack order and its first match can be the oldest one.
  local line
  line=$(adb shell dumpsys activity activities 2>/dev/null | grep -m1 "topResumedActivity")
  if [ -n "$line" ]; then
    echo "$line" | grep -oE "${PKG}/[A-Za-z0-9_.]+" && return 0
  fi
  adb shell dumpsys activity activities 2>/dev/null \
    | grep -m1 -oE "${PKG}/[A-Za-z0-9_.]+" || echo "none"
}

# The reader resolves the document from the MIME type; "*/*" is rejected, so the
# extension decides it here.
mime_for() {
  case "${1##*.}" in
    epub) echo "application/epub+zip" ;;
    pdf)  echo "application/pdf" ;;
    mobi|azw3) echo "application/x-mobipocket-ebook" ;;
    fb2)  echo "application/x-fictionbook+xml" ;;
    cbz)  echo "application/vnd.comicbook+zip" ;;
    *)    echo "application/octet-stream" ;;
  esac
}

# AppState is persisted on shared storage, not in the app's data dir, so
# `pm clear` does not reset it. RESET_PROFILE=1 makes a run start from defaults.
reset_profile() {
  [ "${RESET_PROFILE:-0}" = "1" ] || return 0
  adb shell rm -rf /sdcard/OmniShelf/profile.* 2>/dev/null
  say "  · profile reset"
}

open_book() {
  local path="$1" mime
  mime=$(mime_for "$path")
  adb shell am force-stop "$PKG" >/dev/null 2>&1
  sleep 1
  adb shell am start -a android.intent.action.VIEW \
    -d "file://${path}" -t "$mime" -n "$OPENER" >/dev/null 2>&1
  sleep 6
}

summary() {
  printf '\n---------------------------------------------\n'
  printf '%s: %d checks, %d failures\n' "${LABEL:-run}" "$CHECKS" "$FAILURES"
  printf 'screenshots: %s/%s\n' "$SHOTS" "${LABEL:-run}"
  [ "$FAILURES" -eq 0 ] || exit 1
}
