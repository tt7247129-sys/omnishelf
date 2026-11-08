---
title: "The margin note has a budget, and a highlight does not"
description: "A printed margin holds four words, and that limit is what makes marginalia useful. An electronic highlight has no limit at all, which is why a marked-up book often ends up less useful than an unmarked one."
category: "Reading Craft"
pubDate: "2026-11-08"
readingMinutes: 9
related: ["inspection-reading-without-a-table-of-contents"]
---

There is a reason marginalia from serious readers looks the way it does: a few words, an
arrow, a question mark, a page reference. Not because those readers were terse, but
because the margin is about eighteen millimetres wide and the pencil is the width of a
word. The medium imposed a budget, and the budget forced a judgement — if you could not
say it in four words, you had not decided what you thought yet.

Electronic annotation removed the budget and did not replace it.

## What a highlight actually records

Highlighting a sentence feels like capturing an idea. It captures a location. The sentence
is already in the book; the mark says "this one mattered", and that is all it says. Whether
it mattered because it was the thesis, because it contradicted the previous chapter, or
because it was beautifully written — none of that is in the highlight.

On paper, the margin absorbs that information. A vertical line next to a paragraph means
"the whole passage"; a word means "this specific claim"; a question mark means "I do not
believe this"; a cross-reference means "compare with chapter 4". The notation is a tiny
private language, and it exists because the space forced you to invent it.

A highlighter with four colours is the electronic approximation, and it fails at exactly
the point where it matters: the fourth colour has no agreed meaning, so after a hundred
pages you cannot remember whether orange meant "important" or "disagree".

## The experiment we ran on ourselves

We took one long argumentative book — a 380-page trade paperback, read once on paper and
once as an EPUB — and compared the notes a month later.

On paper: forty-one marks, of which thirty-one were a line in the margin and a word. Read
back in five minutes, the argument reconstructed.

On screen: one hundred and twelve highlights across four colours, plus nine typed notes.
Read back in twenty-five minutes, and the reconstruction was worse — the highlights did
not distinguish between the author's claims and the passages being quoted for refutation,
because the colour system had no way to express that distinction.

The number of marks went up by a factor of two and a half and the amount of understanding
went down. That is the failure mode of removing a budget: the cost of marking fell to
zero, so marking stopped being a decision.

## What we changed, and what we refused to change

The first change was to make the four colours mean something fixed and to say what they
mean in the interface, so a reader does not invent a private code that they will forget.
Ours are: claim, disagreement, quotation, and "return to this".

The second change was to make the note field the primary object and the highlight the
anchor. A highlight without a note is a bookmark; a highlight with four words beside it is
a marginal note, and the four words are the ones that survive a month.

The third thing we tried and removed was a prompt that asked for a note every time you
highlighted. It was correct in spirit and it interrupted reading, which is the one thing
an annotation tool must not do. The compromise is that the note field is open and focused
when the highlight is created, so typing is one gesture away rather than two, and ignoring
it costs nothing.

## What happens to the marks afterwards

An annotation system is only as good as the second reading, and the second reading is
where electronic marginalia is weakest.

A paper book with marginalia is self-contained: you open it and the notes are there, in
your handwriting, in the position they occupied when you made them. The record and the
context are the same object.

An electronic annotation is a record in a database, keyed to a text range. Three things can
happen to it that cannot happen to a pencil mark:

The book can be replaced. A new edition, a re-download, a conversion between formats — and
the ranges no longer resolve. The annotation survives as text with nowhere to point, which
is worse than losing it, because it appears in the list and does nothing when tapped.

The reading system can change. A highlight positioned by a character offset survives a
font change; one positioned by rectangles does not. This is invisible until someone rotates
a phone.

The export can flatten it. Every annotation format that leaves the app is either plain
text or a markup file with the taxonomy stripped out, because "claim" and "disagreement"
are meanings this reader assigned and no interchange format knows about them.

The design response is to store the annotation as a text range plus a cached geometry, and
to treat the geometry as a hint that can be discarded. That costs a re-measurement every
time the layout changes, and it is the only way the marks survive the second reading — which
is the only reading they exist for.

## The list is the index

One consequence of the taxonomy above is that the annotation list stops being a list of
quotes and becomes an index of your own argument with the book.

Sorted by position, it reads like a table of contents you wrote. Sorted by colour, it
groups by function: all the places you disagreed, all the places you wanted to return to.
Neither view is better; they answer different questions, and a reader who only has one of
them has a flat list of text with no shape.

The rule we settled on is that the list shows the note when there is one and the marked
text when there is not — never both, because both is a wall of text on a phone. The note is
the thing you wrote, and it is shorter.

## The counter-argument, which is partly right

There is a real case for frictionless highlighting: some readers mark first and synthesise
later, and for them a low-cost mark is a useful first pass. The research on note-taking
generally supports the idea that the act of deciding what to record matters more than the
record itself, which argues for friction, but it also supports the idea that a second pass
over cheap marks can substitute for a first pass of expensive ones.

We do not have evidence strong enough to settle that. What we have is a clear observation
from our own reading: when marking is free, we mark everything, and a book where
everything is marked is a book with no marks.

## What this does not solve

It does not make annotations portable. Exporting highlights to another application loses
the colour semantics, because "claim" is a meaning this reader assigns, not a property of
the file format. The text and the note survive export; the taxonomy does not.

It also does not work well for books you read once and never return to, where the entire
apparatus is overhead. Marginalia is for texts you intend to argue with.

The feature this produced in OmniShelf is deliberately small: four named highlight colours,
a note field that opens with the highlight, and an annotation list sorted by position
rather than by date. That last choice is the one people notice — the question is "what did
I mark in this chapter", not "what did I mark last Tuesday".
