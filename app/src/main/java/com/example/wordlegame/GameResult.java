package com.example.wordlegame;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Model class to represent the result of a completed game
 */
public class GameResult implements Parcelable {

    private long id;
    private String word;
    private GameMode gameMode;
    private int timeTaken; // Time in seconds
    private int guessesUsed;
    private boolean isWin;
    private String datePlayed;

    public GameResult() {
        // Set default date to current date/time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.datePlayed = sdf.format(new Date());
    }

    public GameResult(String word, GameMode gameMode, int timeTaken, int guessesUsed, boolean isWin) {
        this();
        this.word = word;
        this.gameMode = gameMode;
        this.timeTaken = timeTaken;
        this.guessesUsed = guessesUsed;
        this.isWin = isWin;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getGuessesUsed() {
        return guessesUsed;
    }

    public void setGuessesUsed(int guessesUsed) {
        this.guessesUsed = guessesUsed;
    }

    public boolean isWin() {
        return isWin;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public String getDatePlayed() {
        return datePlayed;
    }

    public void setDatePlayed(String datePlayed) {
        this.datePlayed = datePlayed;
    }

    // Format time as MM:SS
    public String getFormattedTime() {
        int minutes = timeTaken / 60;
        int seconds = timeTaken % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    // Parcelable implementation
    protected GameResult(Parcel in) {
        id = in.readLong();
        word = in.readString();
        gameMode = (GameMode) in.readSerializable();
        timeTaken = in.readInt();
        guessesUsed = in.readInt();
        isWin = in.readByte() != 0;
        datePlayed = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(word);
        dest.writeSerializable(gameMode);
        dest.writeInt(timeTaken);
        dest.writeInt(guessesUsed);
        dest.writeByte((byte) (isWin ? 1 : 0));
        dest.writeString(datePlayed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GameResult> CREATOR = new Creator<GameResult>() {
        @Override
        public GameResult createFromParcel(Parcel in) {
            return new GameResult(in);
        }

        @Override
        public GameResult[] newArray(int size) {
            return new GameResult[size];
        }
    };
}