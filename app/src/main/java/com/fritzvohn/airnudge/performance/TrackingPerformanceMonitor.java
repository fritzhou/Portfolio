package com.fritzvohn.airnudge.performance;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Debug;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.fritzvohn.airnudge.accessibility.LatencySample;

import java.util.concurrent.atomic.AtomicLong;

/** Low-overhead counters and rolling timings for evidence-based profile comparisons. */
public final class TrackingPerformanceMonitor {
    private static final String TAG = "AirNudgePerformance";
    private final Context context;
    private final long startedNanos = SystemClock.elapsedRealtimeNanos();
    private final AtomicLong cameraFrames = new AtomicLong();
    private final AtomicLong analyzedFrames = new AtomicLong();
    private final AtomicLong droppedFrames = new AtomicLong();
    private final AtomicLong profileSkippedFrames = new AtomicLong();
    private final AtomicLong falseTriggers = new AtomicLong();
    private final AtomicLong handLosses = new AtomicLong();
    private final AtomicLong handRecoveries = new AtomicLong();
    private final RollingWindowStats inferenceNanos = new RollingWindowStats(120);
    private final RollingWindowStats gestureNanos = new RollingWindowStats(60);

    public TrackingPerformanceMonitor(Context context) {
        this.context = context.getApplicationContext();
    }

    public void cameraFrameReceived() { cameraFrames.incrementAndGet(); }
    public void analysisStarted() { analyzedFrames.incrementAndGet(); }
    public void frameDropped() { droppedFrames.incrementAndGet(); }
    public void frameSkippedByProfile() { profileSkippedFrames.incrementAndGet(); }
    public synchronized void inferenceFinished(long durationNanos) { inferenceNanos.add(durationNanos); }
    public synchronized void gestureDispatched(LatencySample sample) {
        gestureNanos.add(sample.actionSentNanos - sample.gestureStartedNanos);
    }
    public void falseTriggerReported() { falseTriggers.incrementAndGet(); }
    public void handLost() { handLosses.incrementAndGet(); }
    public void handRecovered() { handRecoveries.incrementAndGet(); }

    public synchronized PerformanceSnapshot snapshot() {
        long elapsedNanos = Math.max(1L, SystemClock.elapsedRealtimeNanos() - startedNanos);
        double seconds = elapsedNanos / 1_000_000_000d;
        Intent battery = context.registerReceiver(
                null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        float batteryTemperature = Float.NaN;
        int batteryPercent = -1;
        if (battery != null) {
            batteryTemperature = battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f;
            int level = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = battery.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (level >= 0 && scale > 0) batteryPercent = Math.round(level * 100f / scale);
        }
        BatteryManager batteryManager = context.getSystemService(BatteryManager.class);
        int batteryCurrent = batteryManager == null ? Integer.MIN_VALUE
                : batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
        int thermalStatus = -1;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            PowerManager power = context.getSystemService(PowerManager.class);
            if (power != null) thermalStatus = power.getCurrentThermalStatus();
        }
        long memoryBytes = Debug.getPss() * 1024L;
        return new PerformanceSnapshot(
                elapsedNanos / 1_000_000L,
                cameraFrames.get() / seconds,
                analyzedFrames.get() / seconds,
                inferenceNanos.average() / 1_000_000d,
                gestureNanos.average() / 1_000_000d,
                memoryBytes,
                droppedFrames.get(),
                profileSkippedFrames.get(),
                analyzedFrames.get(),
                falseTriggers.get(),
                handLosses.get(),
                handRecoveries.get(),
                batteryTemperature,
                batteryPercent,
                batteryCurrent,
                thermalStatus);
    }

    public void logSnapshot(PerformanceProfile profile) {
        Log.i(TAG, snapshot().toLogLine(profile));
    }
}
