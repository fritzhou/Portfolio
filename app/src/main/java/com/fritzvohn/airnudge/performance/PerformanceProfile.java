package com.fritzvohn.airnudge.performance;

/** Explicit experiment inputs; profile changes should be compared with measured sessions. */
public enum PerformanceProfile {
    BATTERY_SAVER("Battery saver", 480, 360, 3, 0.72f, 6, 0.18f, 500L),
    BALANCED("Balanced", 640, 480, 2, 0.65f, 8, 0.16f, 350L),
    RESPONSIVE("Responsive", 1280, 720, 1, 0.60f, 10, 0.14f, 250L);

    public final String label;
    public final int analysisWidth;
    public final int analysisHeight;
    public final int analyzeEveryNthFrame;
    public final float trackingConfidence;
    public final int motionHistorySize;
    public final float gestureMotionThreshold;
    public final long cooldownMillis;

    PerformanceProfile(
            String label,
            int analysisWidth,
            int analysisHeight,
            int analyzeEveryNthFrame,
            float trackingConfidence,
            int motionHistorySize,
            float gestureMotionThreshold,
            long cooldownMillis) {
        this.label = label;
        this.analysisWidth = analysisWidth;
        this.analysisHeight = analysisHeight;
        this.analyzeEveryNthFrame = analyzeEveryNthFrame;
        this.trackingConfidence = trackingConfidence;
        this.motionHistorySize = motionHistorySize;
        this.gestureMotionThreshold = gestureMotionThreshold;
        this.cooldownMillis = cooldownMillis;
    }

    @Override public String toString() { return label; }
}
