package com.fritzvohn.airnudge.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.fritzvohn.airnudge.gesture.AirGesture;
import com.fritzvohn.airnudge.cursor.AirCursorController;
import com.fritzvohn.airnudge.settings.AirNudgeSettings;

import java.util.Locale;

/**
 * App-agnostic bridge between the hand-gesture recognizer and Android actions.
 * It deliberately does not inspect window content or integrate with individual applications.
 */
public final class AirNudgeAccessibilityService extends AccessibilityService
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "AirNudgeLatency";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private GestureActionExecutor executor;
    private AirNudgeSettings settings;
    private AirCursorController cursor;
    private long lastActionNanos;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        settings = new AirNudgeSettings(this);
        executor = new GestureActionExecutor(this, this::logLatency);
        cursor = new AirCursorController(this, settings);
        settings.preferences().registerOnSharedPreferenceChangeListener(this);
        Log.i(TAG, "Accessibility action bridge connected");
    }

    /**
     * Hand-tracking code calls this once it confirms a gesture. Calls may arrive off the main
     * thread; accessibility dispatch is moved onto the main looper without blocking the camera.
     */
    public void onGestureConfirmed(
            AirGesture gesture,
            long gestureStartedNanos,
            long gestureConfirmedNanos) {
        onGestureCandidate(gesture, 1f, gestureStartedNanos, gestureConfirmedNanos);
    }

    /** Applies confidence and cooldown controls before dispatching a detector candidate. */
    public void onGestureCandidate(
            AirGesture gesture,
            float confidence,
            long gestureStartedNanos,
            long gestureConfirmedNanos) {
        if (executor == null) {
            return;
        }
        mainHandler.post(() -> {
            if (confidence < settings.minimumGestureConfidence()) {
                return;
            }
            long cooldownNanos = settings.gestureCooldownMillis() * 1_000_000L;
            if (gestureConfirmedNanos - lastActionNanos < cooldownNanos) {
                return;
            }

            boolean accepted;
            if (gesture == AirGesture.PINCH && cursor.hasPosition()) {
                accepted = executor.executeTap(
                        gesture,
                        cursor.x(),
                        cursor.y(),
                        gestureStartedNanos,
                        gestureConfirmedNanos);
            } else {
                accepted = executor.execute(
                        gesture,
                        settings.actionFor(gesture),
                        gestureStartedNanos,
                        gestureConfirmedNanos);
            }
            if (accepted) {
                lastActionNanos = gestureConfirmedNanos;
            }
        });
    }

    /** Receives normalized index-fingertip coordinates from the camera pipeline. */
    public void onPointerFrame(float normalizedX, float normalizedY, long frameTimestampNanos) {
        if (cursor == null) {
            return;
        }
        mainHandler.post(() -> cursor.update(normalizedX, normalizedY));
    }

    private void logLatency(LatencySample sample) {
        Log.i(TAG, String.format(
                Locale.US,
                "gesture=%s recognition_us=%d dispatch_us=%d end_to_end_us=%d accepted=%s",
                sample.gesture,
                sample.recognitionMicros(),
                sample.dispatchMicros(),
                sample.endToEndMicros(),
                sample.acceptedByAndroid));
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // No window events are needed. AirNudge only dispatches generic system gestures.
    }

    @Override
    public void onInterrupt() {
        Log.w(TAG, "Accessibility service interrupted");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        mainHandler.post(cursor::refreshSettings);
    }

    @Override
    public void onDestroy() {
        if (settings != null) {
            settings.preferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        if (cursor != null) {
            cursor.destroy();
        }
        super.onDestroy();
    }
}
