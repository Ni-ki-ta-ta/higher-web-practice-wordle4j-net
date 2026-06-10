package ru.yandex.practicum;

public class WordNotFoundInDictionaryException extends Exception {

    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}