package com.example.wordlegame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wordlegame.GameMode;
import com.example.wordlegame.GameResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for database operations
 */
public class WordleRepository {

    private final WordleDbHelper dbHelper;

    /**
     * Constructor
     */
    public WordleRepository(Context context) {
        dbHelper = new WordleDbHelper(context);
    }

    /**
     * Saves a game result to the database
     * @param result The game result to save
     * @return The row ID of the newly inserted result, or -1 if an error occurred
     */
    public long saveGameResult(GameResult result) {
        // Get writable database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create content values
        ContentValues values = new ContentValues();
        values.put(WordleContract.GameResultEntry.COLUMN_WORD, result.getWord());
        values.put(WordleContract.GameResultEntry.COLUMN_GAME_MODE, result.getGameMode().toString());
        values.put(WordleContract.GameResultEntry.COLUMN_TIME_TAKEN, result.getTimeTaken());
        values.put(WordleContract.GameResultEntry.COLUMN_GUESSES_USED, result.getGuessesUsed());
        values.put(WordleContract.GameResultEntry.COLUMN_RESULT, result.isWin() ? "WIN" : "LOSS");
        values.put(WordleContract.GameResultEntry.COLUMN_DATE_PLAYED, result.getDatePlayed());

        // Insert row
        long newRowId = db.insert(WordleContract.GameResultEntry.TABLE_NAME, null, values);

        // Close database
        db.close();

        return newRowId;
    }

    /**
     * Gets all game results from the database
     * @return List of game results
     */
    public List<GameResult> getAllGameResults() {
        List<GameResult> results = new ArrayList<>();

        // Query to select all records from the game results table, ordered by date
        String selectQuery = "SELECT * FROM " + WordleContract.GameResultEntry.TABLE_NAME +
                " ORDER BY " + WordleContract.GameResultEntry.COLUMN_DATE_PLAYED + " DESC";

        // Get readable database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                GameResult result = new GameResult();

                result.setId(cursor.getLong(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry._ID)));
                result.setWord(cursor.getString(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_WORD)));

                // Parse game mode
                String gameModeStr = cursor.getString(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_GAME_MODE));
                result.setGameMode(GameMode.valueOf(gameModeStr));

                result.setTimeTaken(cursor.getInt(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_TIME_TAKEN)));
                result.setGuessesUsed(cursor.getInt(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_GUESSES_USED)));

                // Parse result
                String resultStr = cursor.getString(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_RESULT));
                result.setWin(resultStr.equals("WIN"));

                result.setDatePlayed(cursor.getString(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_DATE_PLAYED)));

                results.add(result);
            } while (cursor.moveToNext());
        }

        // Close cursor and database
        cursor.close();
        db.close();

        return results;
    }

    /**
     * Gets game results filtered by game mode
     * @param gameMode The game mode to filter by
     * @return List of filtered game results
     */
    public List<GameResult> getGameResultsByMode(GameMode gameMode) {
        List<GameResult> results = new ArrayList<>();

        // Query to select records with specific game mode
        String selectQuery = "SELECT * FROM " + WordleContract.GameResultEntry.TABLE_NAME +
                " WHERE " + WordleContract.GameResultEntry.COLUMN_GAME_MODE + " = ?" +
                " ORDER BY " + WordleContract.GameResultEntry.COLUMN_DATE_PLAYED + " DESC";

        // Get readable database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{gameMode.toString()});

        // Loop through all rows and add to list
        if (cursor.moveToFirst()) {
            do {
                GameResult result = new GameResult();

                result.setId(cursor.getLong(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry._ID)));
                result.setWord(cursor.getString(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_WORD)));
                result.setGameMode(gameMode);
                result.setTimeTaken(cursor.getInt(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_TIME_TAKEN)));
                result.setGuessesUsed(cursor.getInt(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_GUESSES_USED)));

                // Parse result
                String resultStr = cursor.getString(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_RESULT));
                result.setWin(resultStr.equals("WIN"));

                result.setDatePlayed(cursor.getString(cursor.getColumnIndexOrThrow(WordleContract.GameResultEntry.COLUMN_DATE_PLAYED)));

                results.add(result);
            } while (cursor.moveToNext());
        }

        // Close cursor and database
        cursor.close();
        db.close();

        return results;
    }

    /**
     * Deletes a game result from the database
     * @param resultId The ID of the result to delete
     * @return The number of rows affected
     */
    public int deleteGameResult(long resultId) {
        // Get writable database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete record
        int rowsDeleted = db.delete(
                WordleContract.GameResultEntry.TABLE_NAME,
                WordleContract.GameResultEntry._ID + " = ?",
                new String[]{String.valueOf(resultId)});

        // Close database
        db.close();

        return rowsDeleted;
    }

    /**
     * Clears all game results from the database
     * @return The number of rows affected
     */
    public int clearAllGameResults() {
        // Get writable database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Delete all records
        int rowsDeleted = db.delete(WordleContract.GameResultEntry.TABLE_NAME, null, null);

        // Close database
        db.close();

        return rowsDeleted;
    }
}