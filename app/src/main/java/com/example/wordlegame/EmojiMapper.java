package com.example.wordlegame;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Utility class to map words to emojis for the Emoji game mode
 */
public class EmojiMapper {

    private static final Map<String, String[]> WORD_TO_EMOJIS = new HashMap<>();
    private final Random random = new Random();

    // Static initializer to populate the emoji mappings
    static {
        // Animals
        WORD_TO_EMOJIS.put("tiger", new String[]{"🐯", "🐅", "🏞️", "🇮🇳", "🦁"});
        WORD_TO_EMOJIS.put("snake", new String[]{"🐍", "🔄", "🍎", "🏜️", "⚠️"});
        WORD_TO_EMOJIS.put("eagle", new String[]{"🦅", "🇺🇸", "👁️", "🌄", "🏆"});
        WORD_TO_EMOJIS.put("shark", new String[]{"🦈", "🌊", "🩸", "🏊", "⚠️"});
        WORD_TO_EMOJIS.put("panda", new String[]{"🐼", "🎋", "🇨🇳", "⚫", "⚪"});

        // Food
        WORD_TO_EMOJIS.put("pizza", new String[]{"🍕", "🇮🇹", "🧀", "🍅", "👨‍🍳"});
        WORD_TO_EMOJIS.put("sushi", new String[]{"🍣", "🇯🇵", "🐟", "🌊", "🥢"});
        WORD_TO_EMOJIS.put("pasta", new String[]{"🍝", "🇮🇹", "🧀", "🍲", "👨‍🍳"});
        WORD_TO_EMOJIS.put("bacon", new String[]{"🥓", "🐷", "🍳", "🥞", "🏨"});
        WORD_TO_EMOJIS.put("mango", new String[]{"🥭", "🌴", "🇮🇳", "🌡️", "🧃"});

        // Places
        WORD_TO_EMOJIS.put("beach", new String[]{"🏖️", "🌊", "🏄", "☀️", "🐚"});
        WORD_TO_EMOJIS.put("hotel", new String[]{"🏨", "🛏️", "💤", "🧳", "🔑"});
        WORD_TO_EMOJIS.put("paris", new String[]{"🗼", "🇫🇷", "🍷", "🥖", "🎨"});
        WORD_TO_EMOJIS.put("space", new String[]{"🌠", "🚀", "👨‍🚀", "🛰️", "🌌"});
        WORD_TO_EMOJIS.put("house", new String[]{"🏠", "👨‍👩‍👧‍👦", "🪟", "🚪", "🏡"});

        // Objects
        WORD_TO_EMOJIS.put("phone", new String[]{"📱", "☎️", "💬", "📸", "🔋"});
        WORD_TO_EMOJIS.put("watch", new String[]{"⌚", "⏰", "👀", "🕒", "💪"});
        WORD_TO_EMOJIS.put("money", new String[]{"💰", "💵", "💲", "🏦", "🤑"});
        WORD_TO_EMOJIS.put("knife", new String[]{"🔪", "🥩", "👨‍🍳", "🔪", "🍽️"});
        WORD_TO_EMOJIS.put("light", new String[]{"💡", "🔆", "🌞", "👁️", "⚡"});

        // Actions
        WORD_TO_EMOJIS.put("sleep", new String[]{"😴", "🛏️", "💤", "🌙", "👁️"});
        WORD_TO_EMOJIS.put("dance", new String[]{"💃", "🕺", "🎵", "🎶", "🎭"});
        WORD_TO_EMOJIS.put("drink", new String[]{"🍷", "🥤", "🚰", "🍻", "🧃"});
        WORD_TO_EMOJIS.put("laugh", new String[]{"😂", "🤣", "😹", "🎭", "👄"});
        WORD_TO_EMOJIS.put("study", new String[]{"📚", "🧠", "🎓", "✏️", "🏫"});
    }

    /**
     * Gets a random word from the emoji dictionary
     * @return A random word
     */
    public String getRandomWord() {
        Object[] keys = WORD_TO_EMOJIS.keySet().toArray();
        return (String) keys[random.nextInt(keys.length)];
    }

    /**
     * Gets the emojis for a specific word
     * @param word The word to get emojis for
     * @return Array of emojis, or null if word not found
     */
    public String[] getEmojisForWord(String word) {
        return WORD_TO_EMOJIS.get(word.toLowerCase());
    }

    /**
     * Checks if a word is in the emoji dictionary
     * @param word The word to check
     * @return true if word is in the dictionary, false otherwise
     */
    public boolean hasWord(String word) {
        return WORD_TO_EMOJIS.containsKey(word.toLowerCase());
    }

    /**
     * Gets all available emoji words
     * @return Array of all words in the emoji dictionary
     */
    public String[] getAllWords() {
        return WORD_TO_EMOJIS.keySet().toArray(new String[0]);
    }
}