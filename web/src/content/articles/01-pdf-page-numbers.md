---
title: "A PDF on a phone does not lose resolution — it loses the page number"
summary: "Reflow makes a two-column paper readable on a six-inch screen and quietly destroys the only thing a citation depends on: the page. What we do about it, and what we refuse to do."
date: "2026-11-08"
order: 1
readingMinutes: 9
---

Every reader app advertises reflow. The pitch is simple and true: a PDF is laid out
for paper, a phone is not paper, so the text is re-laid into a single column that fits
the screen. On a two-column journal article, the difference is dramatic. Without it you
are pinching and panning across a page designed for A4; with it you read the way you
read anything else.

The cost is almost never mentioned, and it is not visual.

## What a page number actually is

In a PDF, a page is a physical object. Page 214 is a rectangle of fixed dimensions, and
everything a citation needs — "see p. 214, second column" — depends on that rectangle
existing and being stable. Two people with different phones, different font sizes and
different margins will still agree on what is on page 214, because page 214 is not
computed. It is stored.

Reflow computes pages. It takes the text stream, measures it against the current screen
and font, and produces a new sequence of pages that has nothing to do with the original.
On a 6-inch phone at a comfortable size, a 300-page paper can become 900 pages. The
reading experience improves enormously. The citation becomes meaningless: "p. 214" in
the reflowed view is somewhere around page 640, and it will be a different number on the
next device, and a different number again if the reader changes the font size.

This is not a bug in any particular app. It is what reflow *is*. The question is what a
reader should do about it.

## The decision

We keep the original page model as the source of truth, even when the text is being
displayed reflowed.

Concretely: the reader knows which original page each reflowed paragraph came from. The
progress indicator, the bookmark, and the "go to page" field all speak in original page
numbers, not in the computed ones. The page counter at the bottom of a reflowed document
therefore shows something slightly odd — it does not count the pages you are turning
through — but it shows the number you would write in a footnote.

The alternative is what most readers do: show the computed page number because it is
what the reader sees on screen. It is more intuitive for ten seconds and useless for
every purpose a page number has.

## Where this gets uncomfortable

Half-measures are worse than either extreme here.

If the citation says page 214 and the reader jumps to "page 214" of the reflowed
document, they land in the wrong place. So the jump has to resolve against the original
pagination and then translate back into the reflowed position — which means a jump can
land mid-paragraph rather than at the top of a page, because the original page break
happened in the middle of a sentence that reflow has since moved.

We accept that. A jump that lands one line off is recoverable; a jump that lands forty
pages away is not.

The second uncomfortable case is figures. A figure in a two-column paper is anchored to
a column, and reflow moves the text around it. Some readers re-anchor figures to the
paragraph that references them, which is usually right and occasionally catastrophic: a
full-page plate that originally appeared after the references gets pulled into the middle
of section 3, and the reader concludes the document is corrupt.

Our rule is conservative: figures stay near their original page position, and if that
means a figure appears later than the text that discusses it, so be it. Following a
"see Fig. 4" reference backwards is a normal reading skill. Reconstructing a document
that has been silently rearranged is not.

## What reflow does to hyphenation

Single-column reflow narrows the measure, and a narrow measure with justified text
produces rivers of white space unless hyphenation is on. Most systems turn it on
automatically.

We do not, by default. Automatic hyphenation is only as good as the language dictionary
behind it, and a paper in English with a German abstract will hyphenate the German
incorrectly for as long as it takes the reader to notice. Ragged-right text with a few
loose lines is a minor aesthetic loss; a document where every fourth line breaks a word
in the wrong place is a reading problem.

The setting exists, it is just off until asked for.

## The thing we will not do

We will not renumber the document and call it a page.

There is a temptation, once reflow is good, to present the reflowed pagination as the
document's pagination — to hide the original entirely, the way a word processor hides
the difference between a draft and a proof. It makes the interface simpler and it
quietly breaks the only contract a PDF has with the outside world: that page 214 is a
place you can point at.

A reader that cannot be cited is a viewer. We are trying to build the other thing.

## A note on the two engines

OmniShelf carries two PDF engines, and the choice is not cosmetic. One is the reference
implementation used by most open-source readers; the other is faster on complex vector
graphics but differs in how it handles font substitution and in what it exposes about
text positioning.

Reflow needs the second kind of information — character positions, not just glyphs — so
the reflow path and the plain rendering path do not always agree on where a line ends.
When they disagree, the original page model wins, because that is the one the citation
depends on. It is a small invariant, enforced in one place, and it is the reason the page
number in the corner can be trusted.

The next note is about the shelf itself: why a novel and a scanned paper share one grid,
and what that costs.
