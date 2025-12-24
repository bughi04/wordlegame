package com.example.wordlegame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends BaseActivity {

    private Button playGameButton;
    private Button settingsButton;
    private Button historyButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DictionaryManager.getInstance(this);
        // Initialize UI components
        playGameButton = findViewById(R.id.button_play_game);
        settingsButton = findViewById(R.id.button_settings);
        historyButton = findViewById(R.id.button_history);
        exitButton = findViewById(R.id.button_exit);

        // Set click listeners
        playGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to game selection screen
                Intent intent = new Intent(MainActivity.this, GameSelectionActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to settings screen
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to history screen
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exit the app
                finish();
            }
        });
    }
}