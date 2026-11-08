---
title: "Listening with the screen off is a policy problem before it is a voice problem"
summary: "Text-to-speech on Android 14 stopped being a matter of calling a synthesiser. It became a foreground-service declaration, a required demonstration video, and a decision about what happens when the user locks the phone."
date: "2026-11-08"
order: 6
readingMinutes: 8
---

Text-to-speech sounds like a small feature: hand a string to the system voice, wait for
it to finish, hand over the next string. The interesting problem is not the voice. It is
what happens ninety seconds later, when the reader puts the phone in their pocket.

## The moment the screen goes off

Android does not keep an application's process alive indefinitely once it is in the
background. A reading service that synthesises paragraph after paragraph is, from the
system's point of view, an app that is no longer being used — and it will be stopped,
usually somewhere in the middle of a chapter, without an error the reader can see.

The fix is a foreground service: a component that tells the system "this work is
user-visible and should continue", at the price of a permanent notification. For a reader
that is exactly the right trade. A notification with a play button and the book title is
useful, not intrusive, and it is the price of continuing to read aloud when the screen is
off.

## What changed in Android 14

Starting with Android 14, a foreground service has to declare *what kind* of work it is
doing, and the type has to be justified. "Media playback" is one of the types, and
reading a book aloud is media playback in the sense the platform means: audible content,
controlled by the user, expected to continue while the app is not on screen.

The declaration is not a checkbox in the manifest. It is a form in the store console that
asks which task the permission is used for, and — for this type — requires a short video
showing the feature in use. The reasoning is sound: the permission is abusable, and a
recording of the actual behaviour is harder to fake than a paragraph of prose.

The practical consequence is that the feature now has a maintenance cost beyond the code.
Every time the declaration changes, the video has to be recorded again. That cost is
paid once per release cycle, which is acceptable; it would not be acceptable if the
feature were marginal.

## Why we kept it

Because the alternative is not "text-to-speech without a notification". It is
text-to-speech that stops when you lock the phone. Listening to a book is, for most
people, an activity performed with the screen off — in bed, on a walk, while driving.
An implementation that only works with the screen on is a demo of a feature, not the
feature.

There is a battery cost, and it is real: synthesis plus audio output keeps the CPU awake
for as long as the reading continues. The mitigation is the same as the platform's:
the notification is dismissible, the stop button is in it, and the service ends when the
book ends or when the reader stops it.

## The parts nobody warns you about

Three details cost more time than the synthesis itself.

The first is the notification icon. A notification small icon is treated as an alpha
mask: the system ignores its colour and uses only its shape. A carefully drawn icon
becomes a grey blob unless it is designed as a silhouette first. Ours is four rectangles
standing on a bar, drawn in white on transparent, and it reads correctly at 24dp.

The second is audio focus. A reading app that keeps talking while a phone call arrives is
a bug; a reading app that pauses for a notification sound and never resumes is a worse
one. The service requests focus, pauses on loss, and resumes on regain — except during a
call, where it stays paused until the reader asks.

The third is the sentence boundary. Feeding a synthesiser a whole page produces a single
utterance with no way to skip a sentence, highlight the current phrase, or resume from
the right place after a pause. The text is split into sentences first, queued, and the
queue position is what the notification's next/previous buttons move. That queue is also
what makes "resume where I stopped listening" possible, which is a different position
from "where I stopped reading" — a distinction the app stores separately.

## The rule

A feature that only works while the user is looking at the screen is not finished. If it
needs a platform permission to be finished, the permission is part of the feature — and
so is the paperwork that keeps it.

The next note is about the library scan: what "find my books" actually requires from
Android.
