package com.example.wordlegame;

import android.content.Context;
import android.content.res.Resources;

import com.example.wordlegame.DictionaryManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Utility class for generating random words and validating guesses
 */
public class WordGenerator {

    private final List<String> easyWords;
    private final List<String> hardWords;
    private final Set<String> validWords;
    private final Random random;
    private final DictionaryManager dictionaryManager;

    public WordGenerator(Context context) {
        easyWords = new ArrayList<>();
        hardWords = new ArrayList<>();
        validWords = new HashSet<>();
        random = new Random();
        dictionaryManager = DictionaryManager.getInstance(context);

        // Load word lists from raw resources
        loadWordList(context, R.raw.words_easy, easyWords);
        loadWordList(context, R.raw.words_hard, hardWords);

        // Add all words to valid words set for validation
        validWords.addAll(easyWords);
        validWords.addAll(hardWords);
    }

    /**
     * Loads a word list from a raw resource file
     */
    private void loadWordList(Context context, int resourceId, List<String> wordList) {
        try {
            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Ensure word is 5 letters and convert to lowercase
                line = line.trim().toLowerCase();
                if (line.length() == 5) {
                    wordList.add(line);
                }
            }

            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a random word from the appropriate word list or from the dictionary API
     * @param isHard Whether to get a word from the hard list
     * @return A random 5-letter word
     */
    public String getRandomWord(boolean isHard) {
        // Use the DictionaryManager to get a random word
        return dictionaryManager.getRandomWord(isHard);
    }

    /**
     * Checks if a word is in the valid words list or the online dictionary
     * @param word The word to check
     * @return true if the word is valid, false otherwise
     */
    public boolean isValidWord(String word) {
        // First check our static list
        if (validWords.contains(word.toLowerCase())) {
            return true;
        }

        // Then check the dictionary manager
        return dictionaryManager.isValidWord(word.toLowerCase());
    }

    /**
     * Gets a subset of words that match the given pattern
     * Used for Evil Wordle mode
     * @param pattern The pattern to match (e.g. "a..b." where dots are wildcards)
     * @param excluded Set of characters to exclude
     * @param included Map of characters that must be included but not at specific positions
     * @param isHard Whether to use the hard word list
     * @return List of matching words
     */
    public List<String> getWordsMatchingPattern(
            String pattern,
            Set<Character> excluded,
            Map<Character, List<Integer>> included,
            boolean isHard) {

        List<String> result = new ArrayList<>();
        List<String> sourceList = isHard ? hardWords : easyWords;

        for (String word : sourceList) {
            if (matchesPattern(word, pattern, excluded, included)) {
                result.add(word);
            }
        }

        // If we have very few results, check the expanded dictionary too
        if (result.size() < 5) {
            for (String word : dictionaryManager.getAllWords()) {
                if (!sourceList.contains(word) && matchesPattern(word, pattern, excluded, included)) {
                    result.add(word);
                }
            }
        }

        return result;
    }

    /**
     * Checks if a word matches the given pattern and constraints
     */
    private boolean matchesPattern(
            String word,
            String pattern,
            Set<Character> excluded,
            Map<Character, List<Integer>> included) {

        // Check pattern (correct positions)
        for (int i = 0; i < pattern.length(); i++) {
            char patternChar = pattern.charAt(i);
            if (patternChar != '.' && patternChar != word.charAt(i)) {
                return false;
            }
        }

        // Check excluded characters
        for (char c : excluded) {
            if (word.indexOf(c) != -1) {
                return false;
            }
        }

        // Check included characters (wrong positions)
        for (Map.Entry<Character, List<Integer>> entry : included.entrySet()) {
            char c = entry.getKey();
            List<Integer> positions = entry.getValue();

            // Character must be present in the word
            if (word.indexOf(c) == -1) {
                return false;
            }

            // Character must not be at any of the specified positions
            for (int pos : positions) {
                if (word.charAt(pos) == c) {
                    return false;
                }
            }
        }

        return true;
    }
}