package com.example.wordlegame;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DictionaryApiService {
    @GET("https://api.dictionaryapi.dev/api/v2/entries/en/{word}")
    Call<List<DictionaryResponse>> getWordDefinition(@Path("word") String word);
}