package com.fritzvohn.airnudge.performance;

/** Fixed-size, allocation-free rolling mean and maximum for hot-path timing samples. */
public final class RollingWindowStats {
    private final long[] samples;
    private int count;
    private int next;
    private long sum;

    public RollingWindowStats(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        samples = new long[capacity];
    }

    public void add(long value) {
        value = Math.max(0L, value);
        if (count == samples.length) {
            sum -= samples[next];
        } else {
            count++;
        }
        samples[next] = value;
        sum += value;
        next = (next + 1) % samples.length;
    }

    public int count() { return count; }
    public double average() { return count == 0 ? 0d : (double) sum / count; }

    public long maximum() {
        long maximum = 0L;
        for (int i = 0; i < count; i++) maximum = Math.max(maximum, samples[i]);
        return maximum;
    }
}
