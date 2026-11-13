---
title: "Proving a screen is readable without looking at it"
summary: "A hierarchy dump tells you what is on the screen, never what it looks like. When the tooling cannot view a screenshot, the contrast of a label becomes a number you can measure from raw pixels - and the measurement caught a 1.8:1 failure that every earlier check had missed."
date: "2026-11-15"
order: 12
readingMinutes: 6
---

Testing a reader's interface without a pair of eyes means solving, over and over, the
question of what a screenshot can and cannot tell you. For a long time the answer here
was simple: the hierarchy dump carries every text, every control, every description.
What it does not carry is colour.

So when someone asked whether the empty-shelf message stays readable in night mode,
the honest answer was "probably". The theme says black background, the platform theme
says light text, and probability is not verification.

## The measurement

The screen is 1080 by 1920. The dump gives the exact bounds of the title. A screenshot
gives the pixels. Between them sits a very short script: take the band inside the
bounds, take the band next to it, compare luminance. The title's glyph pixels measured
179 on a background of 0. That is a contrast ratio around fourteen to one. Not
"probably readable" - readable, measured.

The same script, pointed at the continue-reading card in the light theme, returned
1.8 to 1. The green label sat on a translucent card over a green bar: the brand colour
had cancelled the brand colour, and no earlier check noticed, because no earlier check
looked at colour. The fix was to stop painting the label green and let it inherit the
theme's text colour, which is the one colour the design already guarantees in both
modes.

## The reversal

The expectation was that the hard part of visual testing is eyes. The actual finding is
that a small, boring measurement catches failures eyes would have needed luck to see -
a label one point eight times brighter than its background looks like a misprint, not
an oversight. And the measurement scales: once the script exists, every future colour
question is one run away, including the ones nobody has asked yet.

## What this does not solve

Pixel sampling measures text against its background. It says nothing about whether the
text is the right words, whether a glyph is a tofu box, or whether a layout is
beautiful. A screen can pass every contrast measurement and still be wrong. The
measurement is a floor, not a review.

## The product decision

A reading application is mostly text on a background. Contrast is the one visual
property that determines whether the product works at all for a large share of its
users. It deserves the same treatment as every other claim in the codebase: verified,
with a number, or not claimed.
