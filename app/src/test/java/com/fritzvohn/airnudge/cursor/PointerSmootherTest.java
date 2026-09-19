package com.fritzvohn.airnudge.cursor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class PointerSmootherTest {
    @Test
    public void zeroSmoothingTracksInputImmediately() {
        PointerSmoother smoother = new PointerSmoother();
        smoother.update(0.2f, 0.8f, 1f, 0f);
        smoother.update(0.7f, 0.3f, 1f, 0f);
        assertEquals(0.7f, smoother.x(), 0.0001f);
        assertEquals(0.3f, smoother.y(), 0.0001f);
    }

    @Test
    public void smoothingRetainsPartOfPreviousPosition() {
        PointerSmoother smoother = new PointerSmoother();
        smoother.update(0f, 0f, 1f, 0.5f);
        smoother.update(1f, 1f, 1f, 0.5f);
        assertEquals(0.5f, smoother.x(), 0.0001f);
        assertEquals(0.5f, smoother.y(), 0.0001f);
    }

    @Test
    public void sensitivityExpandsAroundCenterAndClamps() {
        PointerSmoother smoother = new PointerSmoother();
        smoother.update(0.9f, 0.1f, 2f, 0f);
        assertEquals(1f, smoother.x(), 0.0001f);
        assertEquals(0f, smoother.y(), 0.0001f);
    }
}
