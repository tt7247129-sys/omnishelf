---
title: "CBZ and CBR on Android: what the format is and which readers open it"
description: "A CBZ is a ZIP file of images with a misleading name. That explains both why comic readers on phones look strange and why some apps refuse to open a file that is, technically, a valid archive."
category: "Where to Read"
pubDate: "2026-11-19"
authorNote: "Added comic archive support so that image-only CBZ files open without converting them first."
readingMinutes: 7
related: ["choosing-an-android-ebook-reader"]
---

Comic archives are the most misunderstood format on Android. A CBZ is a ZIP archive of
image files, optionally with a `ComicInfo.xml` describing the series, renaming a `.zip` to
`.cbz`. A CBR is the same thing in a RAR archive. There is no DRM, no index, no table of
contents — just pages, in alphabetical order.

That simplicity is why comic archives are portable and why readers disagree about them.

## Why some apps refuse a valid archive

Three reasons, all common:

**The extension filter.** An app that only recognises `epub`, `pdf` and `txt` will not
show the file at all, even though it could open it as a ZIP of images.

**The page order.** Archives are read in natural sort order, so `page10.png` sorts before
`page2.png` in a naive numeric sort. Good readers sort naturally; bad ones show the pages
in an order that looks random.

**The memory.** A 200-page comic at 2,000 pixels wide is not a document; it is a photo
album. Readers that decode every page into memory run out of it. Readers that decode one
page at a time do not.

## What to check before committing to a reader

Open a comic with at least 20 pages and check four things:

1. Does it appear in the file list without renaming the extension?
2. Are the pages in the right order?
3. Does turning pages forward and backward stay instant after the tenth page?
4. Does it remember the page you stopped at, per file?

The fourth point is where most readers fail quietly. A comic is read over many sessions in
short bursts; if the reader always starts at page one, you will stop using it.

## Should you convert instead?

Converting CBZ to PDF solves nothing on a phone — you trade one well-understood format for
a heavier one. Converting to EPUB is worse: comics are not text, and the fixed-layout EPUB
container adds a specification without adding a feature.

The only conversion worth doing is renaming `.cbr` to `.cbz` when the archive is actually a
ZIP mislabelled by an old tool. Check with any archive viewer: if it opens as a normal ZIP,
rename it.

## Which Android readers handle comics

Moon+ Reader, KOReader, Librera and OmniShelf all open CBZ and CBR. The differences are in
page order, page-turn speed with large images, and whether the reader remembers your place
per file. On a phone, the screen is small enough that a reader which fits the page to width
and lets you double-tap to zoom is usually more comfortable than one with elaborate
panel-by-panel navigation.

If your library is comics *and* books, choose the reader that does both well rather than
installing two — switching apps for a format is friction you pay for every session.
