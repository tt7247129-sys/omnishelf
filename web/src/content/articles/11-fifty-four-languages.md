---
title: "Fifty-four languages and the line count that rules them"
summary: "Adding a language to this app is not adding a directory. It is translating 724 lines and landing every one of them at the same line number as the English, because the build refuses any file that disagrees. The rule looks bureaucratic. It caught things a reviewer would have missed."
date: "2026-11-14"
order: 11
readingMinutes: 6
---

Every localisation workflow has a moment where someone asks whether the app could
"just fall back to English" for the languages it does not know. Ours cannot, and the
reason is a line count.

The build script walks every `values-*/strings.xml` and compares its length with the
English master. One file with 723 lines against 724 fails the build with a single
sentence: *values-zh-rTW/strings.xml has 723 lines, values/strings.xml (en) has 724.*
No diff, no hint about which line. Just the count.

## What the rule does not say

The rule says nothing about quality. A file can pass with every string untranslated,
pasted English, as long as the lines match. That is the whole reason the project
carries a second, unwritten rule: never paste English into a translation file. The
machine can check the count; only a human can check that the Telugu for *font size*
is not quietly the English for *font size*.

So adding a language means producing a real, complete translation of 724 lines — every
setting name, every error message, every accessibility description — and landing each
line at exactly the position of its English twin, because the next developer diffing
the files reads them side by side, and a line that drifted is a line nobody will ever
maintain.

## The reversal

We expected the hard part of shipping twelve more languages to be the mechanism:
registering codes, wiring the picker, testing the switch. That took an afternoon and
worked the first time, verified by setting a device to Bengali and watching the tabs
change. The hard part was the translation itself: twelve files, 8,688 lines, each of
which had to be a translation and not a placeholder.

Twelve translators running in parallel could do it — one file each, checked against
the same rules — and the interesting failure was not a mistranslation. It was
format placeholders. A string like *"Page %1$s of %2$s"* translated with the
arguments swapped, or with one dropped, renders garbage or crashes a formatted
string. We validated every line's `%` sequences against the English before anything
was committed. Every file came back clean, which is either a sign the instructions
were good or a sign the validation was too weak. The build is the final judge, and
the build passed.

## What this does not solve

The line-count rule cannot judge tone. German uses the formal *Sie*; Portuguese here
is Brazilian, *tela* not *ecrã*; a machine translation service will happily produce
both and call it coverage. The rule guarantees structure, not voice. Anyone adding a
language to this codebase inherits both obligations: the count, which the build
checks, and the voice, which nothing checks but a reader of the language.

## The product decision

A reader application either speaks a language or it does not. An honest English
fallback is a product decision; a half-translated settings page is a defect. The
line-count rule exists to make the difference visible — a file that does not match
is a file that is not finished, and the build says so on behalf of every user who
would have met the unfinished half.
