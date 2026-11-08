---
title: "The left-edge tap zone that a one-handed reader cannot reach"
summary: "Page-turn zones are designed on a tablet and used on a phone. At 360dp wide, a 20% edge strip is eleven millimetres of glass under the base of your thumb — which is why the default changed."
date: "2026-11-08"
order: 4
readingMinutes: 7
---

Tap zones look like the simplest feature in a reader: the left strip goes back, the
right strip goes forward, the middle opens the menu. Then you use it on a phone, in one
hand, on a bus, and discover that the geometry was designed for a device nobody holds
that way.

## The arithmetic

A typical phone reports a width of about 360 to 412 density-independent pixels. Take
360. A 20% edge zone is 72dp. At the standard density that is roughly 11 millimetres of
glass — about the width of a thumbnail, measured from the bezel inward.

Now hold the phone in your right hand. The base of your thumb rests near the bottom-right
corner and the thumb sweeps an arc whose centre is the bottom-right. The top-left corner
is the furthest point from that arc: it is the one place on the screen you cannot reach
without shifting your grip.

Put the "previous page" zone there and two things happen. Readers stop using it and swipe
instead, or they shift their grip on every backward page turn, which is exactly the
motion the zone was supposed to save.

## Why the zones exist at all

Because swiping has its own failure mode. A horizontal swipe on a page of text competes
with text selection, with horizontal scrolling in a zoomed PDF, and with the system's
own back gesture at the screen edge. A tap is unambiguous: it either hit a zone or it
did not.

So the zones are worth keeping. The question is where they go and how wide they are.

## What we changed

The default edge width is now narrower than it was, and the zones are mirrored
vertically rather than spanning the full height.

The reasoning: on a phone, the reachable band is the lower two-thirds. A zone that spans
the full height puts most of its area where the thumb is not, and — worse — puts part of
it under the palm when the phone is held loosely. A zone that only occupies the lower
band is smaller, easier to hit deliberately, and much harder to hit by accident.

The vertical band also resolves an old complaint about the top strip: on a phone with a
notch or a punch-hole camera, the top edge is a system gesture area on some devices and
the tap never reaches the app at all.

## Accidental turns are worse than missed turns

A missed turn costs one swipe. An accidental turn costs the reader their place in a
paragraph, and — if they do not notice immediately — a minute of confused re-reading.

So the zones are biased toward missing rather than firing: a tap has to land inside the
band, not near its edge, and a tap that arrives within a few hundred milliseconds of a
scroll is discarded rather than interpreted. That last rule exists because a scroll
gesture that ends with a finger lift is, geometrically, a tap at the point where the
finger stopped moving.

## Why it is configurable

Because the correct answer depends on the hand, the device and the case. A 6-inch phone
in a slim case and a 10-inch tablet in a folio are not the same instrument, and the same
percentage produces an 11mm strip on one and a 40mm strip on the other.

The default is a guess that is right for most phones. The setting exists for the readers
for whom the guess is wrong, and the widths are exposed in percentage terms rather than
in millimetres because the app cannot know how wide the screen physically is.

## The rule this produced

Any interaction that a reader performs more than a hundred times per book has to be
evaluated on the geometry of the hand, not on the geometry of the layout. Page turns,
brightness, and the menu gesture all qualify. The rest — the settings that are touched
once — can be designed on a desktop monitor and nobody will notice.

## What about the other hand

A left-handed reader holds the phone in the left hand and reaches the left edge easily.
Every default that assumes a right hand is wrong for roughly one reader in ten, and it is
wrong in a way that is invisible if you only test with your own hand.

The zones are therefore symmetric: the same band on both edges, mirrored vertically. The
only asymmetry is which one turns forward, and that follows the reading direction rather
than the hand — a left-to-right book turns forward on the right edge whether the reader
is right- or left-handed.

For right-to-left scripts the mapping flips, which is the correct behaviour and a
surprisingly common source of bugs in readers that hard-code "right means next".

## The double-tap trap

One more failure mode worth recording: the double tap.

In a two-page or zoomed view, a double tap means "zoom in". In a single-page view it
means nothing, so it falls through to whatever the single-tap handler does — which is a
page turn. The result is that a reader who taps twice quickly, as people do when an
interface feels slow, turns two pages and loses their place.

The fix is to hold the single-tap action for the duration of the double-tap window before
committing it. It adds about 250 milliseconds of latency to every page turn, which sounds
unacceptable and is not: the page turn is animated anyway, and the perceived delay is
smaller than the annoyance of the alternative.


The next note is about text itself: why automatic hyphenation is off until you ask for
it.
