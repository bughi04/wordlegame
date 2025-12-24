package com.example.wordlegame;

import android.provider.BaseColumns;

/**
 * Contract class that defines database schema
 */
public final class WordleContract {

    // Private constructor to prevent instantiation
    private WordleContract() {}

    /**
     * Inner class that defines the game results table
     */
    public static class GameResultEntry implements BaseColumns {
        public static final String TABLE_NAME = "game_results";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_GAME_MODE = "game_mode";
        public static final String COLUMN_TIME_TAKEN = "time_taken";
        public static final String COLUMN_GUESSES_USED = "guesses_used";
        public static final String COLUMN_RESULT = "result";
        public static final String COLUMN_DATE_PLAYED = "date_played";

        // Create table statement
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_WORD + " TEXT NOT NULL," +
                        COLUMN_GAME_MODE + " TEXT NOT NULL," +
                        COLUMN_TIME_TAKEN + " INTEGER NOT NULL," +
                        COLUMN_GUESSES_USED + " INTEGER NOT NULL," +
                        COLUMN_RESULT + " TEXT NOT NULL," +
                        COLUMN_DATE_PLAYED + " TEXT NOT NULL)";

        // Delete table statement
        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * Inner class that defines the words table
     */
    public static class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_DIFFICULTY = "difficulty";

        // Create table statement
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_WORD + " TEXT NOT NULL," +
                        COLUMN_DIFFICULTY + " TEXT NOT NULL)";

        // Delete table statement
        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}