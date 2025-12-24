package com.example.wordlegame;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends BaseActivity {

    private RadioGroup themeRadioGroup;
    private Switch soundSwitch;
    private Switch musicSwitch;
    private Button saveButton;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        // Initialize views
        themeRadioGroup = findViewById(R.id.radio_group_theme);
        soundSwitch = findViewById(R.id.switch_sound);
        musicSwitch = findViewById(R.id.switch_music);
        saveButton = findViewById(R.id.button_save);

        // Initialize preference manager
        preferenceManager = new PreferenceManager(this);

        // Load current settings
        loadSettings();

        // Set up save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void loadSettings() {
        // Load theme setting
        int themeId = preferenceManager.getTheme();
        switch (themeId) {
            case 1:
                ((RadioButton) findViewById(R.id.radio_theme_dark)).setChecked(true);
                break;
            case 2:
                ((RadioButton) findViewById(R.id.radio_theme_colorful)).setChecked(true);
                break;
            case 3:
                ((RadioButton) findViewById(R.id.radio_theme_high_contrast)).setChecked(true);
                break;
            default:
                ((RadioButton) findViewById(R.id.radio_theme_light)).setChecked(true);
                break;
        }

        // Load sound and music settings
        soundSwitch.setChecked(preferenceManager.isSoundEnabled());
        musicSwitch.setChecked(preferenceManager.isMusicEnabled());
    }

    private void saveSettings() {
        // Save theme setting
        int themeId;
        int checkedRadioButtonId = themeRadioGroup.getCheckedRadioButtonId();

        if (checkedRadioButtonId == R.id.radio_theme_dark) {
            themeId = 1;
        } else if (checkedRadioButtonId == R.id.radio_theme_colorful) {
            themeId = 2;
        } else if (checkedRadioButtonId == R.id.radio_theme_high_contrast) {
            themeId = 3;
        } else {
            themeId = 0; // Light theme
        }

        preferenceManager.setTheme(themeId);

        // Save sound and music settings
        preferenceManager.setSoundEnabled(soundSwitch.isChecked());
        preferenceManager.setMusicEnabled(musicSwitch.isChecked());

        // Show confirmation
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

        // Finish activity
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle back button click
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}