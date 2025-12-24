package com.example.wordlegame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class GameActivity extends BaseActivity {

    // Define the GameCompleteListener interface as a public interface
    public interface GameCompleteListener {
        void onGameComplete(GameResult result);
    }

    private TextView gameModeTitleText;
    private TextView gameModeDescriptionText;
    private Button exitGameButton;

    private GameMode currentGameMode;
    private WordleRepository repository;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize UI components
        gameModeTitleText = findViewById(R.id.text_game_mode_title);
        gameModeDescriptionText = findViewById(R.id.text_game_mode_description);
        exitGameButton = findViewById(R.id.button_exit_game);

        // Initialize repository and preference manager
        repository = new WordleRepository(this);
        preferenceManager = new PreferenceManager(this);

        // Get selected game mode from intent
        if (getIntent().hasExtra("GAME_MODE")) {
            currentGameMode = (GameMode) getIntent().getSerializableExtra("GAME_MODE");
        } else {
            // Default to normal easy mode if not specified
            currentGameMode = GameMode.NORMAL_EASY;
        }

        // Update UI with game mode info
        gameModeTitleText.setText(currentGameMode.getDisplayName());
        gameModeDescriptionText.setText(currentGameMode.getDescription());

        // Set click listener for exit button
        exitGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to game selection screen
                finish();
            }
        });

        // Load appropriate fragment based on game mode
        loadGameFragment();
    }

    private void loadGameFragment() {
        Fragment gameFragment;

        switch (currentGameMode) {
            case TIMED:
                gameFragment = new TimedGameFragment();
                break;
            case EVIL:
                gameFragment = new EvilGameFragment();
                break;
            case EMOJI:
                gameFragment = new EmojiGameFragment();
                break;
            case NORMAL_EASY:
            case NORMAL_HARD:
            default:
                gameFragment = new NormalGameFragment();
                break;
        }

        // Pass game mode to fragment
        Bundle args = new Bundle();
        args.putSerializable("GAME_MODE", currentGameMode);
        gameFragment.setArguments(args);

        // Replace container with the game fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, gameFragment);
        transaction.commit();
    }

    /**
     * Method called when game is completed
     */
    public void onGameComplete(GameResult result) {
        // Save game result to database
        repository.saveGameResult(result);

        // Show game result dialog
        GameResultDialogFragment resultDialog = new GameResultDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("GAME_RESULT", result);
        resultDialog.setArguments(args);
        resultDialog.show(getSupportFragmentManager(), "GameResult");
    }
}