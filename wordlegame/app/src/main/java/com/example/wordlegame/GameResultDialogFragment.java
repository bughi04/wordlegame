package com.example.wordlegame;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.wordlegame.GameResult;

/**
 * Dialog that shows the result of a completed game
 */
public class GameResultDialogFragment extends DialogFragment {

    private GameResult gameResult;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Inflate the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_game_result, null);

        // Get game result from arguments
        if (getArguments() != null) {
            gameResult = getArguments().getParcelable("GAME_RESULT");
        }

        // Initialize UI components
        TextView resultText = dialogView.findViewById(R.id.text_result);
        TextView wordText = dialogView.findViewById(R.id.text_word);
        TextView statsText = dialogView.findViewById(R.id.text_stats);
        Button playAgainButton = dialogView.findViewById(R.id.button_play_again);
        Button mainMenuButton = dialogView.findViewById(R.id.button_main_menu);

        // Set UI values based on game result
        if (gameResult != null) {
            // Set result text (win or lose)
            if (gameResult.isWin()) {
                resultText.setText("You Won!");
            } else {
                resultText.setText("Game Over");
            }

            // Set the secret word
            wordText.setText("The word was: " + gameResult.getWord().toUpperCase());

            // Set stats
            String stats = "Game Mode: " + gameResult.getGameMode().getDisplayName() + "\n" +
                    "Guesses Used: " + gameResult.getGuessesUsed() + "\n" +
                    "Time Taken: " + gameResult.getFormattedTime();
            statsText.setText(stats);
        }

        // Set click listeners for buttons
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a new game with the same mode
                if (gameResult != null) {
                    Intent intent = new Intent(getActivity(), GameActivity.class);
                    intent.putExtra("GAME_MODE", gameResult.getGameMode());
                    startActivity(intent);
                }

                // Close this activity and dialog
                if (getActivity() != null) {
                    getActivity().finish();
                }
                dismiss();
            }
        });

        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to main menu
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
                dismiss();
            }
        });

        // Set the view and make non-cancelable
        builder.setView(dialogView);
        builder.setCancelable(false);

        return builder.create();
    }
}