package com.fritzvohn.airnudge.performance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class FrameAnalysisGateTest {
    @Test
    public void skipsConfiguredFramesAndNeverQueuesConcurrentAnalysis() {
        FrameAnalysisGate gate = new FrameAnalysisGate();
        assertFalse(gate.tryAcquire(2));
        assertTrue(gate.tryAcquire(2));
        assertFalse(gate.tryAcquire(1));
        gate.release();
        assertTrue(gate.tryAcquire(1));
        assertEquals(1L, gate.droppedFrames());
        assertEquals(1L, gate.skippedFrames());
        assertEquals(4L, gate.receivedFrames());
    }
}
