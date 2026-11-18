#!/usr/bin/env bash
# restore-build-env.sh — reports what the build needs and, when it is all there, builds.
#
# The build is offline-hostile: Gradle downloads its own distribution, then AGP and every
# dependency from Google and Maven Central. This script checks each prerequisite and says
# exactly what is missing instead of failing with a stack trace.
set -uo pipefail

echo "== JDK =="
JAVA_BIN="${JAVA_HOME:-}/bin/java"
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_BIN" ]; then
  "$JAVA_BIN" -version 2>&1 | head -2
  VER=$("$JAVA_BIN" -version 2>&1 | head -1 | grep -oE '[0-9]+' | head -1)
  case "$VER" in
    17|18|19|20|21) echo "  ok: JDK $VER is in the supported range" ;;
    *) echo "  WARN: JDK $VER — this project is built with JDK 17-21; 25 may fail with AGP" ;;
  esac
else
  echo "  MISSING: set JAVA_HOME to a JDK 17-21 (e.g. Temurin 21)"
fi

echo "== Android SDK =="
SDK="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
for part in platform-tools build-tools platforms/android-37.0 system-images; do
  if [ -e "$SDK/$part" ]; then echo "  ok: $part"; else echo "  MISSING: $SDK/$part"; fi
done

echo "== network (the build cannot start without these) =="
for u in https://services.gradle.org https://dl.google.com https://repo.maven.apache.org; do
  code=$(curl -s -o /dev/null -m 8 -w "%{http_code}" "$u" 2>/dev/null || echo 000)
  echo "  $u -> $code"
done

echo "== gradle cache =="
GRADLE_HOME_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}"
if [ -d "$GRADLE_HOME_DIR/caches" ]; then echo "  ok: $GRADLE_HOME_DIR/caches"
else echo "  empty: first build will download ~500 MB"; fi

echo
echo "== build =="
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_BIN" ] && [ -d "$SDK/platforms/android-37.0" ]; then
  GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}" \
  ANDROID_USER_HOME="${ANDROID_USER_HOME:-$HOME/.android}" \
  ./gradlew :app:assembleProDebug -x lint --no-configuration-cache "$@"
else
  echo "  skipped: prerequisites missing (see above)"
fi
