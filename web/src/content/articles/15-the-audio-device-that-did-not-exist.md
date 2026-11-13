---
title: "The audio device that did not exist"
summary: "For weeks every text-to-speech test failed with the same error, and every reading of the error blamed a missing Bluetooth route. The real problem was one boot flag: the emulator had been started with audio disabled, and a device that was never there cannot disconnect."
date: "2026-11-18"
order: 15
readingMinutes: 5
---

The error said *Bluetooth audio disconnected*, in the language of whatever locale the
device happened to be set to. It appeared every time the reader's text-to-speech tried to
play, and it was wrong in a specific, instructive way: nothing had disconnected, because
nothing had ever connected. The emulator the tests ran on had been booted with the audio
subsystem switched off, and the error message was the closest thing the app could say
about an audio device that did not exist.

## The long way around

The panel was verified - voice selection, speed, pitch, volume, the play button. The
engine was verified - it picked the right voice for the system language. The one thing
that kept failing was the actual playing, and the failure pointed at Bluetooth, so a lot
of time went into the Bluetooth path: the receiver that stops reading when a headset
disconnects, the media session state, the audio focus. None of it was the problem.

The problem was one flag on the emulator command line. Booting it without the audio
switch brought a speaker device into existence - visible in the audio dump as a single
line - and the same tap that had always failed reached a playing state: speed one, no
error, and the small play/pause bar the app draws on the page appeared for the first
time.

## The reversal

The expectation was that a persistent error meant a persistent fault. The reality is
that an error message is a report about the world as the program saw it, and a program
running on a machine with no audio device sees a world where audio is impossible. The
message was truthful about that world and useless about the real one. Every debugging
session spent inside the message's world was spent in the wrong world.

## What this does not solve

The playing state was observed, but sound was not heard - there is no ear in this test
environment, and a state machine reaching *playing* is not the same as a sentence
arriving at a person. The one remaining claim - that it sounds right - belongs to a
device test. Everything the machine could confirm, it now has.

## The product decision

When a test environment reports the same failure for weeks, audit the environment before
auditing the code. The boot flag cost more than any of the code paths it appeared to
implicate, and the fix was not in the codebase at all.
