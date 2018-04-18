package com.microsoft.translator.service.app;

/**
 *
 */

public enum TranslationError {
    ERROR_NONE(0),

    ERROR_INVALID_KEY(100),
    ERROR_INVALID_FROM_LANGUAGE(101),
    ERROR_INVALID_TO_LANGUAGE(102),

    ERROR_INVALID_DATA(103),
    ERROR_USER_PRIVACY_AGREEMENT(105),

    ERROR_LIMIT_REACHED(106),

    ERROR_FROM_LANGUAGE_NOT_OFFLINE(201),
    ERROR_TO_LANGUAGE_NOT_OFFLINE(202),

    ERROR_OTHER(500),

    ERROR_NETWORK(1000);

    public final int value;

    TranslationError(int value) {
        this.value = value;
    }
}
