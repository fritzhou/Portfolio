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
import com.fritzvohn.airnudge.performance.FrameAnalysisGate;
import com.fritzvohn.airnudge.performance.PerformanceProfile;
import com.fritzvohn.airnudge.performance.PerformanceSnapshot;
import com.fritzvohn.airnudge.performance.TrackingPerformanceMonitor;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private final FrameAnalysisGate frameGate = new FrameAnalysisGate();
    private TrackingPerformanceMonitor performanceMonitor;
    private final AtomicBoolean handWasLost = new AtomicBoolean(true);
    private final Runnable periodicPerformanceLog = new Runnable() {
        @Override public void run() {
            if (performanceMonitor != null && settings != null) {
                performanceMonitor.logSnapshot(settings.performanceProfile());
                mainHandler.postDelayed(this, 30_000L);
            }
        }
    };

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mainHandler.removeCallbacks(periodicPerformanceLog);
        if (settings != null) {
            settings.preferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        if (cursor != null) cursor.destroy();
        frameGate.resetInFlightWork();
        settings = new AirNudgeSettings(this);
        performanceMonitor = new TrackingPerformanceMonitor(this);
        executor = new GestureActionExecutor(this, sample -> {
            logLatency(sample);
            performanceMonitor.gestureDispatched(sample);
        });
        cursor = new AirCursorController(this, settings);
        settings.preferences().registerOnSharedPreferenceChangeListener(this);
        Log.i(TAG, "Accessibility action bridge connected");
        mainHandler.postDelayed(periodicPerformanceLog, 30_000L);
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
        if (handWasLost.compareAndSet(true, false)) {
            performanceMonitor.handRecovered();
        }
        mainHandler.post(() -> cursor.update(normalizedX, normalizedY));
    }

    /** Records every delivered camera frame, including frames where no hand is present. */
    public void onCameraFrameReceived() {
        if (performanceMonitor != null) performanceMonitor.cameraFrameReceived();
    }

    /**
     * Camera analysis calls this before inference. False means the frame should be closed without
     * analysis; queuing it would increase latency and memory pressure.
     */
    public boolean tryBeginAnalysis() {
        if (settings == null || performanceMonitor == null) return false;
        PerformanceProfile profile = settings.performanceProfile();
        FrameAnalysisGate.Result result = frameGate.tryAcquireResult(profile.analyzeEveryNthFrame);
        if (result == FrameAnalysisGate.Result.ACQUIRED) {
            performanceMonitor.analysisStarted();
            return true;
        }
        if (result == FrameAnalysisGate.Result.PROFILE_SKIPPED) {
            performanceMonitor.frameSkippedByProfile();
        } else {
            performanceMonitor.frameDropped();
        }
        return false;
    }

    /** Must be called in a finally block after every successful tryBeginAnalysis(). */
    public void endAnalysis(long inferenceStartedNanos) {
        if (performanceMonitor == null) {
            frameGate.release();
            return;
        }
        performanceMonitor.inferenceFinished(
                Math.max(0L, android.os.SystemClock.elapsedRealtimeNanos() - inferenceStartedNanos));
        frameGate.release();
    }

    public PerformanceProfile performanceProfile() {
        return settings.performanceProfile();
    }

    public PerformanceSnapshot performanceSnapshot() {
        return performanceMonitor.snapshot();
    }

    /** Allows validation tooling or an explicit user correction flow to count a false trigger. */
    public void reportFalseTrigger() {
        performanceMonitor.falseTriggerReported();
    }

    /** Clears stale pointer state immediately; the next fingertip frame recovers naturally. */
    public void onHandLost() {
        if (cursor == null || !handWasLost.compareAndSet(false, true)) return;
        performanceMonitor.handLost();
        mainHandler.post(cursor::onHandLost);
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
        if (AirNudgeSettings.PERFORMANCE_PROFILE.equals(key)) {
            // Start a clean measurement window so profiles are compared independently.
            performanceMonitor = new TrackingPerformanceMonitor(this);
        }
        mainHandler.post(cursor::refreshSettings);
    }

    @Override
    public void onDestroy() {
        mainHandler.removeCallbacks(periodicPerformanceLog);
        if (settings != null) {
            settings.preferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        if (cursor != null) {
            cursor.destroy();
        }
        super.onDestroy();
    }
}
