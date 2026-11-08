---
title: "Shelf — what is on it, and what changed"
description: "The state of the OmniShelf reader: closed testing on Google Play, what is finished, what is not, and a short changelog of the decisions that altered it."
eyebrow: "Shelf"
---

# What is on the shelf

One app, in closed testing. This page is the honest state of it — what works, what does
not yet, and what changed recently. It is not a feature list; the store listing has that.

## The app

OmniShelf for Android, version 1.0.0, is in closed testing on Google Play. When the
testing period ends it will be published publicly, free, with no advertising and no
purchases. If you are one of the testers, install through the invitation link you were
sent rather than searching the store — a closed track is invisible to search until it
opens.

The source is public and the licence is the AGPLv3:
[github.com/tt7247129-sys/omnishelf](https://github.com/tt7247129-sys/omnishelf).

## What works today

Opening and reading EPUB, PDF, MOBI, AZW3, FB2, DJVU, CBZ/CBR, DOCX, ODT, RTF, Markdown,
HTML and plain text. A single library that mixes them. Reading progress that survives a
restart. Highlights, notes, bookmarks, text-to-speech with the device voice, and OPDS
catalogue browsing. Night mode, adjustable margins, spacing and alignment.

## What is not finished

Cloud sync is not offered, deliberately: it would require an account, and an account
would mean the app holds something about you. Widget behaviour on unusual launchers is
untested. Some right-to-left layouts still break on the two-page mode. These are recorded
here rather than discovered by a reviewer.

## Changes

### November 2026 — closed testing begins

Version 1.0.0 is in closed testing on Google Play. The reader is complete enough to read
with: EPUB, PDF, MOBI, CBZ, TTS, highlights, bookmarks and an OPDS catalogue browser. It
is not complete enough to call finished, which is why the testing track exists.

### November 2026 — the paid-app entry was removed

An inherited menu entry opened a different, paid reader by the upstream author, labelled
with our own name. It is gone, together with the settings row and the method behind it.
No path in the app now leads to a purchase.

### November 2026 — folders stopped being called Librera

The rebrand missed paths built by string concatenation: the sync folder, the TTS scratch
folder and the stylesheet name still carried the old name into the visible file system.
They now read OmniShelf.
