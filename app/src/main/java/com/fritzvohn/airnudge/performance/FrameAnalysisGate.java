package com.fritzvohn.airnudge.performance;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** Keeps only the newest useful work instead of building a latency-producing frame queue. */
public final class FrameAnalysisGate {
    public enum Result { ACQUIRED, PROFILE_SKIPPED, BUSY_DROPPED }
    private final AtomicBoolean analysisRunning = new AtomicBoolean();
    private final AtomicLong received = new AtomicLong();
    private final AtomicLong dropped = new AtomicLong();
    private final AtomicLong skipped = new AtomicLong();

    public boolean tryAcquire(int everyNthFrame) {
        return tryAcquireResult(everyNthFrame) == Result.ACQUIRED;
    }

    public Result tryAcquireResult(int everyNthFrame) {
        long frame = received.incrementAndGet();
        if (everyNthFrame > 1 && frame % everyNthFrame != 0) {
            skipped.incrementAndGet();
            return Result.PROFILE_SKIPPED;
        }
        if (!analysisRunning.compareAndSet(false, true)) {
            dropped.incrementAndGet();
            return Result.BUSY_DROPPED;
        }
        return Result.ACQUIRED;
    }

    public void release() { analysisRunning.set(false); }
    public void resetInFlightWork() { analysisRunning.set(false); }
    public long receivedFrames() { return received.get(); }
    public long droppedFrames() { return dropped.get(); }
    public long skippedFrames() { return skipped.get(); }
}
