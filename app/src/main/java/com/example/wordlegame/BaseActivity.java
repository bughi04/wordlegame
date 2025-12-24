package com.example.wordlegame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(this);
        // Apply the theme before setting the content view
        setTheme(preferenceManager.getThemeResourceId());
        super.onCreate(savedInstanceState);
    }
}