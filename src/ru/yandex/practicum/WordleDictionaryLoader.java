package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    private static final int WORD_LENGTH = 5;

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String fileName) throws IOException {
        List<String> words = new ArrayList<>();

        log.println("Начата загрузка словаря: " + fileName);

        try (BufferedReader reader =
                     Files.newBufferedReader(
                             Path.of(fileName),
                             StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                String word = WordleDictionary.normalize(line);

                if (word.length() == WORD_LENGTH) {
                    words.add(word);
                }
            }
        }

        log.println("Загрузка завершена. Слов в словаре: " + words.size());

        if (words.isEmpty()) {
            throw new EmptyDictionaryException(
                    "Словарь пуст."
            );
        }

        return new WordleDictionary(words);
    }
}