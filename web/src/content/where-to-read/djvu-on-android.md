---
title: "DJVU on Android: the format that scans better than PDF"
description: "DJVU exists because scanned pages are images, not text, and PDF stores them badly. The format is smaller, faster on old phones, and supported by fewer readers than it deserves."
category: "Where to Read"
pubDate: "2026-11-20"
authorNote: "Kept DJVU support in the fork because scanned academic books still arrive in this format."
readingMinutes: 7
related: ["choosing-an-android-ebook-reader"]
---

A DJVU file is a scanned book encoded for the way scans actually look: mostly white paper,
a little ink, and a background that repeats. The format was designed at AT&T in the 1990s
for exactly this, and for a certain kind of book it still beats PDF on a phone by a wide
margin.

## Where DJVU still wins

A 300-page scanned academic book as PDF is often 60–200 MB. The same book as DJVU is
typically 5–20 MB, and the pages render faster on an old device because the decoder is
doing less work. On a phone with 3 GB of RAM and a mid-range chip, that difference is the
difference between a usable book and a spinning progress indicator.

DJVU also handles text layers better in one specific case: OCR applied to a scanned page
can be stored alongside the image, so the book remains searchable without being
re-typeset.

## Where it loses

Almost everywhere else. There is no mainstream way to create a DJVU file — you need
`djvulibre` and a scan workflow. Editing is not a thing. Text selection works only when an
OCR layer exists, and even then the layout of the text is not preserved. And the number of
Android apps that open it is small, which is the format's real problem.

## Which Android readers open DJVU

Librera and its fork OmniShelf both open DJVU, using a native decoder. KOReader supports
it on most builds. Moon+ Reader does not. If DJVU is part of your library, that single row
narrows the choice to two or three apps.

## Converting DJVU to PDF

Possible, and a reasonable choice if you must use a reader without DJVU support — the
usual tool is `ddjvu` from djvulibre:

```
ddjvu -format=pdf -quality=85 book.djvu book.pdf
```

Expect the file to grow three to ten times. The scanned pages are now stored as images with
less efficient compression, which is precisely the problem DJVU was invented to solve.

## When a phone is the wrong device for the file

A 300-page A4 scan is not comfortable on a 6-inch screen regardless of format. If you read
DJVU regularly, an e-ink device or a tablet will serve you better than any phone app — the
format is fine, the screen is the constraint. For occasional use, a reader that opens DJVU
without converting is enough.
