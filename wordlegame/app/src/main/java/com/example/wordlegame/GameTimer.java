package com.example.wordlegame;

import android.os.CountDownTimer;

/**
 * Utility class to manage countdown timer for timed game mode
 */
public class GameTimer {

    private static final long COUNTDOWN_INTERVAL = 1000; // Update every second

    private CountDownTimer timer;
    private boolean isRunning;
    private long timeRemaining;
    private final TimerListener listener;

    /**
     * Interface for timer updates
     */
    public interface TimerListener {
        void onTick(long secondsRemaining);
        void onFinish();
    }

    /**
     * Constructor
     * @param seconds Initial number of seconds
     * @param listener Listener for timer updates
     */
    public GameTimer(int seconds, TimerListener listener) {
        this.timeRemaining = seconds * 1000; // Convert to milliseconds
        this.listener = listener;
        this.isRunning = false;
    }

    /**
     * Starts the timer
     */
    public void start() {
        if (isRunning) {
            return;
        }

        timer = new CountDownTimer(timeRemaining, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                if (listener != null) {
                    listener.onTick(millisUntilFinished / 1000);
                }
            }

            @Override
            public void onFinish() {
                isRunning = false;
                timeRemaining = 0;
                if (listener != null) {
                    listener.onFinish();
                }
            }
        };

        timer.start();
        isRunning = true;
    }

    /**
     * Pauses the timer
     */
    public void pause() {
        if (isRunning && timer != null) {
            timer.cancel();
            isRunning = false;
        }
    }

    /**
     * Resets the timer to the specified seconds
     * @param seconds Number of seconds to reset to
     */
    public void reset(int seconds) {
        if (isRunning && timer != null) {
            timer.cancel();
        }

        timeRemaining = seconds * 1000;
        isRunning = false;
    }

    /**
     * Adds additional time to the timer
     * @param seconds Number of seconds to add
     */
    public void addTime(int seconds) {
        timeRemaining += seconds * 1000;

        if (isRunning) {
            // Restart with new time
            pause();
            start();
        }
    }

    /**
     * Checks if timer is currently running
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Gets the remaining time in seconds
     * @return Remaining seconds
     */
    public int getRemainingSeconds() {
        return (int) (timeRemaining / 1000);
    }
}