package com.example.wordlegame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Algorithm for Evil Wordle game mode
 * This class handles the logic for picking the most difficult valid word
 * after each guess to maximize the difficulty
 */
public class EvilWordleAlgorithm {

    private final WordGenerator wordGenerator;
    private List<String> possibleWords;
    private final boolean isHardMode;
    private final Random random;

    // Pattern tracking
    private String currentPattern; // Pattern with dots for unknown letters
    private Set<Character> excludedChars; // Letters that are not in the word
    private Map<Character, List<Integer>> includedChars; // Letters in word but in wrong positions

    /**
     * Constructor
     */
    public EvilWordleAlgorithm(WordGenerator wordGenerator, boolean isHardMode) {
        this.wordGenerator = wordGenerator;
        this.isHardMode = isHardMode;
        this.random = new Random();

        // Initialize pattern tracking
        currentPattern = "....."; // 5 dots for unknown letters
        excludedChars = new HashSet<>();
        includedChars = new HashMap<>();

        // Get initial possible words
        resetPossibleWords();
    }

    /**
     * Resets the possible words list to all valid words
     */
    private void resetPossibleWords() {
        if (isHardMode) {
            // Start with hard words
            possibleWords = wordGenerator.getWordsMatchingPattern(currentPattern, excludedChars, includedChars, true);

            // If not enough words, add easy words
            if (possibleWords.size() < 10) {
                possibleWords.addAll(wordGenerator.getWordsMatchingPattern(currentPattern, excludedChars, includedChars, false));
            }
        } else {
            // Start with easy words
            possibleWords = wordGenerator.getWordsMatchingPattern(currentPattern, excludedChars, includedChars, false);

            // If not enough words, add hard words
            if (possibleWords.size() < 10) {
                possibleWords.addAll(wordGenerator.getWordsMatchingPattern(currentPattern, excludedChars, includedChars, true));
            }
        }
    }

    /**
     * Processes a guess and updates pattern constraints
     * @param guess The player's guess
     * @param currentWord The current secret word
     * @return Updated result codes and a new secret word
     */
    public ProcessResult processGuess(String guess, String currentWord) {
        if (possibleWords.isEmpty()) {
            resetPossibleWords();
        }

        // Calculate result codes for current word
        List<Integer> resultCodes = new ArrayList<>();
        for (int i = 0; i < guess.length(); i++) {
            char guessChar = Character.toLowerCase(guess.charAt(i));
            char targetChar = Character.toLowerCase(currentWord.charAt(i));

            if (guessChar == targetChar) {
                // Correct letter, correct position
                resultCodes.add(2);

                // Update pattern with correct letter
                StringBuilder pattern = new StringBuilder(currentPattern);
                pattern.setCharAt(i, guessChar);
                currentPattern = pattern.toString();
            } else if (currentWord.toLowerCase().indexOf(guessChar) != -1) {
                // Correct letter, wrong position
                resultCodes.add(1);

                // Add to included chars
                if (!includedChars.containsKey(guessChar)) {
                    includedChars.put(guessChar, new ArrayList<>());
                }
                includedChars.get(guessChar).add(i);
            } else {
                // Letter not in word
                resultCodes.add(0);

                // Add to excluded chars
                excludedChars.add(guessChar);
            }
        }

        // Update possible words based on the new constraints
        List<String> newPossibleWords = wordGenerator.getWordsMatchingPattern(
                currentPattern, excludedChars, includedChars, isHardMode);

        // If we have no matching words, relax some constraints
        if (newPossibleWords.isEmpty()) {
            // Try with opposite difficulty
            newPossibleWords = wordGenerator.getWordsMatchingPattern(
                    currentPattern, excludedChars, includedChars, !isHardMode);

            // If still empty, reset all constraints except pattern
            if (newPossibleWords.isEmpty()) {
                excludedChars.clear();
                includedChars.clear();
                newPossibleWords = wordGenerator.getWordsMatchingPattern(
                        currentPattern, excludedChars, includedChars, isHardMode);

                if (newPossibleWords.isEmpty()) {
                    // Last resort: get word with just the correct positions
                    newPossibleWords = wordGenerator.getWordsMatchingPattern(
                            currentPattern, new HashSet<>(), new HashMap<>(), isHardMode);

                    if (newPossibleWords.isEmpty()) {
                        // Absolute last resort: any 5-letter word
                        newPossibleWords.add(wordGenerator.getRandomWord(isHardMode));
                    }
                }
            }
        }

        // Update possible words
        possibleWords = newPossibleWords;

        // Pick a new word from the possible words that maximizes difficulty
        String newWord;
        if (!possibleWords.isEmpty()) {
            newWord = possibleWords.get(random.nextInt(possibleWords.size()));
        } else {
            newWord = wordGenerator.getRandomWord(isHardMode);
        }

        return new ProcessResult(resultCodes, newWord);
    }

    /**
     * Class to hold the result of processing a guess
     */
    public static class ProcessResult {
        private final List<Integer> resultCodes;
        private final String newWord;

        ProcessResult(List<Integer> resultCodes, String newWord) {
            this.resultCodes = resultCodes;
            this.newWord = newWord;
        }

        public List<Integer> getResultCodes() {
            return resultCodes;
        }

        public String getNewWord() {
            return newWord;
        }
    }
}