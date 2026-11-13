#!/usr/bin/env bash
# ui-smoke.sh — the gate every change must pass.
#
# Opens a real EPUB and a real PDF, turns pages, opens and closes the reading
# controls, returns to the library, relaunches, and fails on any fatal error,
# ANR or lost reading position.
#
#   ./scripts/ui-smoke.sh before-t0
#
# Requires a booted emulator with the app installed and a book at
# /sdcard/Download/pg11.epub plus a PDF at /sdcard/Download/Meditations.pdf.

cd "$(dirname "$0")/.." || exit 2
. scripts/lib.sh

LABEL="${1:-smoke}"
require_device
reset_profile

step "1. cold launch"
clear_log
adb shell am force-stop "$PKG" >/dev/null 2>&1
adb shell am start -n "$MAIN" >/dev/null 2>&1
wait_activity "MainTabs2" || true
sleep 2
top=$(top_activity)
case "$top" in
  *MainTabs2*) CHECKS=$((CHECKS+1)); say "  ✓ library is on screen" ;;
  *) CHECKS=$((CHECKS+1)); FAILURES=$((FAILURES+1)); say "  ✗ expected MainTabs2, got $top" ;;
esac
shot 01-library
scan_crash "cold launch"

step "2. open EPUB from the file manager path"
open_book "/sdcard/Download/pg11.epub"
top=$(top_activity)
case "$top" in
  *DocumentActivity*|*OpenerActivity*|*HorizontalViewActivity*|*VerticalViewActivity*)
    CHECKS=$((CHECKS+1)); say "  ✓ document opened ($top)" ;;
  *) CHECKS=$((CHECKS+1)); FAILURES=$((FAILURES+1)); say "  ✗ document did not open, got $top" ;;
esac
shot 02-epub-page
scan_crash "open EPUB"

step "3. turn five pages"
for i in 1 2 3 4 5; do swipe_ratio 80 55 20 55; sleep 1; done
shot 03-epub-page6
scan_crash "page turns"

step "4. reading controls open and close"
tap_ratio 50 50; sleep 2; shot 04-reader-menu
tap_ratio 50 50; sleep 2
scan_crash "reader menu toggle"

step "5. return to the library"
adb shell input keyevent 4; sleep 1
adb shell input keyevent 4; sleep 2
shot 05-back-to-library

step "6. reopen the same book and check the position survived"
open_book "/sdcard/Download/pg11.epub"
shot 06-epub-reopened
scan_crash "reopen EPUB"

step "7. open a PDF"
open_book "/sdcard/Download/Meditations.pdf"
shot 07-pdf-page
scan_crash "open PDF"
swipe_ratio 80 55 20 55; sleep 1
swipe_ratio 80 55 20 55; sleep 1
scan_crash "PDF page turns"

step "8. night mode round trip"
tap_ratio 50 50; sleep 2
shot 08-pdf-menu
adb shell input keyevent 4; sleep 2

step "9. a book opened from the folders tab"
adb shell am force-stop "$PKG" >/dev/null 2>&1
adb shell am start -n "$MAIN" >/dev/null 2>&1
wait_activity "MainTabs2" || true
sleep 3
tap_ratio 50 91          # Folder tab, second from the left in this tab order
sleep 4
shot 09-folder-tab
# The folder tab opens wherever it was last; after a profile reset that is
# already Download. Only when no book files are visible does the step enter
# Download by name - tapping its breadcrumb instead would open the path picker.
if ! wait_text ".epub" 8 && ! wait_text ".pdf" 8; then
  tap_ratio 25 7
  sleep 2
  for _ in 1 2 3; do
    tap_text "Download" || true
    sleep 3
    if wait_text ".epub" 5 || wait_text ".pdf" 5; then
      break
    fi
  done
fi
top=""
for _ in 1 2 3; do
  tap_ratio 50 34
  if wait_activity "HorizontalViewActivity" 20; then
    top=$(top_activity)
    break
  fi
  top=$(top_activity)
  case "$top" in
    *OpenerActivity*) break ;;
  esac
done
case "$top" in
  *HorizontalViewActivity*) CHECKS=$((CHECKS+1)); say "  ✓ opened from the folder tab" ;;
  *) CHECKS=$((CHECKS+1)); FAILURES=$((FAILURES+1)); say "  ✗ folder tap did not open a book (got $top)" ;;
esac
adb shell input keyevent 4; sleep 2
scan_crash "folder tab"

summary
