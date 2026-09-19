package com.fritzvohn.airnudge;

import android.os.Bundle;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.fritzvohn.airnudge.settings.AirNudgeSettings;

/** Optional, live cursor tuning; values persist and the service observes them immediately. */
public final class CalibrationActivity extends AppCompatActivity {
    private AirNudgeSettings settings;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        settings = new AirNudgeSettings(this);
        bind();
        findViewById(R.id.calibration_reset).setOnClickListener(view -> {
            settings.preferences().edit()
                    .putInt(AirNudgeSettings.CURSOR_SENSITIVITY, 100)
                    .putInt(AirNudgeSettings.CURSOR_SMOOTHING, 25)
                    .putInt(AirNudgeSettings.POINTER_SIZE, 28)
                    .apply();
            bind();
        });
        findViewById(R.id.calibration_done).setOnClickListener(view -> finish());
    }

    private void bind() {
        bindSeek(R.id.calibration_sensitivity, AirNudgeSettings.CURSOR_SENSITIVITY,
                Math.round(settings.cursorSensitivity() * 100f) - 50, 50);
        bindSeek(R.id.calibration_smoothing, AirNudgeSettings.CURSOR_SMOOTHING,
                Math.round(settings.cursorSmoothing() * 100f), 0);
        bindSeek(R.id.calibration_size, AirNudgeSettings.POINTER_SIZE,
                settings.pointerSizeDp() - 16, 16);
    }

    private void bindSeek(int id, String key, int progress, int offset) {
        SeekBar bar = findViewById(id);
        bar.setOnSeekBarChangeListener(null);
        bar.setProgress(progress);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                if (fromUser) settings.preferences().edit().putInt(key, value + offset).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
}
