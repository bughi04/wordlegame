package com.example.wordlegame;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

    private ListView historyListView;
    private Spinner filterSpinner;
    private TextView emptyStateTextView;

    private WordleRepository repository;
    private GameHistoryAdapter historyAdapter;
    private List<GameResult> gameResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Game History");

        // Initialize views
        historyListView = findViewById(R.id.list_history);
        filterSpinner = findViewById(R.id.spinner_filter);
        emptyStateTextView = findViewById(R.id.text_empty_state);

        // Initialize repository
        repository = new WordleRepository(this);

        // Initialize list and adapter
        gameResults = new ArrayList<>();
        historyAdapter = new GameHistoryAdapter(this, gameResults);
        historyListView.setAdapter(historyAdapter);

        // Set up filter spinner
        setupFilterSpinner();

        // Load game results
        loadGameResults(null);

        // Set up list item click listener
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showGameResultDetails(gameResults.get(position));
            }
        });
    }

    private void setupFilterSpinner() {
        // Create adapter with game mode options
        List<String> filterOptions = new ArrayList<>();
        filterOptions.add("All Game Modes");

        for (GameMode mode : GameMode.values()) {
            filterOptions.add(mode.getDisplayName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, filterOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        filterSpinner.setAdapter(spinnerAdapter);

        // Set listener for filter selection
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // "All Game Modes" selected
                    loadGameResults(null);
                } else {
                    // Specific game mode selected
                    loadGameResults(GameMode.values()[position - 1]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadGameResults(GameMode filterMode) {
        // Clear current results
        gameResults.clear();

        // Load results based on filter
        if (filterMode == null) {
            gameResults.addAll(repository.getAllGameResults());
        } else {
            gameResults.addAll(repository.getGameResultsByMode(filterMode));
        }

        // Update adapter
        historyAdapter.notifyDataSetChanged();

        // Show empty state if no results
        if (gameResults.isEmpty()) {
            historyListView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            historyListView.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);
        }
    }

    private void showGameResultDetails(GameResult result) {
        // Create dialog to show details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Details");

        // Build detail message
        StringBuilder details = new StringBuilder();
        details.append("Word: ").append(result.getWord().toUpperCase()).append("\n\n");
        details.append("Game Mode: ").append(result.getGameMode().getDisplayName()).append("\n\n");
        details.append("Result: ").append(result.isWin() ? "WIN" : "LOSS").append("\n\n");
        details.append("Guesses Used: ").append(result.getGuessesUsed()).append("\n\n");
        details.append("Time Taken: ").append(result.getFormattedTime()).append("\n\n");
        details.append("Date: ").append(result.getDatePlayed());

        builder.setMessage(details.toString());
        builder.setPositiveButton("Close", null);

        // Add delete option
        builder.setNegativeButton("Delete", (dialog, which) -> {
            deleteGameResult(result);
        });

        builder.show();
    }

    private void deleteGameResult(GameResult result) {
        // Confirm deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Game Result");
        builder.setMessage("Are you sure you want to delete this game result?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            // Delete result from database
            repository.deleteGameResult(result.getId());

            // Reload game results
            GameMode filterMode = filterSpinner.getSelectedItemPosition() == 0 ?
                    null : GameMode.values()[filterSpinner.getSelectedItemPosition() - 1];
            loadGameResults(filterMode);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Navigate back
            finish();
            return true;
        } else if (id == R.id.action_clear_history) {
            // Clear history
            showClearHistoryConfirmation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showClearHistoryConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear History");
        builder.setMessage("Are you sure you want to clear all game history? This action cannot be undone.");

        builder.setPositiveButton("Clear", (dialog, which) -> {
            // Clear all game results
            repository.clearAllGameResults();

            // Reload empty list
            loadGameResults(null);

            // Reset spinner to "All"
            filterSpinner.setSelection(0);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}