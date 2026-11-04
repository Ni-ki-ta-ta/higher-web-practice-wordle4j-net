package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.List;

public class WordleGame {

    private static final int MAX_STEPS = 6;
    private static final int WORD_LENGTH = 5;

    private final String answer;
    private final WordleDictionary dictionary;

    private final List<String> guesses;
    private final List<String> hints;
    private final List<String> suggestedWords;

    private int steps;
    private boolean won;

    public WordleGame(WordleDictionary dictionary) {
        this.dictionary = dictionary;
        this.answer = dictionary.getRandomWord();

        this.steps = MAX_STEPS;
        this.won = false;

        this.guesses = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.suggestedWords = new ArrayList<>();
    }

    public WordleGame(
            WordleDictionary dictionary,
            String answer
    ) {
        this.dictionary = dictionary;
        this.answer = answer;

        this.steps = MAX_STEPS;
        this.won = false;

        this.guesses = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.suggestedWords = new ArrayList<>();
    }

    public String makeMove(String word)
            throws InvalidWordLengthException,
            InvalidWordException,
            WordNotFoundInDictionaryException {

        String guess = WordleDictionary.normalize(word);

        if (!guess.matches("[а-я]+")) {
            throw new InvalidWordException(
                    "Слово должно содержать только русские буквы."
            );
        }

        if (guess.length() != WORD_LENGTH) {
            throw new InvalidWordLengthException(
                    "Слово должно содержать 5 букв."
            );
        }

        if (!dictionary.contains(guess)) {
            throw new WordNotFoundInDictionaryException(
                    "Слово отсутствует в словаре."
            );
        }

        steps--;

        String hintString = generateHint(guess, answer);

        if (guess.equals(answer)) {
            won = true;
        }

        guesses.add(guess);
        hints.add(hintString);

        return hintString;
    }

    public boolean isWon() {
        return won;
    }

    public boolean isLost() {
        return !won && steps <= 0;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getGuesses() {
        return new ArrayList<>(guesses);
    }

    public List<String> getHints() {
        return new ArrayList<>(hints);
    }

    public List<String> getUsedSuggestions() {
        return new ArrayList<>(suggestedWords);
    }

    private String generateHint(String guess, String target) {

        char[] result = new char[WORD_LENGTH];
        boolean[] usedTargetLetters = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                result[i] = '+';
                usedTargetLetters[i] = true;
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {

            if (result[i] == '+') {
                continue;
            }

            char guessLetter = guess.charAt(i);
            boolean found = false;

            for (int j = 0; j < WORD_LENGTH; j++) {

                if (!usedTargetLetters[j]
                        && target.charAt(j) == guessLetter) {

                    usedTargetLetters[j] = true;
                    found = true;
                    break;
                }
            }

            result[i] = found ? '^' : '-';
        }

        return new String(result);
    }

    public String getSuggestion() {

        for (String candidate : dictionary.getWords()) {

            boolean matches = true;

            for (int i = 0; i < guesses.size(); i++) {

                String expectedHint = hints.get(i);

                String actualHint =
                        generateHint(guesses.get(i), candidate);

                if (!expectedHint.equals(actualHint)) {
                    matches = false;
                    break;
                }
            }

            if (matches
                    && !guesses.contains(candidate)
                    && !suggestedWords.contains(candidate)) {

                suggestedWords.add(candidate);
                return candidate;
            }
        }

        return null;
    }
}