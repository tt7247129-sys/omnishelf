#!/usr/bin/env bash
# Regenerates the landing page's QR code from the current download URL.
#   ./scripts/make-site-qr.sh [url]
set -euo pipefail
cd "$(dirname "$0")/.."
JAVAC="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}/bin/javac"
JAVA="${JAVA_HOME:-/Applications/Android Studio.app/Contents/jbr/Contents/Home}/bin/java"
ZIP=$(ls /tmp/deps/zxing-core.jar 2>/dev/null || true)
URL="${1:-https://omnishelf.cc/go/play/?src=qr}"
mkdir -p web/public/qr
"$JAVAC" -cp "$ZIP" -d /tmp/qrbuild web/tools/QrSvg.java
"$JAVA" -cp "$ZIP:/tmp/qrbuild" QrSvg "$URL" 8 web/public/qr/play.svg
