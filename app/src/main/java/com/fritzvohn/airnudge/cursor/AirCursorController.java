package com.fritzvohn.airnudge.cursor;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.util.Log;

import com.fritzvohn.airnudge.settings.AirNudgeSettings;

/** Lightweight accessibility overlay that maps normalized fingertip coordinates to a pointer. */
public final class AirCursorController {
    private static final String TAG = "AirNudgeCursor";
    private final AccessibilityService service;
    private final AirNudgeSettings settings;
    private final WindowManager windowManager;
    private CursorView cursorView;
    private WindowManager.LayoutParams layoutParams;
    private final PointerSmoother smoother = new PointerSmoother();
    private float screenX;
    private float screenY;

    public AirCursorController(AccessibilityService service, AirNudgeSettings settings) {
        this.service = service;
        this.settings = settings;
        windowManager = service.getSystemService(WindowManager.class);
    }

    /** Updates the pointer from camera coordinates normalized to the inclusive 0..1 range. */
    public void update(float normalizedX, float normalizedY) {
        if (!settings.cursorEnabled()) {
            hide();
            return;
        }

        DisplayMetrics metrics = service.getResources().getDisplayMetrics();
        smoother.update(
                normalizedX,
                normalizedY,
                settings.cursorSensitivity(),
                settings.cursorSmoothing());
        screenX = smoother.x() * metrics.widthPixels;
        screenY = smoother.y() * metrics.heightPixels;
        showOrMove();
    }

    public boolean hasPosition() {
        return smoother.initialized() && settings.cursorEnabled();
    }

    public float x() { return screenX; }
    public float y() { return screenY; }

    public void refreshSettings() {
        if (!settings.cursorEnabled()) {
            hide();
        } else if (smoother.initialized()) {
            hide();
            showOrMove();
        }
    }

    public void destroy() {
        hide();
        smoother.reset();
    }

    /** Immediately removes stale position when tracking loses the hand. */
    public void onHandLost() {
        hide();
        smoother.reset();
    }

    private void showOrMove() {
        int size = Math.round(settings.pointerSizeDp()
                * service.getResources().getDisplayMetrics().density);
        if (cursorView == null) {
            cursorView = new CursorView(service);
            layoutParams = new WindowManager.LayoutParams(
                    size,
                    size,
                    WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            layoutParams.gravity = Gravity.TOP | Gravity.START;
            position(size);
            try {
                windowManager.addView(cursorView, layoutParams);
            } catch (RuntimeException error) {
                Log.e(TAG, "Unable to attach accessibility pointer", error);
                cursorView = null;
                layoutParams = null;
            }
        } else {
            layoutParams.width = size;
            layoutParams.height = size;
            position(size);
            try {
                windowManager.updateViewLayout(cursorView, layoutParams);
            } catch (RuntimeException error) {
                Log.e(TAG, "Unable to move accessibility pointer", error);
                hide();
            }
        }
    }

    private void position(int size) {
        layoutParams.x = Math.round(screenX - size / 2f);
        layoutParams.y = Math.round(screenY - size / 2f);
    }

    private void hide() {
        if (cursorView != null) {
            try {
                windowManager.removeView(cursorView);
            } catch (RuntimeException error) {
                Log.w(TAG, "Pointer was already detached", error);
            }
            cursorView = null;
            layoutParams = null;
        }
    }

    private static final class CursorView extends View {
        private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);

        CursorView(AccessibilityService service) {
            super(service);
            fill.setColor(Color.argb(225, 24, 122, 88));
            stroke.setColor(Color.WHITE);
            stroke.setStyle(Paint.Style.STROKE);
            stroke.setStrokeWidth(3f * getResources().getDisplayMetrics().density);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float radius = Math.min(getWidth(), getHeight()) * 0.36f;
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, fill);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, stroke);
        }
    }
}
