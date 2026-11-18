---
title: "EPUB vs MOBI vs AZW3: which one should you keep"
description: "Three formats, one is a container of web pages, one is a 1990s Palm document, one is Amazon's version of the first. What each stores, who can open it, and when converting is worth the trouble."
category: "Where to Read"
pubDate: "2026-11-22"
authorNote: "Opens all three daily and has opinions about which one survives a decade."
readingMinutes: 7
related: ["kindle-books-on-android", "choosing-an-android-ebook-reader"]
---

If you are rebuilding a library and can keep only one format, keep **EPUB**. Everything
below explains why, and what you lose.

## What each format actually is

**EPUB** is a ZIP archive of XHTML, CSS and images with a manifest. The text reflows: the
same file looks right on a phone, a tablet and an e-ink reader. It supports embedded fonts,
SVG, footnotes, and — in EPUB 3 — audio and maths. It is an open standard maintained by the
W3C, which is why no single company can withdraw support for it.

**MOBI** is a format from the Palm era, adopted by Mobipocket and then by Amazon. It is a
single binary with HTML inside, minimal styling control, and known limits on images and
tables. Amazon deprecated it for new purchases years ago.

**AZW3** is Amazon's successor to MOBI: essentially Mobipocket with better CSS support and
embedded fonts. It is a better MOBI, not a portable standard. Outside Amazon's ecosystem,
support depends on readers reverse-engineering it — many do, well enough that unprotected
AZW3 files open fine in third-party apps.

## The practical comparison

| | EPUB | MOBI | AZW3 |
|---|---|---|---|
| Reflows to any screen | yes | yes, roughly | yes |
| Embedded fonts | yes | limited | yes |
| Tables and complex layout | good | poor | better |
| Footnotes and pop-ups | yes | awkward | yes |
| Open standard | yes | no | no |
| Reader support on Android | universal | wide | wide |
| Risk of losing support | lowest | highest | medium |

## What "converting" really costs

Converting between reflowable text formats is usually lossless enough that nobody notices.
Converting MOBI to EPUB with Calibre keeps chapters, footnotes and images; the main losses
are the exact styling choices and sometimes the table of contents. Converting the other way
is where quality falls: EPUB features that MOBI cannot express are dropped silently.

Fixed-layout books — comics, cookbooks, children's books with text as images — are a
different matter. Converting those usually means rasterising pages, which is why the
comic formats exist separately (see the CBZ guide).

## When to convert, and when not to

**Convert** if you have a pile of MOBI files and want one library in one format, or if your
reader of choice handles EPUB best.

**Do not convert** if the book is fixed-layout, if it is a scan (keep PDF or DJVU), or if
the file already opens correctly — conversion is a risk you take for no gain.

**Never convert** a DRM-protected purchase with a third-party tool: the conversion requires
breaking the lock, which is a separate legal question from format support.

## The ten-year test

Ask which file still opens in 2036. EPUB, as a W3C standard with dozens of independent
implementations, is the safest bet. A MOBI file depends on a single company's continued
tolerance for an old format — and Amazon already stopped selling them.

If you are choosing what to archive, choose EPUB. Keep the original as a backup, and keep a
reader installed that was designed around open files rather than a store.
