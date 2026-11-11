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

## Native libraries: how to build them locally

`Builder/link_to_mupdf_1.28.5.sh` clones MuPDF, patches two openjpeg files and runs
`ndk-build`. For one ABI (the emulator and the phone are both arm64) it takes about
50 minutes and roughly 1.5 GB of disk:

```bash
# NDK 27.2 (the version CI uses), unpacked anywhere writable
echo "librera_ndk_dir=/tmp/ndk-r27c/android-ndk-r27c" >> /tmp/gradle-home/gradle.properties

cp Builder/link_to_mupdf_1.28.5.sh Builder/link_arm64.sh
# in the copy: clone with --depth 1, comment out the three non-arm64 ndk-build lines
cd Builder && GRADLE_USER_HOME=/tmp/gradle-home bash link_arm64.sh
```

It writes `app/src/main/jniLibs/arm64-v8a/*.so` (21 MB for `libmupdf.so` alone), and after
that `assembleProDebug` produces an installable APK. Delete `Builder/mupdf-1.28.5` afterwards;
the libraries stay.

## Native libraries: what was broken before

`assembleProDebug` produces an APK **without** `libmupdf.so`, `libdjvu.so` and the rest of
the native set, because those are built by `Builder/link_to_mupdf_1.28.5.sh` (MuPDF source
+ NDK) and are not in the repository. Installing such an APK crashes on launch:

```
java.lang.UnsatisfiedLinkError: dlopen failed: library "libmupdf.so" not found
    at com.foobnix.pdf.info.AppsConfig.<clinit>(AppsConfig.java:94)
```

The `proJS` flavour (no MuPDF by design) gets further and then dies on `libdjvu.so`, so it
is not a substitute.

Consequences:

- Local builds are good for **compile checks only** (Java/XML/资源 errors, AAPT, lint).
- Anything that has to run must either be built in CI (which caches the MuPDF build) or
  after running `Builder/link_to_mupdf_1.28.5.sh` locally, which needs an NDK (not
  installed) and about 1.2 GB of disk for the NDK alone.

## Settings live outside the app (important for testing)

`AppState` is persisted to **`/sdcard/OmniShelf/profile.<channel>/device.<model>/app-State.json`**,
not to the app's private data directory. Consequences:

- `adb uninstall` and `pm clear` do **not** reset the theme, reading position or palette.
- A test that needs default settings must delete `/sdcard/OmniShelf/profile.*` first
  (`RESET_PROFILE=1 ./scripts/ui-smoke.sh ...`).
- Anyone upgrading from an older build keeps their saved accent colour, so a rebrand that
  changes the default only shows on a fresh profile.

## Emulator: working recipe

A clean AVD boots and shared storage works:

```bash
export JAVA_HOME=/tmp/jdk21b/jdk-21.0.13+9/Contents/Home
export ANDROID_SDK_ROOT=/tmp/android-sdk ANDROID_HOME=/tmp/android-sdk
export ANDROID_AVD_HOME=/tmp/avd4            # created by avdmanager, not the broken test33
/tmp/android-sdk/emulator/emulator -avd ui33 -no-snapshot -no-audio \
  -no-boot-anim -memory 2048 -gpu swiftshader_indirect
adb -s emulator-5556 shell ls /sdcard        # must list Alarms, Android, DCIM, ...
```

Two traps: the AVD's `image.sysdir.1` is written as `android-sdk/...` (relative to `/tmp`)
and must be changed to `system-images/android-33/...`; and `avdmanager` resolves the SDK
root to `/tmp`, so `/tmp/android-sdk` must contain symlinks to the real `build-tools`,
`platforms`, `licenses` and `cmdline-tools`.

Install the **arm64** split; the `-arm` APK fails with `INSTALL_FAILED_NO_MATCHING_ABIS`.

## Emulator: the original broken AVD

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
