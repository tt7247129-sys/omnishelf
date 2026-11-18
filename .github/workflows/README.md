# CI

`build.yml` compiles `:app:assembleProDebug` on every push and uploads the APK as an
artifact. Two things worth knowing:

1. **The native libraries are not in the repository** (`app/src/main/jniLibs` is ignored,
   ~150 MB). The Java compiles without them and the APK builds, but opening a book without
   MuPDF's `.so` files will fail at runtime. To produce a working APK, run
   `Builder/link_arm64.sh` locally first (needs the NDK, ~50 minutes) — or add a job that
   installs the NDK and runs the same script.
2. **Signing** uses the debug key unless `RELEASE_STORE_FILE` and friends are passed. The
   store upload must be signed with the key Play already has (see
   `delivery/DELIVERY-NOTES.md`).
