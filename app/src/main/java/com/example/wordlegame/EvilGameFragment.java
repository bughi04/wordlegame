package com.example.wordlegame;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
 * Fragment for the Evil Wordle game mode where the word changes after each guess
 */
public class EvilGameFragment extends Fragment implements KeyboardAdapter.OnKeyClickListener {

    private static final int MAX_ATTEMPTS = 6;
    private static final int WORD_LENGTH = 5;

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
    private long startTime;
    private long elapsedTime;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private boolean gameActive;

    private GameActivity gameActivity;
    private WordGenerator wordGenerator;
    private EvilWordleAlgorithm evilAlgorithm;

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
            gameMode = GameMode.EVIL;
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

        // Initialize adapters
        setupBoardAdapter();
        setupKeyboardAdapter();

        // Initialize word generator and get secret word
        wordGenerator = new WordGenerator(requireContext());
        currentWord = wordGenerator.getRandomWord(false); // Start with an easy word

        // Initialize evil wordle algorithm
        evilAlgorithm = new EvilWordleAlgorithm(wordGenerator, false);

        // Start timer
        startTimer();

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

    private void startTimer() {
        startTime = SystemClock.elapsedRealtime();
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = SystemClock.elapsedRealtime() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));

                if (gameActive) {
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void stopTimer() {
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
            elapsedTime = SystemClock.elapsedRealtime() - startTime;
        }
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
            Toast.makeText(getContext(), getString(R.string.not_enough_letters), Toast.LENGTH_SHORT).show();
            return;
        }

        String guess = guesses.get(currentAttempt).trim();

        // Check if the guess is a valid word
        if (!wordGenerator.isValidWord(guess)) {
            Toast.makeText(getContext(), getString(R.string.not_in_word_list), Toast.LENGTH_SHORT).show();
            return;
        }

        // Process the guess using the evil algorithm
        EvilWordleAlgorithm.ProcessResult result = evilAlgorithm.processGuess(guess, currentWord);
        List<Integer> resultCodes = result.getResultCodes();
        currentWord = result.getNewWord(); // Update to the new evil word

        // Update board with results
        boardAdapter.setRowResult(currentAttempt, resultCodes);
        boardAdapter.notifyItemChanged(currentAttempt);

        // Update keyboard colors
        keyboardAdapter.updateKeyboardState(guess, resultCodes);
        keyboardAdapter.notifyDataSetChanged();

        // Check if all positions are correct (a win)
        boolean isWin = true;
        for (Integer code : resultCodes) {
            if (code != 2) {
                isWin = false;
                break;
            }
        }

        if (isWin) {
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
    }

    private void endGame(boolean isWin) {
        gameActive = false;
        stopTimer();

        // Create game result
        GameResult result = new GameResult(
                currentWord, // Final word
                gameMode,
                (int) (elapsedTime / 1000),
                currentAttempt + 1,
                isWin
        );

        // Notify activity of game completion
        if (gameActivity != null) {
            gameActivity.onGameComplete(result);
        }
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
        if (gameActive) {
            stopTimer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gameActive && startTime > 0) {
            startTimer();
        }
    }
}