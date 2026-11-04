package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeEach
    void setUp() {
        dictionary =
                new WordleDictionary(
                        List.of(
                                "кошка",
                                "собак",
                                "ежикс",
                                "герой",
                                "гонец"
                        )
                );

        game =
                new WordleGame(
                        dictionary,
                        "кошка"
                );
    }

    @Test
    void shouldThrowExceptionForShortWord() {

        assertThrows(
                InvalidWordLengthException.class,
                () -> game.makeMove("кот"),
                "Для слова короче пяти букв должно выбрасываться исключение"
        );
    }

    @Test
    void shouldThrowExceptionForEnglishLetters() {

        assertThrows(
                InvalidWordException.class,
                () -> game.makeMove("hello"),
                "Слова с английскими буквами должны отклоняться"
        );
    }

    @Test
    void shouldWinWhenAnswerIsGuessed()
            throws Exception {

        game.makeMove("кошка");

        assertTrue(
                game.isWon(),
                "После ввода правильного слова игра должна перейти в состояние победы"
        );
    }

    @Test
    void shouldDecreaseStepsAfterCorrectMove()
            throws Exception {

        int initialSteps = game.getSteps();

        game.makeMove("кошка");

        assertEquals(
                initialSteps - 1,
                game.getSteps(),
                "После каждого корректного хода количество попыток должно уменьшаться"
        );
    }

    @Test
    void shouldSaveMoveHistory()
            throws Exception {

        game.makeMove("кошка");

        assertEquals(
                1,
                game.getGuesses().size(),
                "После хода история должна содержать одно слово"
        );

        assertEquals(
                "кошка",
                game.getGuesses().get(0),
                "В истории должно сохраняться введённое слово"
        );
    }

    @Test
    void shouldThrowExceptionForUnknownWord() {

        assertThrows(
                WordNotFoundInDictionaryException.class,
                () -> game.makeMove("тигрр"),
                "Слово отсутствующее в словаре должно отклоняться"
        );
    }

    @Test
    void shouldSaveHintsHistory()
            throws Exception {

        game.makeMove("кошка");

        assertEquals(
                1,
                game.getHints().size(),
                "После хода должна сохраняться подсказка"
        );

        assertEquals(
                "+++++",
                game.getHints().get(0),
                "Для полностью угаданного слова подсказка должна состоять из плюсов"
        );
    }

    @Test
    void shouldLoseAfterSixWrongAttempts()
            throws Exception {

        for (int i = 0; i < 6; i++) {
            game.makeMove("собак");
        }

        assertTrue(
                game.isLost(),
                "После шести неудачных попыток игра должна быть проиграна"
        );
    }

    @Test
    void shouldAcceptLetterYoAsLetterE()
            throws Exception {

        WordleDictionary yoDictionary =
                new WordleDictionary(
                        List.of("ежикс")
                );

        WordleGame yoGame =
                new WordleGame(
                        yoDictionary,
                        "ежикс"
                );

        yoGame.makeMove("ёжикс");

        assertTrue(
                yoGame.isWon(),
                "Буква ё должна обрабатываться как буква е"
        );
    }

    @Test
    void shouldGenerateCorrectHint()
            throws Exception {

        WordleDictionary hintDictionary =
                new WordleDictionary(
                        List.of(
                                "герой",
                                "гонец"
                        )
                );

        WordleGame hintGame =
                new WordleGame(
                        hintDictionary,
                        "герой"
                );

        String hint =
                hintGame.makeMove("гонец");

        assertEquals(
                "+^-^-",
                hint,
                "Подсказка должна соответствовать правилам Wordle"
        );
    }
}