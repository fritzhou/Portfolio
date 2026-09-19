package com.fritzvohn.airnudge;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.view.accessibility.AccessibilityManager;

import com.fritzvohn.airnudge.accessibility.AirNudgeAccessibilityService;

import java.util.List;

/** Reads service state from Android instead of assuming setup succeeded. */
public final class ServiceStatus {
    private ServiceStatus() { }

    public static boolean isAccessibilityEnabled(Context context) {
        AccessibilityManager manager = context.getSystemService(AccessibilityManager.class);
        if (manager == null || !manager.isEnabled()) return false;
        List<AccessibilityServiceInfo> services = manager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        String expected = context.getPackageName() + "/"
                + AirNudgeAccessibilityService.class.getName();
        for (AccessibilityServiceInfo service : services) {
            if (service.getId().equalsIgnoreCase(expected)) return true;
        }
        return false;
    }
}
