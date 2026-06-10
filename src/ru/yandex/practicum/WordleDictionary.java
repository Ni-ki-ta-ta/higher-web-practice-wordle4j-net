package ru.yandex.practicum;

import java.util.*;

public class WordleDictionary {

    private final List<String> words;
    private final Set<String> wordsSet;
    private final Random random;

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
        this.wordsSet = new HashSet<>(words);
        this.random = new Random();
    }

    public static String normalize(String word) {
        if (word == null) {
            return "";
        }

        return word
                .trim()
                .toLowerCase()
                .replace('ё', 'е');
    }

    public boolean contains(String word) {
        return wordsSet.contains(normalize(word));
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст.");
        }

        return words.get(random.nextInt(words.size()));
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public int size() {
        return words.size();
    }
}