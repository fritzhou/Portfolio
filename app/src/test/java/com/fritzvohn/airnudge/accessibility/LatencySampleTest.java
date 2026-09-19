package com.fritzvohn.airnudge.accessibility;

import static org.junit.Assert.assertEquals;

import com.fritzvohn.airnudge.gesture.AirGesture;

import org.junit.Test;

public final class LatencySampleTest {
    @Test
    public void reportsEachPipelineStageInMicroseconds() {
        LatencySample sample = new LatencySample(
                AirGesture.SWIPE_UP,
                1_000_000L,
                3_500_000L,
                4_000_000L,
                true);

        assertEquals(2_500L, sample.recognitionMicros());
        assertEquals(500L, sample.dispatchMicros());
        assertEquals(3_000L, sample.endToEndMicros());
    }

    @Test
    public void guardsAgainstOutOfOrderTimestamps() {
        LatencySample sample = new LatencySample(
                AirGesture.CLOSED_FIST,
                5_000L,
                4_000L,
                3_000L,
                false);

        assertEquals(0L, sample.recognitionMicros());
        assertEquals(0L, sample.dispatchMicros());
        assertEquals(0L, sample.endToEndMicros());
    }
}
