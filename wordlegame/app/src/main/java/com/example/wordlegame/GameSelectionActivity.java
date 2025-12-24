package com.example.wordlegame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

public class GameSelectionActivity extends AppCompatActivity {

    private RadioGroup gameTypeGroup;
    private RadioGroup difficultyGroup;
    private Button startGameButton;
    private Button backButton;

    // Views for difficulty selection (only shown for normal mode)
    private View difficultyContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);

        // Initialize UI components
        gameTypeGroup = findViewById(R.id.radio_group_game_type);
        difficultyGroup = findViewById(R.id.radio_group_difficulty);
        difficultyContainer = findViewById(R.id.difficulty_container);
        startGameButton = findViewById(R.id.button_start_game);
        backButton = findViewById(R.id.button_back);

        // Set up listener for game type selection
        gameTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Show difficulty options only for normal mode
                if (checkedId == R.id.radio_normal) {
                    difficultyContainer.setVisibility(View.VISIBLE);
                } else {
                    difficultyContainer.setVisibility(View.GONE);
                }
            }
        });

        // Set default selection
        ((RadioButton) findViewById(R.id.radio_normal)).setChecked(true);
        ((RadioButton) findViewById(R.id.radio_easy)).setChecked(true);

        // Set click listener for start game button
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        // Set click listener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void startGame() {
        GameMode selectedGameMode;

        // Determine selected game mode
        int selectedGameTypeId = gameTypeGroup.getCheckedRadioButtonId();

        if (selectedGameTypeId == R.id.radio_normal) {
            // For normal mode, also check difficulty
            int selectedDifficultyId = difficultyGroup.getCheckedRadioButtonId();
            if (selectedDifficultyId == R.id.radio_easy) {
                selectedGameMode = GameMode.NORMAL_EASY;
            } else {
                selectedGameMode = GameMode.NORMAL_HARD;
            }
        } else if (selectedGameTypeId == R.id.radio_timed) {
            selectedGameMode = GameMode.TIMED;
        } else if (selectedGameTypeId == R.id.radio_evil) {
            selectedGameMode = GameMode.EVIL;
        } else {
            selectedGameMode = GameMode.EMOJI;
        }

        // Start game activity with selected mode
        Intent intent = new Intent(GameSelectionActivity.this, GameActivity.class);
        intent.putExtra("GAME_MODE", selectedGameMode);
        startActivity(intent);
    }
}