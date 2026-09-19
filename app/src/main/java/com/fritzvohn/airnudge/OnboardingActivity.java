package com.fritzvohn.airnudge;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.fritzvohn.airnudge.settings.AirNudgeSettings;

/** Short first-run flow covering gestures, privacy, and required Android setup. */
public final class OnboardingActivity extends AppCompatActivity {
    private ViewFlipper pages;
    private TextView progress;
    private Button primary;
    private Button back;
    private AirNudgeSettings settings;
    private boolean cameraRequestAttempted;
    private final ActivityResultLauncher<String> cameraPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                cameraRequestAttempted = true;
                updatePage();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        settings = new AirNudgeSettings(this);
        cameraRequestAttempted = savedInstanceState != null
                && savedInstanceState.getBoolean("camera_request_attempted");
        pages = findViewById(R.id.onboarding_pages);
        progress = findViewById(R.id.onboarding_progress);
        primary = findViewById(R.id.onboarding_primary);
        back = findViewById(R.id.onboarding_back);
        primary.setOnClickListener(view -> advance());
        back.setOnClickListener(view -> {
            if (pages.getDisplayedChild() > 0) pages.showPrevious();
            updatePage();
        });
        updatePage();
    }

    @Override protected void onResume() {
        super.onResume();
        updatePage();
    }

    private void advance() {
        int page = pages.getDisplayedChild();
        if (page == 2 && checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (cameraRequestAttempted
                    && !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Intent appSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(appSettings);
                return;
            }
            cameraPermission.launch(Manifest.permission.CAMERA);
            return;
        }
        if (page == 3 && !ServiceStatus.isAccessibilityEnabled(this)) {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }
        if (page == pages.getChildCount() - 1) {
            settings.setOnboardingComplete(true);
            finish();
            return;
        }
        pages.showNext();
        updatePage();
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("camera_request_attempted", cameraRequestAttempted);
        super.onSaveInstanceState(outState);
    }

    private void updatePage() {
        int page = pages.getDisplayedChild();
        progress.setText(getString(R.string.onboarding_progress, page + 1, pages.getChildCount()));
        back.setEnabled(page > 0);
        if (page == 2) {
            boolean granted = checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
            primary.setText(granted ? R.string.continue_label : R.string.allow_camera);
        } else if (page == 3) {
            primary.setText(ServiceStatus.isAccessibilityEnabled(this)
                    ? R.string.finish_setup : R.string.enable_accessibility);
        } else {
            primary.setText(R.string.continue_label);
        }
    }
}
