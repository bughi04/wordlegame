package com.example.wordlegame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordlegame.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for the keyboard RecyclerView
 */
public class KeyboardAdapter extends RecyclerView.Adapter<KeyboardAdapter.KeyViewHolder> {

    private static final String[][] KEYBOARD_LAYOUT = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"ENTER", "Z", "X", "C", "V", "B", "N", "M", "DELETE"}
    };

    private final List<String> keys;
    private final Map<String, Integer> keyStates; // 0: unused, 1: wrong, 2: partial, 3: correct
    private final OnKeyClickListener listener;

    public interface OnKeyClickListener {
        void onKeyClick(String key);
    }

    public KeyboardAdapter(OnKeyClickListener listener) {
        this.listener = listener;
        this.keys = new ArrayList<>();
        this.keyStates = new HashMap<>();

        // Flatten keyboard layout and initialize key states
        for (String[] row : KEYBOARD_LAYOUT) {
            keys.addAll(Arrays.asList(row));
        }

        for (String key : keys) {
            keyStates.put(key, 0);
        }
    }

    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_keyboard_key, parent, false);

        // Special handling for ENTER and DELETE keys (wider)
        String key = keys.get(viewType);
        if (key.equals("ENTER") || key.equals("DELETE")) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params instanceof GridLayoutManager.LayoutParams) {
                ((GridLayoutManager.LayoutParams) params).width =
                        (int) (parent.getContext().getResources().getDisplayMetrics().density * 60);
            }
        }

        return new KeyViewHolder(view);
    }
    public String getKeyAtPosition(int position) {
        if (position >= 0 && position < keys.size()) {
            return keys.get(position);
        }
        return "";
    }

    @Override
    public void onBindViewHolder(@NonNull KeyViewHolder holder, int position) {
        String key = keys.get(position);
        holder.bind(key, keyStates.get(key));
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Use position as view type to handle different key widths
        return position;
    }

    /**
     * Updates the keyboard state based on a guess result
     * @param guess The guess string
     * @param results List of result codes (0: wrong, 1: partial, 2: correct)
     */
    public void updateKeyboardState(String guess, List<Integer> results) {
        for (int i = 0; i < guess.length(); i++) {
            String key = String.valueOf(guess.charAt(i)).toUpperCase();
            int result = results.get(i);

            // Only update key state if it's an improvement (wrong < partial < correct)
            int currentState = keyStates.getOrDefault(key, 0);
            if (result + 1 > currentState) { // +1 to match our state codes
                keyStates.put(key, result + 1);
            }
        }
    }

    class KeyViewHolder extends RecyclerView.ViewHolder {
        private final TextView keyText;
        private final View keyBackground;

        KeyViewHolder(@NonNull View itemView) {
            super(itemView);
            keyText = itemView.findViewById(R.id.text_key);
            keyBackground = itemView.findViewById(R.id.key_background);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onKeyClick(keys.get(position));
                }
            });
        }

        void bind(String key, int state) {
            keyText.setText(key);

            // Set key appearance based on state
            Context context = itemView.getContext();

            switch (state) {
                case 1: // Wrong
                    keyBackground.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.key_wrong));
                    keyText.setTextColor(
                            ContextCompat.getColor(context, R.color.key_text_used));
                    break;
                case 2: // Partial
                    keyBackground.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.key_partial));
                    keyText.setTextColor(
                            ContextCompat.getColor(context, R.color.key_text_used));
                    break;
                case 3: // Correct
                    keyBackground.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.key_correct));
                    keyText.setTextColor(
                            ContextCompat.getColor(context, R.color.key_text_used));
                    break;
                default: // Unused
                    keyBackground.setBackgroundColor(
                            ContextCompat.getColor(context, R.color.key_unused));
                    keyText.setTextColor(
                            ContextCompat.getColor(context, R.color.key_text_unused));
                    break;
            }

            // Special handling for ENTER and DELETE keys
            if (key.equals("ENTER") || key.equals("DELETE")) {
                keyText.setTextSize(12);
            } else {
                keyText.setTextSize(18);
            }
        }
    }
}