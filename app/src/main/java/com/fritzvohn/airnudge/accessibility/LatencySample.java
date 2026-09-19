package com.fritzvohn.airnudge.accessibility;

import com.fritzvohn.airnudge.gesture.AirGesture;

/** Monotonic timestamps for one gesture-to-Android action pipeline. */
public final class LatencySample {
    public final AirGesture gesture;
    public final long gestureStartedNanos;
    public final long gestureConfirmedNanos;
    public final long actionSentNanos;
    public final boolean acceptedByAndroid;

    public LatencySample(
            AirGesture gesture,
            long gestureStartedNanos,
            long gestureConfirmedNanos,
            long actionSentNanos,
            boolean acceptedByAndroid) {
        this.gesture = gesture;
        this.gestureStartedNanos = gestureStartedNanos;
        this.gestureConfirmedNanos = gestureConfirmedNanos;
        this.actionSentNanos = actionSentNanos;
        this.acceptedByAndroid = acceptedByAndroid;
    }

    public long recognitionMicros() {
        return Math.max(0L, gestureConfirmedNanos - gestureStartedNanos) / 1_000L;
    }

    public long dispatchMicros() {
        return Math.max(0L, actionSentNanos - gestureConfirmedNanos) / 1_000L;
    }

    public long endToEndMicros() {
        return Math.max(0L, actionSentNanos - gestureStartedNanos) / 1_000L;
    }
}
