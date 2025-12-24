package com.example.wordlegame;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for the timed Wordle game mode
 */
public class TimedGameFragment extends Fragment implements KeyboardAdapter.OnKeyClickListener, GameTimer.TimerListener {

    private static final int MAX_ATTEMPTS = 6;
    private static final int WORD_LENGTH = 5;
    private static final int TIME_PER_GUESS = 30; // seconds

    private RecyclerView boardRecyclerView;
    private RecyclerView keyboardRecyclerView;
    private TextView timerTextView;
    private TextView attemptsTextView;

    private WordleRowAdapter boardAdapter;
    private KeyboardAdapter keyboardAdapter;

    private GameMode gameMode;
    private String currentWord;
    private List<String> guesses;
    private int currentAttempt;
    private int currentColumn;
    private boolean gameActive;
    private GameTimer gameTimer;
    private int totalTimeTaken;

    private GameActivity gameActivity;
    private WordGenerator wordGenerator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_normal_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        boardRecyclerView = view.findViewById(R.id.recycler_board);
        keyboardRecyclerView = view.findViewById(R.id.recycler_keyboard);
        timerTextView = view.findViewById(R.id.text_timer);
        attemptsTextView = view.findViewById(R.id.text_attempts);

        // Get game mode from arguments
        if (getArguments() != null) {
            gameMode = (GameMode) getArguments().getSerializable("GAME_MODE");
        } else {
            gameMode = GameMode.TIMED;
        }

        // Initialize game state
        guesses = new ArrayList<>();
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            StringBuilder emptyRow = new StringBuilder();
            for (int j = 0; j < WORD_LENGTH; j++) {
                emptyRow.append(" ");
            }
            guesses.add(emptyRow.toString());
        }

        currentAttempt = 0;
        currentColumn = 0;
        gameActive = true;
        totalTimeTaken = 0;

        // Initialize adapters
        setupBoardAdapter();
        setupKeyboardAdapter();

        // Initialize word generator and get secret word
        wordGenerator = new WordGenerator(requireContext());
        currentWord = wordGenerator.getRandomWord(false); // Use easy words for timed mode

        // Initialize and start timer
        gameTimer = new GameTimer(TIME_PER_GUESS, this);
        gameTimer.start();

        // Update attempts text
        updateAttemptsText();
    }

    private void setupBoardAdapter() {
        boardAdapter = new WordleRowAdapter(guesses, WORD_LENGTH, new ArrayList<>());
        boardRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        boardRecyclerView.setAdapter(boardAdapter);
    }

    private void setupKeyboardAdapter() {
        keyboardAdapter = new KeyboardAdapter(this);

        // Create a grid layout manager with proper configuration for the keyboard
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 10); // 10 columns for widest row
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Make ENTER and DELETE keys wider (take up more columns)
                String key = keyboardAdapter.getKeyAtPosition(position);
                if (key.equals("ENTER") || key.equals("DELETE")) {
                    return 2; // Takes up 2 columns
                }
                return 1; // Regular keys take 1 column
            }
        });

        keyboardRecyclerView.setLayoutManager(layoutManager);
        keyboardRecyclerView.setAdapter(keyboardAdapter);
    }

    private void updateAttemptsText() {
        attemptsTextView.setText(String.format("Attempt %d/%d", currentAttempt + 1, MAX_ATTEMPTS));
    }

    @Override
    public void onKeyClick(String key) {
        if (!gameActive) {
            return;
        }

        if (key.equals("ENTER")) {
            submitGuess();
        } else if (key.equals("DELETE")) {
            deleteCharacter();
        } else if (currentColumn < WORD_LENGTH && key.length() == 1) {
            addCharacter(key);
        }
    }

    private void addCharacter(String character) {
        if (currentColumn < WORD_LENGTH) {
            String currentGuess = guesses.get(currentAttempt);
            StringBuilder newGuess = new StringBuilder(currentGuess);
            newGuess.setCharAt(currentColumn, character.charAt(0));
            guesses.set(currentAttempt, newGuess.toString());
            boardAdapter.notifyItemChanged(currentAttempt);
            currentColumn++;
        }
    }

    private void deleteCharacter() {
        if (currentColumn > 0) {
            currentColumn--;
            String currentGuess = guesses.get(currentAttempt);
            StringBuilder newGuess = new StringBuilder(currentGuess);
            newGuess.setCharAt(currentColumn, ' ');
            guesses.set(currentAttempt, newGuess.toString());
            boardAdapter.notifyItemChanged(currentAttempt);
        }
    }

    private void submitGuess() {
        // Check if the guess is complete
        if (currentColumn != WORD_LENGTH) {
            Toast.makeText(getContext(), R.string.not_enough_letters, Toast.LENGTH_SHORT).show();
            return;
        }

        String guess = guesses.get(currentAttempt).trim();

        // Check if the guess is a valid word
        if (!wordGenerator.isValidWord(guess)) {
            Toast.makeText(getContext(), R.string.not_in_word_list, Toast.LENGTH_SHORT).show();
            return;
        }

        // Process the guess
        List<Integer> results = checkGuess(guess);
        boardAdapter.setRowResult(currentAttempt, results);
        boardAdapter.notifyItemChanged(currentAttempt);

        // Update keyboard colors
        keyboardAdapter.updateKeyboardState(guess, results);
        keyboardAdapter.notifyDataSetChanged();

        // Add time taken for this guess to total
        totalTimeTaken += TIME_PER_GUESS - gameTimer.getRemainingSeconds();

        // Check for win
        if (guess.equalsIgnoreCase(currentWord)) {
            endGame(true);
            return;
        }

        // Move to next attempt
        currentAttempt++;
        currentColumn = 0;

        // Check for game over
        if (currentAttempt >= MAX_ATTEMPTS) {
            endGame(false);
            return;
        }

        // Update attempts text
        updateAttemptsText();

        // Reset timer for next guess
        gameTimer.reset(TIME_PER_GUESS);
        gameTimer.start();
    }

    private List<Integer> checkGuess(String guess) {
        List<Integer> results = new ArrayList<>();

        for (int i = 0; i < WORD_LENGTH; i++) {
            char guessChar = Character.toLowerCase(guess.charAt(i));
            char targetChar = Character.toLowerCase(currentWord.charAt(i));

            if (guessChar == targetChar) {
                // Correct letter, correct position
                results.add(2);
            } else if (currentWord.toLowerCase().indexOf(guessChar) != -1) {
                // Correct letter, wrong position
                results.add(1);
            } else {
                // Letter not in word
                results.add(0);
            }
        }

        return results;
    }

    private void endGame(boolean isWin) {
        gameActive = false;
        gameTimer.pause();

        // Create game result
        GameResult result = new GameResult(
                currentWord,
                gameMode,
                totalTimeTaken,
                currentAttempt + 1,
                isWin
        );

        // Notify activity of game completion
        if (gameActivity != null) {
            gameActivity.onGameComplete(result);
        }
    }

    @Override
    public void onTick(long secondsRemaining) {
        // Update timer display
        timerTextView.setText(String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60));
    }

    @Override
    public void onFinish() {
        // Time's up for this guess
        Toast.makeText(getContext(), "Time's up!", Toast.LENGTH_SHORT).show();

        // Add full time for this guess to total
        totalTimeTaken += TIME_PER_GUESS;

        // Show correct row with no matches
        List<Integer> noMatches = new ArrayList<>();
        for (int i = 0; i < WORD_LENGTH; i++) {
            noMatches.add(0);
        }
        boardAdapter.setRowResult(currentAttempt, noMatches);
        boardAdapter.notifyItemChanged(currentAttempt);

        // Move to next attempt
        currentAttempt++;
        currentColumn = 0;

        // Check for game over
        if (currentAttempt >= MAX_ATTEMPTS) {
            endGame(false);
            return;
        }

        // Update attempts text
        updateAttemptsText();

        // Reset timer for next guess
        gameTimer.reset(TIME_PER_GUESS);
        gameTimer.start();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GameActivity) {
            gameActivity = (GameActivity) context;
        } else {
            throw new RuntimeException(context + " must be a GameActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        gameActivity = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gameActive && gameTimer != null) {
            gameTimer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gameActive && gameTimer != null && !gameTimer.isRunning()) {
            gameTimer.start();
        }
    }
}