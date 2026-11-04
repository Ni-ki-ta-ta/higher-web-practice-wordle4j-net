package ru.yandex.practicum;

public class EmptyDictionaryException
        extends RuntimeException {

    public EmptyDictionaryException(
            String message
    ) {
        super(message);
    }
}