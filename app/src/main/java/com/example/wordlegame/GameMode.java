package com.example.wordlegame;

/**
 * Enum representing different game modes in the Wordle game
 */
public enum GameMode {
    NORMAL_EASY,    // Normal Wordle with common words
    NORMAL_HARD,    // Normal Wordle with more difficult words
    TIMED,          // Timed mode with countdown for each guess
    EVIL,           // Evil Wordle that changes word after each guess
    EMOJI;          // Emoji-based Wordle game

    /**
     * Returns a user-friendly display name for the game mode
     */
    public String getDisplayName() {
        switch (this) {
            case NORMAL_EASY:
                return "Normal Wordle (Easy)";
            case NORMAL_HARD:
                return "Normal Wordle (Hard)";
            case TIMED:
                return "Timed Mode";
            case EVIL:
                return "Evil Wordle";
            case EMOJI:
                return "Emoji Game";
            default:
                return "Unknown";
        }
    }

    /**
     * Returns the description of the game mode
     */
    public String getDescription() {
        switch (this) {
            case NORMAL_EASY:
                return "Guess the 5-letter word within 6 attempts. Easy word list.";
            case NORMAL_HARD:
                return "Guess the 5-letter word within 6 attempts. Hard word list.";
            case TIMED:
                return "Guess each letter with a countdown timer for each attempt.";
            case EVIL:
                return "The game changes the secret word after each guess to maximize difficulty.";
            case EMOJI:
                return "Guess the word represented by the emoji clues.";
            default:
                return "";
        }
    }
}