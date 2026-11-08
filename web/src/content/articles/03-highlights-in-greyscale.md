---
title: "A highlight that disappears on an e-ink screen"
summary: "Colour highlighters are chosen on a colour screen and read on a greyscale one. The palette has to survive a luminance conversion, or the annotation becomes a slightly darker paragraph."
date: "2026-11-08"
order: 3
readingMinutes: 8
---

Highlight colours are picked in an editor, on a bright display, next to white text on a
white page. They are read on a phone in daylight, on a phone at night with the brightness
at four percent, and — for a meaningful number of readers — on an e-ink screen that has
no colour at all.

The last case is the one that decides the palette, because greyscale is unforgiving in a
specific way: it does not fail loudly. A yellow highlight at full saturation converts to
a light grey that is very close to the page background. It looks fine in the editor. On
the device it is invisible, and the reader concludes the highlight was not saved.

## What greyscale actually preserves

An e-ink panel maps colour to luminance, and the mapping is roughly perceptual: green
contributes most, blue least. That means two colours with the same RGB *value* can end up
with very different greys — a saturated blue and a saturated yellow are not the same
distance from the paper once the colour is gone.

The practical consequence for a highlighter: the choice cannot be made on hue. It has to
be made on the luminance difference between the highlight and the page, and that
difference has to be large enough to see at a glance on a reflective screen in bad light.

Three constraints follow, and they conflict:

- The highlight must be visibly darker (or lighter, in dark mode) than the page.
- The text on top of it must stay legible, which caps how dark it can be.
- The highlight must not be so strong that a page of twenty of them becomes unreadable.

## The palette we settled on

Four colours, all desaturated, all chosen for their grey value rather than their hue: a
muted amber, a soft green, a dusty blue, and a neutral grey-violet. In colour they are
distinguishable. In greyscale they map to four clearly separated levels.

The test is mechanical: convert the page to greyscale, look at it from a metre away, and
ask whether the marked sentences are findable without hunting. If two of the four
collapse into the same grey, one of them is replaced — even if it is the prettiest of
the set.

There is a second rule that is less obvious: highlights are drawn *under* the text, never
over it. Drawing a translucent rectangle over glyphs looks better in a mockup and ruins
antialiasing on a low-DPI panel, because the text is composited twice. Under the text,
the glyph edges stay crisp and the highlight behaves like a marker on paper.

## Dark mode is a different problem

Inverting the page does not invert the highlight sensibly. A pale yellow that reads as a
highlight on white becomes a glowing block on near-black, and the text on it — which is
now light — loses contrast.

So the palette is defined twice: once for light surfaces, once for dark, with the dark
variants shifted toward the middle of the luminance range rather than simply inverted.
The rule is the same in both: the highlight must be distinguishable from the page and
must not compete with the text for attention.

## Why not let the reader pick any colour

Because a free colour picker guarantees that someone will choose a highlight that is
invisible on their device, and will not understand why. A fixed palette that has been
checked against greyscale, dark mode and daylight is a smaller feature and a better one.

The palette is not a matter of taste. It is the set of colours that still work when the
display stops cooperating — which, on e-ink, is always.

## Why the colour is stored as a name, not as a value

An annotation in the file format carries a colour, and the simplest implementation stores
whatever the reader picked — a red, green and blue triple — and draws it back verbatim.

That breaks the moment the palette changes, which it will, because a palette is a design
decision and design decisions get revisited. Every highlight made under the old palette
becomes an orphan: a colour that no longer exists in the interface, that cannot be
selected again, and that will not have a dark-mode variant.

So the stored value is a slot, not a colour: "the second highlight colour". The slot maps
to a light value and a dark value at draw time. Re-tuning the palette updates every
existing highlight in every book, including the ones made years ago, because nothing in
the file recorded the old RGB.

The cost is that exporting annotations to another application loses the exact colour —
the slot name is meaningful only here. That is a fair trade for a palette that can be
fixed after the fact.

## The annotation bar

Highlights are only useful if they can be found again. A page of marks with no index is
decoration.

The annotation list is sorted by position, not by date, because the question a reader
asks is "what did I mark in this chapter", not "what did I mark last Tuesday". It shows
the highlighted text and the surrounding line, so a mark that has drifted — because the
book was re-flowed, or because the highlight was made in a different rendering engine —
can be recognised even when its position is approximate.


The next note is about gestures: why a tap zone that tests well on a tablet fails on a
phone held in one hand.
