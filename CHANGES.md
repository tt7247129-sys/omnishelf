# Changelog

All notable changes to this fork, relative to upstream.
Required by GPLv3 §5(a): a prominent statement of modifications and their dates.

Upstream project: [LibreraReader](https://github.com/foobnix/LibreraReader)

---

## 1.0.0 — 2026-11-07

First release. Independent fork of Librera Reader `master` (2026-11-07, `f043c00`).

### Branding

- Renamed the application to **OmniShelf**.
- New launcher icon, adaptive icon foreground and background, and notification icon.
  The upstream Librera artwork is no longer shipped.
- Replaced the app name in all 48 translation files (same line count as English,
  so upstream's translation-consistency build check still passes).
- New URL scheme `omnishelf` (upstream used `librera`).
- Replaced every upstream website, store, Telegram and contact link.

### Identity

- `applicationId`: `com.foobnix.pro.pdf.reader` → `cc.omnishelf.reader`
- `versionCode` / `versionName`: reset to `1` / `1.0.0`
- Only the `pro` product flavour is published. It carries no AdMob SDK.

### Removed

- **Every path to the upstream author's paid app.** Upstream offered it from the overflow
  menu (labelled with the app's own name), from a row in the settings screen, and through
  `Urls.openPdfPro()`. All three are gone, and `AppsConfig.isShowAdsInApp()` now always
  returns false. As a side effect `isRewardActivated()` is always true, so features that
  upstream locked behind watching a rewarded ad are simply unlocked here.
- **A second permission dialog that duplicated upstream's own.** Upstream already explains
  "All files access" with a dialog before asking, so the extra explanation added here only
  made two prompts appear in a row; the direct jump is restored.
- **Storage folders still named "Librera"** (`/sdcard/Librera`, `Downloads/Librera`, the SAF
  profile and the Drive root). They are created by string constant and by concatenation, so
  the first rebrand pass missed them.

- **Google Drive entry** in the library panel. `AppsConfig.isCloudsEnable` is
  hard-wired to `false` upstream, so the entry only ever opened an empty screen.
  It is now hidden for every flavour, not just `fdroid`.
- **LibreraX cross-promotion** texts that advertised another app by the upstream author.
- **AdMob meta-data** (`com.google.android.gms.ads.APPLICATION_ID`) from the manifest.
- **google-services Gradle plugin.** The repository ships no `google-services.json`,
  so the plugin made every clean clone fail to configure.

### Fixed

- **31 manifest placeholders referenced Gradle properties that do not exist in the
  repository.** They live only in the upstream author's local `~/.gradle/gradle.properties`
  (`pro_appGdriveKey`, `librera_admobAppId`, `pdf_classic_*`, `ebooka_*`, `pdf_v2_*`,
  `tts_reader_*`, `epub_reader_*`). Gradle evaluates every product flavour at
  configuration time, so **any** missing one made **every** build fail — including
  `pro`, the only flavour this project ships. All now default to empty strings.
- **Removed `gradle/gradle-daemon-jvm.properties`.** It pinned the Gradle daemon to a
  JetBrains-built JDK 21 and made every clean build download a ~200 MB vendor-specific
  runtime from a slow host. Any JDK 21 works for this project.
- Removed the remaining cross-app promotion entry points (open-failure dialog, share
  menu, settings export button and promo card). After rebranding, their strings read
  "OmniShelf is not installed" while the user was running OmniShelf.

### Build

- GitHub Actions workflow that builds a signed APK and AAB, with the MuPDF native
  build cached between runs (`.github/workflows/build.yml`).
- GitHub Pages workflow for the website and privacy policy (`.github/workflows/pages.yml`),
  published from `site/`.

### Verified

- `./gradlew :app:help` configures the project successfully with JDK 21 and
  `compileSdk 37` — this is the check that surfaced the 31 placeholders above.
- Full compilation was **not** completed in the development environment (slow/locked-down
  network halted dependency resolution); it is expected to run on GitHub Actions instead.

### Documentation

- New `README.md`, `PRIVACY.md`, `NOTICE.md`.
- Full AGPLv3 and GPLv3 license texts added; upstream's original notice preserved
  verbatim as `LICENSE-LIBRERA-NOTICE.txt`.

### Known issues

- Cloud sync is unavailable and not offered. Enabling it would require our own Google
  OAuth client and would change the privacy posture of the app.
- `DOCX` requires Android 8.0 (API 26) or newer.
