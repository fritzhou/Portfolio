package com.fritzvohn.airnudge.performance;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class RollingWindowStatsTest {
    @Test
    public void evictsOldestSamplesWithoutGrowingMemory() {
        RollingWindowStats stats = new RollingWindowStats(3);
        stats.add(10);
        stats.add(20);
        stats.add(30);
        stats.add(40);
        assertEquals(3, stats.count());
        assertEquals(30d, stats.average(), 0.001d);
        assertEquals(40L, stats.maximum());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyWindow() {
        new RollingWindowStats(0);
    }
}
