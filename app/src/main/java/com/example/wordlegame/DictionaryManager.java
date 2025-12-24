package com.example.wordlegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.wordlegame.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DictionaryManager {
    private static final String TAG = "DictionaryManager";
    private static final String PREFS_NAME = "WordleDictionaryPrefs";
    private static final String KEY_CACHED_WORDS = "cached_words";
    private static final String KEY_LAST_UPDATE = "last_update";
    private static final long CACHE_VALIDITY_MS = TimeUnit.DAYS.toMillis(7); // Cache valid for 7 days

    private static DictionaryManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final Random random;
    private final DictionaryApiService apiService;
    private final Set<String> cachedWords;
    private final Set<String> baseWords;

    private DictionaryManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.random = new Random();

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.dictionaryapi.dev/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(DictionaryApiService.class);

        // Load base words from raw resources
        baseWords = new HashSet<>();
        loadWordsFromResource(R.raw.words_easy);
        loadWordsFromResource(R.raw.words_hard);

        // Load cached words
        cachedWords = loadCachedWords();

        // Check if we need to refresh the cache
        if (shouldRefreshCache()) {
            refreshRandomWordCache();
        }
    }

    public static synchronized DictionaryManager getInstance(Context context) {
        if (instance == null) {
            instance = new DictionaryManager(context);
        }
        return instance;
    }

    private void loadWordsFromResource(int resourceId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.length() == 5) {
                    baseWords.add(line);
                }
            }

            reader.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error loading words from resource", e);
        }
    }

    private Set<String> loadCachedWords() {
        String cachedWordsJson = prefs.getString(KEY_CACHED_WORDS, null);
        if (cachedWordsJson != null) {
            Type type = new TypeToken<HashSet<String>>(){}.getType();
            return new Gson().fromJson(cachedWordsJson, type);
        }
        return new HashSet<>(baseWords); // Start with base words if no cache
    }

    private void saveCachedWords() {
        String cachedWordsJson = new Gson().toJson(cachedWords);
        prefs.edit()
                .putString(KEY_CACHED_WORDS, cachedWordsJson)
                .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                .apply();
    }

    private boolean shouldRefreshCache() {
        long lastUpdate = prefs.getLong(KEY_LAST_UPDATE, 0);
        return System.currentTimeMillis() - lastUpdate > CACHE_VALIDITY_MS;
    }

    public void refreshRandomWordCache() {
        // Add 50 random words to expand the dictionary
        for (int i = 0; i < 50; i++) {
            fetchRandomFiveLetterWord();
        }
    }

    private void fetchRandomFiveLetterWord() {
        // Generate a random 5-letter word pattern to try
        char[] consonants = "bcdfghjklmnpqrstvwxyz".toCharArray();
        char[] vowels = "aeiou".toCharArray();

        StringBuilder testWord = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                testWord.append(consonants[random.nextInt(consonants.length)]);
            } else {
                testWord.append(vowels[random.nextInt(vowels.length)]);
            }
        }

        // Check if this pattern is a valid word
        checkWord(testWord.toString());
    }

    public void checkWord(String word) {
        if (word.length() != 5) {
            return;
        }

        apiService.getWordDefinition(word).enqueue(new Callback<List<DictionaryResponse>>() {
            @Override
            public void onResponse(Call<List<DictionaryResponse>> call, Response<List<DictionaryResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String validWord = response.body().get(0).getWord().toLowerCase();
                    if (validWord.length() == 5) {
                        cachedWords.add(validWord);
                        saveCachedWords();
                        Log.d(TAG, "Added new word to dictionary: " + validWord);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DictionaryResponse>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    public String getRandomWord(boolean isHard) {
        List<String> wordsList = new ArrayList<>(cachedWords);

        if (wordsList.isEmpty()) {
            wordsList.addAll(baseWords); // Fallback to base words
        }

        return wordsList.get(random.nextInt(wordsList.size()));
    }

    public boolean isValidWord(String word) {
        // Check our local cache first
        if (cachedWords.contains(word.toLowerCase())) {
            return true;
        }

        // If not in cache, check base words
        if (baseWords.contains(word.toLowerCase())) {
            return true;
        }

        // If not found, query API (will add to cache if valid)
        checkWord(word);

        // Return false for now, we'll add it to cache for next time if valid
        return false;
    }

    public Set<String> getAllWords() {
        return new HashSet<>(cachedWords);
    }
}