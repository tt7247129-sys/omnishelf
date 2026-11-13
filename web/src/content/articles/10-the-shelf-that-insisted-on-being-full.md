---
title: "The shelf that insisted on being full"
summary: "A fresh install, an empty Downloads folder, and the library still showed one book. The empty state everyone designs for is unreachable when the app quietly plants a sample on the shelf — and the fix was not a better empty screen, it was removing the book nobody asked for."
date: "2026-11-14"
order: 10
readingMinutes: 6
---

A reader application should have an answer for the day it meets a person who owns no
books. Everyone designs that answer: an illustration, a sentence, a button. Ours existed
in the code for weeks before we could ever see it. The shelf would not stay empty long
enough to render it.

## The one-book shelf

The test was simple. Take a fresh install on a clean emulator, give it a Downloads
folder with nothing in it, open the library, and confirm the empty state appears. It
never did. The count badge said 1. The grid showed a card. The title was *Alice's
Adventures in Wonderland*.

We deleted the file. Still 1. We cleared the app data. Still 1. We deleted the profile
directory the app keeps on shared storage, because this app persists its settings outside
its own sandbox and survives an uninstall that way. Still 1, and now the file had
reappeared on disk, byte for byte, with a fresh timestamp.

The book was being reborn on every launch.

## What was actually happening

The scan worker had a fallback for a scan that returns nothing: it unpacked a bundled
sample from the app's own assets, copied it into the app's temporary folder, and indexed
it. Upstream logic, inherited wholesale. For the fork it had one visible effect and one
invisible one.

The visible effect is that every new user received a book they never downloaded. Harmless,
one might say, except that a library is a place a person curates, and a book appearing
from nowhere reads as a bug even when it is intentional.

The invisible effect mattered more. Our empty state was wired to "the list is empty", and
the list could never be empty. Every test of the empty screen was a test of the seed
instead. We had been verifying a screen that did not exist in production.

## The reversal

We expected the empty state to be one line of code away from being finished. It was,
instead, two weeks and a root-cause chase away, and the work that actually mattered was
a deletion: removing the seeding block entirely. The shelf now stays empty when it is
empty, the empty state renders, and it carries a single action — *Add a folder* — which
is the one thing a person with no books actually needs next.

There is a habit here worth naming. When a designed state is unreachable, the first
question should not be "why is the design wrong" but "what is holding the state open".
We fixed the empty screen twice before we found the seeder. Neither fix was observable.

## What this does not solve

An empty shelf is not the only state a reader can be stranded in. The same class of bug —
a fallback that fills a state the designer wanted empty — can live in the bookmarks tab,
in the search results, in the recent list. We found and fixed the search variant in the
same pass: a search with no matches used to show the "no books" message, telling a person
with a full library that their books were gone. The principle is the same. The states are
only different until you look.

## The product decision

A reader application does not need to plant books to be friendly. A shelf that says what
it needs — one folder, one tap — is friendlier than one that invents content. Every
"helpful" fallback that adds data a person did not ask for deserves the same audit: what
state does it hold open, and who asked for the data.
