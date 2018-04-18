package com.microsoft.translator.local;

class BaseResult {
    public final int errorCode;
    public final String errorMessage;

    public BaseResult(int errorCode, String message) {
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public boolean isError() {
        return errorCode != Translator.ERROR_NONE;
    }
}
