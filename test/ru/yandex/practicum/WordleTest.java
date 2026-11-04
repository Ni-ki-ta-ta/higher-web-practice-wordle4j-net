package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WordleTest {

    @Test
    void shouldCreateGameSuccessfully() {

        WordleDictionary dictionary =
                new WordleDictionary(
                        List.of(
                                "кошка",
                                "собак"
                        )
                );

        WordleGame game =
                new WordleGame(
                        dictionary,
                        "кошка"
                );

        assertNotNull(
                game,
                "Игра должна успешно создаваться"
        );

        assertEquals(
                6,
                game.getSteps(),
                "Новая игра должна начинаться с шести попыток"
        );
    }
}