---
title: "The test that could not be automated"
summary: "Four stages, each needing a beat the device could not guarantee, and a permission prompt that hijacked every tap. The bookmark flow works - it is verified by hand. What failed was the attempt to make the machine say so, and the honest move was to let the gate stay smaller."
date: "2026-11-16"
order: 13
readingMinutes: 6
---

A smoke suite is a promise: every green run says these paths still work. The moment a
check fails for reasons that are not the product's, the promise breaks in the worst
way - the run reports a bug where there is none, and the people reading the report
learn to distrust it.

The bookmark flow taught this lesson in four stages.

## The flow

Adding a bookmark in the reader takes four steps: long-press a word, unfold the
selection panel with the dots button, tap the bookmark row, confirm in a dialog that
names the page. Each step was verified by hand first. Each step works.

Automating it meant driving all four with fixed timings on an emulator that had just
finished running the rest of the suite. The selection needs a long press that must
land on text; the panel needs a moment to fold out; the dialog needs its own beat.
Miss any beat and the next tap lands on the wrong thing - and the failure looks
exactly like the product forgot how to add bookmarks.

## The hijack

There was a second failure that had nothing to do with the app at all. An earlier
mis-tap had launched the search assistant, and it began asking for contacts access.
The prompt returned every time it was dismissed, and every tap the suite made while
it was up went into the prompt instead of the app. On an unrooted emulator there was
no clean way to remove the requester; the loop had to be closed by granting the
permission and leaving it granted.

A test environment that can be taken over by a permission dialog is not a place for
timing-sensitive checks.

## The decision

The check was removed, and the reason is written down next to where it would have
lived. The flow stays verified - by hand, step by step, with the evidence in the
ledger. A gate that is always red is worse than no gate at all: people stop reading
the report, and the one real failure eventually hides among the noise.

There is a general shape here. Automation is worth what its failures can be trusted
to mean. When a failure can mean "the product broke" or "the device was tired" with
equal likelihood, the check belongs in the manual column until the environment can
tell the two apart.

## What this does not solve

Retiring a check is not the same as covering the path. The bookmark flow now has
less automated pressure on it than the other eleven checks, and that is a real gap.
It will close when the suite runs on a device where a long press is a long press and
a permission prompt is an event the suite itself opens.

## The product decision

The suite exists to catch regressions, not to produce a green number. A smaller
honest gate beats a larger one that lies. Every check earns its place by failing
only when the product deserves it.
