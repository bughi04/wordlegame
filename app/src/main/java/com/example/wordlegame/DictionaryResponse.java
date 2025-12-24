package com.example.wordlegame;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DictionaryResponse {
    @SerializedName("word")
    private String word;

    @SerializedName("phonetic")
    private String phonetic;

    @SerializedName("meanings")
    private List<Meaning> meanings;

    public String getWord() {
        return word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public static class Meaning {
        @SerializedName("partOfSpeech")
        private String partOfSpeech;

        @SerializedName("definitions")
        private List<Definition> definitions;

        public String getPartOfSpeech() {
            return partOfSpeech;
        }

        public List<Definition> getDefinitions() {
            return definitions;
        }
    }

    public static class Definition {
        @SerializedName("definition")
        private String definition;

        @SerializedName("example")
        private String example;

        public String getDefinition() {
            return definition;
        }

        public String getExample() {
            return example;
        }
    }
}