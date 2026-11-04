package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    @Test
    void shouldConvertWordToLowerCase() {

        String result =
                WordleDictionary.normalize("КОшКа");

        assertEquals(
                "кошка",
                result,
                "Метод normalize должен переводить слово в нижний регистр"
        );
    }

    @Test
    void shouldReplaceYoWithE() {

        String result =
                WordleDictionary.normalize("ёжик");

        assertEquals(
                "ежик",
                result,
                "Буква ё должна заменяться на е"
        );
    }

    @Test
    void shouldContainExistingWord() {

        WordleDictionary dictionary =
                new WordleDictionary(
                        List.of("кошка", "собак")
                );

        assertTrue(
                dictionary.contains("кошка"),
                "Словарь должен содержать добавленное слово"
        );
    }

    @Test
    void shouldNotContainUnknownWord() {

        WordleDictionary dictionary =
                new WordleDictionary(
                        List.of("кошка", "собак")
                );

        assertFalse(
                dictionary.contains("тигрр"),
                "Словарь не должен содержать неизвестное слово"
        );
    }

    @Test
    void randomWordMustBelongToDictionary() {

        WordleDictionary dictionary =
                new WordleDictionary(
                        List.of(
                                "кошка",
                                "собак",
                                "тигрр"
                        )
                );

        String randomWord =
                dictionary.getRandomWord();

        assertTrue(
                dictionary.contains(randomWord),
                "Случайное слово должно присутствовать в словаре"
        );
    }
}