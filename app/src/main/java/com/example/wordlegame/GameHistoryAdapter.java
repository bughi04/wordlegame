package com.example.wordlegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.wordlegame.R;
import com.example.wordlegame.GameResult;

import java.util.List;

/**
 * Adapter for displaying game history in a ListView
 */
public class GameHistoryAdapter extends ArrayAdapter<GameResult> {

    private final Context context;
    private final List<GameResult> gameResults;

    public GameHistoryAdapter(Context context, List<GameResult> gameResults) {
        super(context, R.layout.item_game_history, gameResults);
        this.context = context;
        this.gameResults = gameResults;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_game_history, parent, false);

            holder = new ViewHolder();
            holder.wordText = convertView.findViewById(R.id.text_word);
            holder.gameModeText = convertView.findViewById(R.id.text_game_mode);
            holder.resultText = convertView.findViewById(R.id.text_result);
            holder.guessesText = convertView.findViewById(R.id.text_guesses);
            holder.timeText = convertView.findViewById(R.id.text_time);
            holder.dateText = convertView.findViewById(R.id.text_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the current game result
        GameResult result = gameResults.get(position);

        // Set the word text
        holder.wordText.setText(result.getWord().toUpperCase());

        // Set the game mode text
        holder.gameModeText.setText(result.getGameMode().getDisplayName());

        // Set the result text and color
        holder.resultText.setText(result.isWin() ? "WIN" : "LOSS");
        holder.resultText.setTextColor(ContextCompat.getColor(context,
                result.isWin() ? R.color.win_color : R.color.loss_color));

        // Set the guesses text
        holder.guessesText.setText("Guesses: " + result.getGuessesUsed());

        // Set the time text
        holder.timeText.setText("Time: " + result.getFormattedTime());

        // Set the date text
        holder.dateText.setText(result.getDatePlayed());

        return convertView;
    }

    private static class ViewHolder {
        TextView wordText;
        TextView gameModeText;
        TextView resultText;
        TextView guessesText;
        TextView timeText;
        TextView dateText;
    }
}