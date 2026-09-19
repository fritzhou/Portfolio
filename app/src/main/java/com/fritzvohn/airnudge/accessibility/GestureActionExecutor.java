package com.fritzvohn.airnudge.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.SystemClock;
import android.util.DisplayMetrics;

import com.fritzvohn.airnudge.gesture.AirGesture;
import com.fritzvohn.airnudge.settings.UserAction;

/** Maps confirmed hand gestures to generic Android accessibility actions. */
public final class GestureActionExecutor {
    public interface LatencyListener {
        void onActionSent(LatencySample sample);
    }

    private static final long SWIPE_DURATION_MILLIS = 220L;
    private static final float SWIPE_START = 0.72f;
    private static final float SWIPE_END = 0.28f;

    private final AccessibilityService service;
    private final LatencyListener latencyListener;

    public GestureActionExecutor(AccessibilityService service, LatencyListener latencyListener) {
        this.service = service;
        this.latencyListener = latencyListener;
    }

    /**
     * Executes an already-confirmed gesture. Timestamps must come from
     * {@link SystemClock#elapsedRealtimeNanos()} so latency is immune to wall-clock changes.
     */
    public boolean execute(
            AirGesture gesture,
            UserAction action,
            long gestureStartedNanos,
            long gestureConfirmedNanos) {
        boolean accepted;
        switch (action) {
            case SWIPE_UP:
                accepted = dispatchSwipe(0.5f, SWIPE_START, 0.5f, SWIPE_END);
                break;
            case SWIPE_DOWN:
                accepted = dispatchSwipe(0.5f, SWIPE_END, 0.5f, SWIPE_START);
                break;
            case SWIPE_LEFT:
                accepted = dispatchSwipe(SWIPE_START, 0.5f, SWIPE_END, 0.5f);
                break;
            case SWIPE_RIGHT:
                accepted = dispatchSwipe(SWIPE_END, 0.5f, SWIPE_START, 0.5f);
                break;
            case BACK:
                accepted = service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case NONE:
            default:
                accepted = false;
                break;
        }

        report(gesture, gestureStartedNanos, gestureConfirmedNanos, accepted);
        return accepted;
    }

    /** Dispatches a short tap at the current Air Cursor position. */
    public boolean executeTap(
            AirGesture gesture,
            float x,
            float y,
            long gestureStartedNanos,
            long gestureConfirmedNanos) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription tap = new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(path, 0L, 50L))
                .build();
        boolean accepted = service.dispatchGesture(tap, null, null);
        report(gesture, gestureStartedNanos, gestureConfirmedNanos, accepted);
        return accepted;
    }

    private boolean dispatchSwipe(float startX, float startY, float endX, float endY) {
        DisplayMetrics metrics = service.getResources().getDisplayMetrics();
        Path path = new Path();
        path.moveTo(metrics.widthPixels * startX, metrics.heightPixels * startY);
        path.lineTo(metrics.widthPixels * endX, metrics.heightPixels * endY);

        GestureDescription gesture = new GestureDescription.Builder()
                .addStroke(new GestureDescription.StrokeDescription(path, 0L, SWIPE_DURATION_MILLIS))
                .build();
        return service.dispatchGesture(gesture, null, null);
    }

    private void report(
            AirGesture gesture,
            long gestureStartedNanos,
            long gestureConfirmedNanos,
            boolean accepted) {
        long actionSentNanos = SystemClock.elapsedRealtimeNanos();
        latencyListener.onActionSent(new LatencySample(
                gesture,
                gestureStartedNanos,
                gestureConfirmedNanos,
                actionSentNanos,
                accepted));
    }
}
