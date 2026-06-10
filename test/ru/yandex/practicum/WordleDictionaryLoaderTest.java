package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    @Test
    void shouldLoadOnlyFiveLetterWords()
            throws Exception {

        Path tempFile =
                Files.createTempFile(
                        "wordle-test",
                        ".txt"
                );

        Files.write(
                tempFile,
                """
                        кошка
                        дом
                        самолет
                        берег
                        """.getBytes(StandardCharsets.UTF_8)
        );

        WordleDictionaryLoader loader =
                new WordleDictionaryLoader(
                        new PrintWriter(System.out)
                );

        WordleDictionary dictionary =
                loader.load(tempFile.toString());

        assertEquals(
                2,
                dictionary.size(),
                "В словарь должны попадать только слова из пяти букв"
        );
    }

    @Test
    void shouldNormalizeWordsDuringLoading()
            throws Exception {

        Path tempFile =
                Files.createTempFile(
                        "wordle-test",
                        ".txt"
                );

        Files.write(
                tempFile,
                """
                        ЁЖИКС
                        """.getBytes(StandardCharsets.UTF_8)
        );

        WordleDictionaryLoader loader =
                new WordleDictionaryLoader(
                        new PrintWriter(System.out)
                );

        WordleDictionary dictionary =
                loader.load(tempFile.toString());

        assertTrue(
                dictionary.contains("ежикс"),
                "При загрузке слова должны нормализоваться"
        );
    }

    @Test
    void shouldThrowExceptionForEmptyDictionary()
            throws Exception {

        Path tempFile =
                Files.createTempFile(
                        "wordle-test",
                        ".txt"
                );

        Files.write(
                tempFile,
                """
                        дом
                        кот
                        """.getBytes(StandardCharsets.UTF_8)
        );

        WordleDictionaryLoader loader =
                new WordleDictionaryLoader(
                        new PrintWriter(System.out)
                );

        assertThrows(
                EmptyDictionaryException.class,
                () -> loader.load(tempFile.toString()),
                "Если после фильтрации не осталось слов, должно выбрасываться EmptyDictionaryException"
        );
    }
}