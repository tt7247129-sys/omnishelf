# Build environment (this machine)

Everything below is outside the repository, which means it is **not reproducible from a
fresh clone** and disappears when `/tmp` is cleaned. Recorded here so the next session does
not rediscover it.

## Working combination (verified: `assembleProDebug` succeeded)

| Variable | Value | Why |
|---|---|---|
| `JAVA_HOME` | `/tmp/jdk21b/jdk-21.0.13+9/Contents/Home` | JDK 21 (Temurin). `~/.dsh` and `/tmp/jdk21` were both broken — `/tmp/jdk21` is missing `lib/jvm.cfg`. |
| `GRADLE_USER_HOME` | `/tmp/gradle-home` | `~/.gradle` is not writable in this sandbox; the Gradle wrapper cannot create its lock file there. |
| `ANDROID_USER_HOME` | `/tmp/android-home` | The debug keystore is written to `$ANDROID_USER_HOME`; `~/.android` is not writable. |
| `sdk.dir` (`local.properties`) | `/Users/huangpeng/Library/Android/sdk` | The SDK in `/tmp/android-sdk` is **incomplete** (no `android.jar`, no `aapt2`, no `sdkmanager`). The one under `~/Library/Android/sdk` is complete. |

`local.properties` is git-ignored, so this path is local-only; CI uses its own SDK.

## Commands

```bash
cd omnishelf
export JAVA_HOME=/tmp/jdk21b/jdk-21.0.13+9/Contents/Home
export GRADLE_USER_HOME=/tmp/gradle-home
export ANDROID_USER_HOME=/tmp/android-home

./gradlew :app:assembleProDebug -x lint --no-configuration-cache   # ~2.5 min warm
```

Output: `app/build/outputs/apk/pro/debug/OmniShelf Pro-1.0.0-mupdf-arm.apk`.

Do **not** set `ANDROID_SDK_HOME`; AGP 9 treats it as the SDK root and fails with
`Could not create provider for value source AndroidLocationsBuildService`.

## Emulator: known-broken on this machine

The AVD at `/tmp/avd/test33.avd` was created with **96 MB RAM** and a temporary data
partition, and its shared storage (`/sdcard`, `/storage/emulated/0`) fails with
`Transport endpoint is not connected`. Raising `hw.ramSize` to 2048 and pointing
`disk.dataPartition.path` at a real file did not fix the boot: adb stays `offline`.

What was tried, in order, each failing at a different stage:

1. Cold boot with `-wipe-data` → boots, but `/sdcard` is broken.
2. `mksdcard` 512 MB + `-sdcard` + `-partition-size 4096` → boots to `offline`.
3. `hw.ramSize = 2048`, persistent data partition → still `offline`.

Next things to try, in the order that seemed most promising:

- Build a fresh AVD from `~/Library/Android/sdk/system-images/android-37.0/google_apis_playstore/arm64-v8a`
  (that SDK has the complete emulator and the image; it lacks `cmdline-tools`, so the AVD
  has to be written by hand or `cmdline-tools` re-downloaded).
- Free disk first: the volume is at 98% (≈11 GB free). `-wipe-data` on a 4 GB partition
  plus a 512 MB SD card may simply be failing to allocate.
- Drop `-gpu swiftshader_indirect` and try `-gpu auto` on a machine that has a real GPU.

Until a device is available, visual verification is impossible: the UI work in
`docs/UI-PLAN.md` must not be merged without the screenshot matrix it defines.
