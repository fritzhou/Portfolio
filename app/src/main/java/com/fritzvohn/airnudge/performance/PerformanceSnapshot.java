package com.fritzvohn.airnudge.performance;

import java.util.Locale;

/** Immutable measurements from one runtime window. Times are milliseconds. */
public final class PerformanceSnapshot {
    public final long elapsedMillis;
    public final double cameraFps;
    public final double analysisFps;
    public final double inferenceMillis;
    public final double gestureLatencyMillis;
    public final long memoryBytes;
    public final long droppedFrames;
    public final long profileSkippedFrames;
    public final long analyzedFrames;
    public final long falseTriggers;
    public final long handLosses;
    public final long handRecoveries;
    public final float batteryTemperatureCelsius;
    public final int batteryPercent;
    public final int batteryCurrentMicroamps;
    public final int thermalStatus;

    public PerformanceSnapshot(long elapsedMillis, double cameraFps, double analysisFps,
            double inferenceMillis, double gestureLatencyMillis, long memoryBytes,
            long droppedFrames, long profileSkippedFrames, long analyzedFrames,
            long falseTriggers, long handLosses,
            long handRecoveries, float batteryTemperatureCelsius, int batteryPercent,
            int batteryCurrentMicroamps, int thermalStatus) {
        this.elapsedMillis = elapsedMillis;
        this.cameraFps = cameraFps;
        this.analysisFps = analysisFps;
        this.inferenceMillis = inferenceMillis;
        this.gestureLatencyMillis = gestureLatencyMillis;
        this.memoryBytes = memoryBytes;
        this.droppedFrames = droppedFrames;
        this.profileSkippedFrames = profileSkippedFrames;
        this.analyzedFrames = analyzedFrames;
        this.falseTriggers = falseTriggers;
        this.handLosses = handLosses;
        this.handRecoveries = handRecoveries;
        this.batteryTemperatureCelsius = batteryTemperatureCelsius;
        this.batteryPercent = batteryPercent;
        this.batteryCurrentMicroamps = batteryCurrentMicroamps;
        this.thermalStatus = thermalStatus;
    }

    public double droppedFramePercent() {
        long total = analyzedFrames + droppedFrames;
        return total == 0 ? 0d : droppedFrames * 100d / total;
    }

    public String toLogLine(PerformanceProfile profile) {
        return String.format(Locale.US,
                "profile=%s elapsed_ms=%d camera_fps=%.1f analysis_fps=%.1f "
                        + "inference_ms=%.1f gesture_ms=%.1f memory_mb=%.1f "
                        + "dropped=%d dropped_pct=%.1f profile_skipped=%d "
                        + "false_triggers=%d hand_losses=%d "
                        + "hand_recoveries=%d battery_pct=%d battery_current_ua=%d "
                        + "battery_temp_c=%.1f thermal=%d",
                profile.name(), elapsedMillis, cameraFps, analysisFps, inferenceMillis,
                gestureLatencyMillis, memoryBytes / 1048576d, droppedFrames,
                droppedFramePercent(), profileSkippedFrames, falseTriggers, handLosses, handRecoveries,
                batteryPercent, batteryCurrentMicroamps, batteryTemperatureCelsius, thermalStatus);
    }
}
