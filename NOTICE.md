# NOTICE

OmniShelf is a modified version of **Librera Reader**.

| | |
|---|---|
| Upstream project | [LibreraReader](https://github.com/foobnix/LibreraReader) |
| Upstream author | Ivan Ivanenko (foobnix) and contributors |
| Upstream license | GNU General Public License v3.0 or later |
| Base revision | master snapshot, 2026-11-07 (`f043c00`) |
| First modified | 2026-11-07 |

**OmniShelf is not the official Librera app.** It is not affiliated with, endorsed by,
sponsored by, or approved by Librera's author. "Librera" is the trademark of its
respective owner; this project does not use that name, icon, or visual identity.

---

## Statement of changes (GPLv3 §5(a))

The following substantive modifications have been made to the upstream work.
Each release appends to this list — see [CHANGES.md](CHANGES.md) for the dated log.

| # | Change | Scope |
|---|---|---|
| 1 | Renamed the application to OmniShelf; new launcher icon and adaptive icon | `res/values*/`, `res/mipmap-*`, `res/drawable/` |
| 2 | Changed `applicationId` to `cc.omnishelf.reader`; new URL scheme `omnishelf` | `app/build.gradle`, `AndroidManifest.xml` |
| 3 | Removed the google-services Gradle plugin (no Firebase or Google OAuth in this build) | `app/build.gradle` |
| 4 | Replaced the undefined `pro_appGdriveKey` placeholder with an empty value | `app/build.gradle` |
| 5 | Removed advertising meta-data from the manifest | `AndroidManifest.xml` |
| 6 | Removed the Google Drive entry from the library panel (cloud sync is not offered) | `PrefFragment2.java` |
| 7 | Replaced the notification icon and removed `LibreraX` cross-promotion links | `res/drawable/`, `res/values*/` |
| 8 | Replaced all upstream website, store, and contact links with own equivalents | `res/values*/`, `res/layout*/`, `*.java` |
| 9 | Replaced the privacy policy, README, and project documentation | repository root |

This list is a summary of the **first** release. Later releases add rows.

---

## Third-party components

OmniShelf includes third-party software, most notably:

- **MuPDF** — Copyright © Artifex Software, Inc. Dual-licensed under the
  [GNU AGPLv3](https://www.gnu.org/licenses/agpl-3.0.html) or a commercial license.
  This project uses the AGPLv3 option. MuPDF is compiled from source at build time;
  see `Builder/link_to_mupdf_1.28.5.sh`.
- **PDFium** (`io.legere:pdfiumandroid`) — BSD-style license.
- **DjVuLibre**, **libmobi**, **antiword**, **libarchive**, **libwebp**, **LAME** —
  see the individual `Builder/jni/*/Android.mk` for versions and licenses.

Because MuPDF is used under the AGPLv3, **the entire application is distributed under
the AGPLv3**, and the complete corresponding source is published with every release.

---

## Source code

Complete corresponding source for every published build:

**https://github.com/tt7247129-sys/omnishelf**

Under GPLv3 §6(d), anyone who receives a binary of this application is entitled to
obtain the corresponding source from the same place, at no further charge.

---

## License

- Full AGPLv3 text: [LICENSE](LICENSE)
- Full GPLv3 text: [LICENSE-GPL-3.0.txt](LICENSE-GPL-3.0.txt)
- Original upstream notice, preserved verbatim: [LICENSE-LIBRERA-NOTICE.txt](LICENSE-LIBRERA-NOTICE.txt)

## No warranty

```
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
```
