package com.example.wordlegame;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordlegame.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for the Wordle game board rows
 */
public class WordleRowAdapter extends RecyclerView.Adapter<WordleRowAdapter.WordleRowViewHolder> {

    private final List<String> guesses;
    private final int wordLength;
    private final Map<Integer, List<Integer>> rowResults; // Maps row index to result list

    public WordleRowAdapter(List<String> guesses, int wordLength, List<Integer> initialRowResults) {
        this.guesses = guesses;
        this.wordLength = wordLength;
        this.rowResults = new HashMap<>();

        // Initialize with empty results
        for (int i = 0; i < guesses.size(); i++) {
            this.rowResults.put(i, new ArrayList<>());
        }
    }

    @NonNull
    @Override
    public WordleRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wordle_row, parent, false);
        return new WordleRowViewHolder(view, wordLength);
    }

    @Override
    public void onBindViewHolder(@NonNull WordleRowViewHolder holder, int position) {
        String guess = guesses.get(position);
        List<Integer> results = rowResults.get(position);
        holder.bind(guess, results, position);
    }

    @Override
    public int getItemCount() {
        return guesses.size();
    }

    /**
     * Set result for a specific row
     * @param rowIndex Index of the row
     * @param results List of result codes (0: wrong, 1: partial, 2: correct)
     */
    public void setRowResult(int rowIndex, List<Integer> results) {
        if (rowIndex >= 0 && rowIndex < guesses.size()) {
            rowResults.put(rowIndex, results);
        }
    }

    static class WordleRowViewHolder extends RecyclerView.ViewHolder {
        private final List<TextView> tiles;
        private final List<View> tileBgs;

        WordleRowViewHolder(@NonNull View itemView, int wordLength) {
            super(itemView);

            // Find all tile views
            tiles = new ArrayList<>();
            tileBgs = new ArrayList<>();

            ViewGroup tileContainer = itemView.findViewById(R.id.tile_container);
            for (int i = 0; i < tileContainer.getChildCount(); i++) {
                View tileView = tileContainer.getChildAt(i);
                tiles.add(tileView.findViewById(R.id.text_tile));
                tileBgs.add(tileView.findViewById(R.id.tile_background));
            }
        }

        void bind(String guess, List<Integer> results, int rowIndex) {
            // Fill tiles with characters from guess
            for (int i = 0; i < tiles.size(); i++) {
                String character = (i < guess.length()) ?
                        String.valueOf(guess.charAt(i)).toUpperCase() : "";
                tiles.get(i).setText(character);

                // Set default appearance for all tiles
                if (!character.isEmpty() && !character.equals(" ")) {
                    tileBgs.get(i).setBackgroundResource(R.drawable.tile_filled);
                } else {
                    tileBgs.get(i).setBackgroundResource(R.drawable.tile_empty);
                }
            }

            // If results exist for this row, apply colors and animate
            if (results != null && !results.isEmpty()) {
                for (int i = 0; i < results.size() && i < tiles.size(); i++) {
                    int result = results.get(i);
                    int colorResId;

                    switch (result) {
                        case 2: // Correct position
                            colorResId = R.color.tile_correct;
                            break;
                        case 1: // Wrong position
                            colorResId = R.color.tile_partial;
                            break;
                        default: // Not in word
                            colorResId = R.color.tile_wrong;
                            break;
                    }

                    // Get the tile background view
                    View tileBg = tileBgs.get(i);

                    // Apply color with animation
                    int finalI = i;
                    tileBg.postDelayed(() -> {
                        // Flip animation
                        AnimatorSet animatorSet = new AnimatorSet();

                        // First half of flip (show back)
                        ObjectAnimator flipOut = ObjectAnimator.ofFloat(
                                tileBg, "rotationX", 0f, 90f);
                        flipOut.setDuration(150);
                        flipOut.setInterpolator(new AccelerateDecelerateInterpolator());

                        // Second half of flip (show front with new color)
                        ObjectAnimator flipIn = ObjectAnimator.ofFloat(
                                tileBg, "rotationX", -90f, 0f);
                        flipIn.setDuration(150);
                        flipIn.setInterpolator(new AccelerateDecelerateInterpolator());

                        // When flipped 90 degrees (vertical), change the background color
                        flipOut.addListener(new android.animation.AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(android.animation.Animator animation) {
                                tileBg.setBackgroundColor(ContextCompat.getColor(
                                        tileBg.getContext(), colorResId));

                                // Also change text color to white
                                tiles.get(finalI).setTextColor(ContextCompat.getColor(
                                        tileBg.getContext(), android.R.color.white));
                            }
                        });

                        animatorSet.playSequentially(flipOut, flipIn);
                        animatorSet.start();
                    }, i * 200); // Stagger animation for each tile
                }
            }
        }
    }
}