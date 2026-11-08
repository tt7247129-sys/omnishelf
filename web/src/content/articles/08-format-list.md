---
title: "Thirty-four formats is not a feature, it is a promise about failure"
summary: "A format list is usually read as a score. It is better read as a statement about which parsers will run on your file, which of them are native code, and how the app behaves when the file is malformed."
date: "2026-11-08"
order: 8
readingMinutes: 8
---

Every reader app publishes a list of supported formats, and every list is read the same
way: longer is better. The number is treated as a capability score, as though opening
forty formats were meaningfully different from opening thirty.

It is not a score. It is a statement about which parsers will run on your file, and each
of those parsers has its own failure mode.

## What the number actually counts

Count the extensions and one reader will report thirty-four. Count the engines behind
them and the number is closer to eighteen, because several formats share a parser: the
same engine reads MOBI, AZW, AZW3, AZW4, PDB and PRC; another reads CBZ and CBR because
both are archives full of images.

Extensions are cheap. A single parser that handles six Kindle variants produces six
entries in the list for one piece of engineering. That is not dishonest — those are six
real formats a reader might own — but it explains why the number grows faster than the
capability does.

## The four kinds of document

Behind the list there are four genuinely different problems, and they behave nothing
alike:

**Fixed-layout documents.** PDF, XPS, DJVU, comic archives. The page is the unit. Zoom,
crop and rendering quality matter; reflow is an optional transformation that has to be
reversible, because the original layout is the document's identity.

**Reflowable documents.** EPUB, FB2, MOBI. There is no page until the reader makes one.
Typography, hyphenation and progress tracking are the whole experience, and the file's
own CSS may fight the app's settings.

**Office documents.** DOCX, DOC, ODT, RTF. Converted to something readable, not rendered
faithfully. A DOCX with complex tables will not look like it did in Word, and pretending
otherwise is a support burden.

**Containers and plain text.** ZIP, TXT, HTML, Markdown. Trivial to parse and easy to get
subtly wrong — encoding, line endings, and the fact that a ZIP may contain a book or may
contain something else entirely.

## Why some formats are conditional

DOCX support depends on an Android API level, because the conversion library requires
one. On older devices the format is simply absent from the app's list — not broken, not
silently failing, but not offered.

This is the correct behaviour and it is unpopular. A user on an older phone reads "DOCX"
on the store listing, installs, and finds the format missing. The alternative — offering
it and failing at open time — is worse, but only slightly.

The honest fix is to say so where the list appears: DOCX requires Android 8 or newer.
That is a sentence in the store description and a line in this note, and it is the only
place the constraint is visible.

## Native code changes the risk

Several parsers are native libraries: C code compiled per architecture, reading a file
format with a long history of malformed samples. A parser bug in Java throws an
exception. A parser bug in C can take the whole process down.

This is why the reader opens documents defensively: a failed open produces a dialog and a
retry, not a crash that returns the user to the home screen with no explanation. It is
also why the app records that a file crashed it last time and warns before trying again.

The format list, read this way, is a list of parsers that can crash. Thirty-four is not
a boast; it is thirty-four opportunities for a malformed file to find a bug.

## What the list should not be used for

It should not be the first thing on a store page, and it should not be the reason to
choose one reader over another, because the formats most people actually read — EPUB,
PDF, and one comic archive — are supported by every serious reader.

The list matters at the edges: the FB2 library someone inherited, the DJVU scan of a
book that was never digitised properly, the AZW3 bought in 2012. Those are the files that
decide whether a reader is useful to a particular person, and they are the reason the
list is long.

## The formats that only work if a native library is present

DJVU and MOBI are the clearest examples. Both are parsed by C libraries compiled for each
CPU architecture, and both are the reason the app's download size differs by architecture
rather than being a single universal file.

A build that omits those libraries does not fail gracefully at the point of use. It fails
at the point of install, or it ships a UI that offers a format it cannot open. The
project's answer is to ship every engine in every build and accept the size, because a
reader that opens "most" formats is a reader whose format list is a guess.

That decision has a cost that is easy to state: the universal package is roughly a
hundred and thirty megabytes, about seventy of which are native libraries for four CPU
architectures. Per-device delivery through the store cuts that to the architecture the
device actually has, which is the only reason the size is acceptable at all.

## What happens when a format is dropped

Every format carried costs maintenance: a parser to keep building, a set of malformed
samples to test against, and a rendering path to verify on each release.

When a format is dropped — because the library is unmaintained, or because it crashes on
enough files that shipping it is worse than not shipping it — the honest move is to say
so in the release notes rather than to leave the extension in the list and let it fail at
open time.

This has not happened in this project yet. It will. The list is a liability as much as an
asset, and treating it as a scoreboard makes the eventual removal feel like a regression
instead of maintenance.


The next note is about the other end of that risk: what the app does when a file will not
open at all.
