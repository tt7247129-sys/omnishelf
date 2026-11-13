---
title: "The button that could never show"
summary: "A row promising to turn off ads in exchange for a video survived the fork for months, invisible because the flag that feeds it always says no. Hidden is not gone: dead monetisation is a promise waiting to contradict itself, and removing it is part of keeping the promise."
date: "2026-11-17"
order: 14
readingMinutes: 6
---

A free reader with no ads makes one promise, and a codebase makes it thousands of times:
every row that could show an advertisement, every hook that could count a click, every
stub that once waited for a network. Most of them are dead by the time the fork happens -
guarded by a flag that always evaluates to false. Dead code does not show up in the UI,
so for a long time nobody looked at it.

## The row

The reader's footer carried a row: watch a short video, turn off ads for three hours.
It sat in two layouts, with its own icon, its own text, its own binder method, its own
click handler wired to a reward callback. None of it could ever appear, because the
decision that governs it - `isShowAdsInApp` - returns false unconditionally in this
build. The row was a promise the product had already broken, preserved in amber.

## The reversal

The expected story is that hidden things stay hidden. The actual risk is subtler: a
future change to the flag, an innocent refactor of the guard, a test that exercises the
branch - any of these would have made the row appear, and the appearance would have been
a bug that no one remembered was possible. Hidden is not gone. It is deferred.

The removal was mechanical: two layout blocks, one binder, two call sites, two fields.
The interesting part is what stayed. The reward method itself remains as a stub, because
the no-ads policy lives in that flag, and the stub is the policy's only source of truth.
Removing the UI and keeping the gate is the same move as removing the symptoms and
keeping the diagnosis.

## What this does not solve

There are other residues of the same shape: promotional cards for other apps, strings
that describe features the build does not have, entries in menus that lead nowhere.
Each one needs the same audit - what would have to become true for this to appear, and
who would notice. The audit is not glamorous. It is the difference between a promise
implemented and a promise asserted.

## The product decision

"No ads" is a product claim. In code, it is a property of the whole tree, not of the
screens that happen to render. A reader that cannot show an ad, even by accident, keeps
the promise better than one that merely chooses not to.
