package com.fritzvohn.airnudge.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.fritzvohn.airnudge.gesture.AirGesture;
import com.fritzvohn.airnudge.performance.PerformanceProfile;

/** Single source of truth for user-tunable control settings and gesture mappings. */
public final class AirNudgeSettings {
    public static final String PREFERENCES = "airnudge_control_settings";
    public static final String CURSOR_ENABLED = "cursor_enabled";
    public static final String CURSOR_SENSITIVITY = "cursor_sensitivity";
    public static final String CURSOR_SMOOTHING = "cursor_smoothing";
    public static final String POINTER_SIZE = "pointer_size";
    public static final String GESTURE_SENSITIVITY = "gesture_sensitivity";
    public static final String GESTURE_COOLDOWN = "gesture_cooldown";
    public static final String PERFORMANCE_PROFILE = "performance_profile";
    public static final String CONTROL_ENABLED = "control_enabled";
    public static final String ONBOARDING_COMPLETE = "onboarding_complete";
    public static final String SERVICE_HEARTBEAT = "service_heartbeat";

    private final SharedPreferences preferences;

    public AirNudgeSettings(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public SharedPreferences preferences() {
        return preferences;
    }

    public boolean cursorEnabled() {
        return preferences.getBoolean(CURSOR_ENABLED, false);
    }

    public boolean controlEnabled() {
        return preferences.getBoolean(CONTROL_ENABLED, true);
    }

    public boolean onboardingComplete() {
        return preferences.getBoolean(ONBOARDING_COMPLETE, false);
    }

    public void setOnboardingComplete(boolean complete) {
        preferences.edit().putBoolean(ONBOARDING_COMPLETE, complete).apply();
    }

    public void writeServiceHeartbeat(long elapsedRealtimeMillis) {
        preferences.edit().putLong(SERVICE_HEARTBEAT, elapsedRealtimeMillis).apply();
    }

    public long serviceHeartbeatMillis() {
        return preferences.getLong(SERVICE_HEARTBEAT, -1L);
    }

    public float cursorSensitivity() {
        return preferences.getInt(CURSOR_SENSITIVITY, 100) / 100f;
    }

    /** 0 is raw input; 0.90 strongly smooths motion. */
    public float cursorSmoothing() {
        return preferences.getInt(CURSOR_SMOOTHING, 25) / 100f;
    }

    public int pointerSizeDp() {
        return preferences.getInt(POINTER_SIZE, 28);
    }

    public float minimumGestureConfidence() {
        return preferences.getInt(GESTURE_SENSITIVITY, 65) / 100f;
    }

    public long gestureCooldownMillis() {
        return preferences.getInt(GESTURE_COOLDOWN, 350);
    }

    public PerformanceProfile performanceProfile() {
        String stored = preferences.getString(
                PERFORMANCE_PROFILE, PerformanceProfile.BALANCED.name());
        try {
            return PerformanceProfile.valueOf(stored);
        } catch (IllegalArgumentException ignored) {
            return PerformanceProfile.BALANCED;
        }
    }

    public void applyProfile(PerformanceProfile profile) {
        preferences.edit()
                .putString(PERFORMANCE_PROFILE, profile.name())
                .putInt(GESTURE_COOLDOWN, (int) profile.cooldownMillis)
                .apply();
    }

    public UserAction actionFor(AirGesture gesture) {
        String stored = preferences.getString(mappingKey(gesture), defaultAction(gesture).name());
        try {
            return UserAction.valueOf(stored);
        } catch (IllegalArgumentException ignored) {
            return defaultAction(gesture);
        }
    }

    public void setAction(AirGesture gesture, UserAction action) {
        preferences.edit().putString(mappingKey(gesture), action.name()).apply();
    }

    public static String mappingKey(AirGesture gesture) {
        return "mapping_" + gesture.name();
    }

    private static UserAction defaultAction(AirGesture gesture) {
        switch (gesture) {
            case SWIPE_UP: return UserAction.SWIPE_UP;
            case SWIPE_DOWN: return UserAction.SWIPE_DOWN;
            case SWIPE_LEFT: return UserAction.SWIPE_LEFT;
            case SWIPE_RIGHT: return UserAction.SWIPE_RIGHT;
            case CLOSED_FIST: return UserAction.BACK;
            case PINCH:
            default: return UserAction.NONE;
        }
    }
}
