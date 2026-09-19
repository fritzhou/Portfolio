package com.fritzvohn.airnudge.cursor;

/** Small allocation-free normalized-coordinate transform and exponential smoother. */
public final class PointerSmoother {
    private boolean initialized;
    private float x;
    private float y;

    public void update(float inputX, float inputY, float sensitivity, float retention) {
        float targetX = clamp(0.5f + (clamp(inputX) - 0.5f) * sensitivity);
        float targetY = clamp(0.5f + (clamp(inputY) - 0.5f) * sensitivity);
        retention = clamp(retention);
        if (!initialized) {
            x = targetX;
            y = targetY;
            initialized = true;
            return;
        }
        x = x * retention + targetX * (1f - retention);
        y = y * retention + targetY * (1f - retention);
    }

    public boolean initialized() { return initialized; }
    public float x() { return x; }
    public float y() { return y; }

    public void reset() {
        initialized = false;
    }

    private static float clamp(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
