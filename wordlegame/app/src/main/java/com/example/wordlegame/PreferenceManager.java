package com.example.wordlegame;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class to manage app preferences
 */
public class PreferenceManager {

    private static final String PREFS_NAME = "WordleGamePrefs";

    // Preference keys
    private static final String KEY_THEME = "theme";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_MUSIC_ENABLED = "music_enabled";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    /**
     * Constructor
     */
    public PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * Gets the selected theme
     * @return Theme ID (0: Light, 1: Dark, 2: Colorful, 3: High Contrast)
     */
    public int getTheme() {
        return preferences.getInt(KEY_THEME, 0);
    }

    /**
     * Sets the selected theme
     * @param themeId Theme ID to set
     */
    public void setTheme(int themeId) {
        editor.putInt(KEY_THEME, themeId);
        editor.apply();
    }

    /**
     * Checks if sound effects are enabled
     * @return true if sound effects are enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        return preferences.getBoolean(KEY_SOUND_ENABLED, true);
    }

    /**
     * Sets whether sound effects are enabled
     * @param enabled true to enable sound effects, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        editor.putBoolean(KEY_SOUND_ENABLED, enabled);
        editor.apply();
    }

    /**
     * Checks if background music is enabled
     * @return true if background music is enabled, false otherwise
     */
    public boolean isMusicEnabled() {
        return preferences.getBoolean(KEY_MUSIC_ENABLED, true);
    }

    /**
     * Sets whether background music is enabled
     * @param enabled true to enable background music, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        editor.putBoolean(KEY_MUSIC_ENABLED, enabled);
        editor.apply();
    }

    /**
     * Gets the app theme resource ID based on the selected theme
     * @return The resource ID of the theme to apply
     */
    public int getThemeResourceId() {
        int themeId = getTheme();

        switch (themeId) {
            case 1:
                return R.style.AppTheme_Dark;
            case 2:
                return R.style.AppTheme_Colorful;
            case 3:
                return R.style.AppTheme_HighContrast;
            default:
                return R.style.AppTheme_Light;
        }
    }
}