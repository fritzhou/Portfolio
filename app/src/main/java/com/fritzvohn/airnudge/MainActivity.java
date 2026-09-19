package com.fritzvohn.airnudge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fritzvohn.airnudge.gesture.AirGesture;
import com.fritzvohn.airnudge.settings.AirNudgeSettings;
import com.fritzvohn.airnudge.settings.UserAction;
import com.fritzvohn.airnudge.performance.PerformanceProfile;

/** Setup and intentionally small runtime-control screen. */
public final class MainActivity extends AppCompatActivity {
    private AirNudgeSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = new AirNudgeSettings(this);

        if (!settings.onboardingComplete()) {
            startActivity(new Intent(this, OnboardingActivity.class));
        }

        findViewById(R.id.open_accessibility_settings).setOnClickListener(view ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        findViewById(R.id.review_tutorial).setOnClickListener(view ->
                startActivity(new Intent(this, OnboardingActivity.class)));
        findViewById(R.id.open_calibration).setOnClickListener(view ->
                startActivity(new Intent(this, CalibrationActivity.class)));

        Switch controlEnabled = findViewById(R.id.control_enabled);
        controlEnabled.setChecked(settings.controlEnabled());
        controlEnabled.setOnCheckedChangeListener((button, checked) -> {
            edit().putBoolean(AirNudgeSettings.CONTROL_ENABLED, checked).apply();
            updateSetupStatus();
        });

        bindMapping(R.id.map_swipe_up, AirGesture.SWIPE_UP);
        bindMapping(R.id.map_swipe_down, AirGesture.SWIPE_DOWN);
        bindMapping(R.id.map_swipe_left, AirGesture.SWIPE_LEFT);
        bindMapping(R.id.map_swipe_right, AirGesture.SWIPE_RIGHT);
        bindMapping(R.id.map_closed_fist, AirGesture.CLOSED_FIST);
        bindPerformanceProfile();

        Switch cursorEnabled = findViewById(R.id.cursor_enabled);
        cursorEnabled.setChecked(settings.cursorEnabled());
        cursorEnabled.setOnCheckedChangeListener((button, checked) -> edit()
                .putBoolean(AirNudgeSettings.CURSOR_ENABLED, checked).apply());

        bindSeekBar(R.id.cursor_sensitivity, AirNudgeSettings.CURSOR_SENSITIVITY,
                Math.round(settings.cursorSensitivity() * 100f) - 50, 50);
        bindSeekBar(R.id.cursor_smoothing, AirNudgeSettings.CURSOR_SMOOTHING,
                Math.round(settings.cursorSmoothing() * 100f), 0);
        bindSeekBar(R.id.pointer_size, AirNudgeSettings.POINTER_SIZE,
                settings.pointerSizeDp() - 16, 16);
        bindSeekBar(R.id.gesture_sensitivity, AirNudgeSettings.GESTURE_SENSITIVITY,
                Math.round(settings.minimumGestureConfidence() * 100f) - 50, 50);
        bindSeekBar(R.id.gesture_cooldown, AirNudgeSettings.GESTURE_COOLDOWN,
                (int) settings.gestureCooldownMillis() - 100, 100);
    }

    @Override protected void onResume() {
        super.onResume();
        if (settings != null) updateSetupStatus();
    }

    private void updateSetupStatus() {
        boolean camera = checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        boolean accessibility = ServiceStatus.isAccessibilityEnabled(this);
        long heartbeat = settings.serviceHeartbeatMillis();
        boolean recentlyConnected = heartbeat > 0
                && android.os.SystemClock.elapsedRealtime() - heartbeat < 90_000L;
        TextView status = findViewById(R.id.setup_status);
        if (!camera) status.setText(R.string.status_camera_denied);
        else if (!accessibility) status.setText(R.string.status_accessibility_disabled);
        else if (!recentlyConnected) status.setText(R.string.status_service_recovering);
        else if (!settings.controlEnabled()) status.setText(R.string.status_control_paused);
        else status.setText(R.string.status_ready);
    }

    private void bindMapping(int viewId, AirGesture gesture) {
        Spinner spinner = findViewById(viewId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.action_labels,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(settings.actionFor(gesture).ordinal(), false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                settings.setAction(gesture, UserAction.values()[position]);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void bindPerformanceProfile() {
        Spinner spinner = findViewById(R.id.performance_profile);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.performance_profile_labels,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(settings.performanceProfile().ordinal(), false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PerformanceProfile selected = PerformanceProfile.values()[position];
                if (selected != settings.performanceProfile()) {
                    settings.applyProfile(selected);
                    recreate();
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void bindSeekBar(int viewId, String key, int progress, int offset) {
        SeekBar seekBar = findViewById(viewId);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int value, boolean fromUser) {
                if (fromUser) edit().putInt(key, value + offset).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar bar) { }
            @Override public void onStopTrackingTouch(SeekBar bar) { }
        });
    }

    private SharedPreferences.Editor edit() {
        return settings.preferences().edit();
    }
}
