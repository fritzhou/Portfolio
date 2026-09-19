# AirNudge — Android Control + Air Cursor

Phase 3 connects confirmed hand gestures to app-agnostic Android accessibility actions. It does not use TikTok, Facebook, Instagram, YouTube, browser, gallery, or any other application-specific API.

Phase 4 adds persistent gesture mappings, confidence and cooldown controls, plus a responsive Air Cursor. The cursor is deliberately small in scope: normalized index-fingertip coordinates are scaled around the screen center, lightly smoothed, and displayed as a non-interactive accessibility overlay. A confirmed pinch dispatches a tap at the current pointer position.

## Gesture mapping

| Confirmed gesture | Android action |
| --- | --- |
| Swipe Up | Upward screen swipe |
| Swipe Down | Downward screen swipe |
| Swipe Left | Left screen swipe |
| Swipe Right | Right screen swipe |
| Closed Fist | Global Back action |
| Pinch | Tap at the active Air Cursor position |

`AirNudgeAccessibilityService.onGestureCandidate(...)` is the confidence-aware hand-tracking integration point. The recognizer must provide monotonic `gestureStartedNanos` and `gestureConfirmedNanos` values from `SystemClock.elapsedRealtimeNanos()`. The original `onGestureConfirmed(...)` method remains as a compatibility shortcut with full confidence.

## Air Cursor pipeline

Send each tracked index-fingertip frame to:

```java
service.onPointerFrame(normalizedX, normalizedY, frameTimestampNanos);
```

Both coordinates use camera-normalized `0..1` values. AirNudge applies cursor sensitivity, clamps to the display, performs a single exponential smoothing step, converts to screen pixels, and moves the virtual pointer. The user can turn the cursor on or off and adjust sensitivity, smoothing, and pointer size. Keeping smoothing low favors responsiveness.

When the detector confirms `PINCH`, the service taps the current pointer position. Pinch does nothing while the cursor is disabled or before a valid fingertip frame arrives.

## Customization

The setup screen persists:

- separate action mappings for all four swipes and Closed Fist
- minimum gesture confidence
- gesture cooldown
- Air Cursor on/off
- cursor sensitivity, smoothing, and pointer size

Mappings remain app-agnostic and take effect immediately through shared preferences.

## Latency measurement

Every attempted action writes one structured `AirNudgeLatency` log entry containing:

- recognition time: gesture start → gesture confirmed
- dispatch time: gesture confirmed → accessibility action sent
- end-to-end time: gesture start → accessibility action sent
- whether Android accepted the action for dispatch

Inspect the measurements while exercising supported apps:

```bash
adb logcat -s AirNudgeLatency:I
```

The `actionSentNanos` timestamp is captured immediately after `dispatchGesture` or `performGlobalAction` returns. It measures submission to Android, not completion of the resulting animation.

## Build and setup

Requirements: JDK 17 and Android SDK 35.

```bash
gradle :app:assembleDebug
gradle :app:testDebugUnitTest
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Open AirNudge, select **Open Accessibility settings**, and enable **AirNudge gesture control**. The service requests gesture dispatch capability but explicitly disables window-content retrieval.

## Manual compatibility test

For TikTok, Facebook, Instagram, YouTube, a browser, and the system gallery:

1. Open scrollable content and perform each directional gesture.
2. Confirm vertical and horizontal movement matches the gesture direction.
3. Navigate to a secondary screen and confirm Closed Fist performs Back.
4. Record `AirNudgeLatency` output and compare end-to-end and dispatch measurements.
5. Enable Air Cursor, move the pointer, and confirm Pinch taps its current position.

Behavior can vary when an app has no scrollable content or handles system gestures specially, but AirNudge itself has no per-app code paths.
