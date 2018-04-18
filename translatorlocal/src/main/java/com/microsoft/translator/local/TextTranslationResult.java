package com.microsoft.translator.local;

import com.microsoft.translator.service.app.TranslationArrayResult;

import java.util.List;

public class TextTranslationResult extends BaseResult {
    private List<String> data;

    public TextTranslationResult(int error, String errorMessage) {
        super(error, errorMessage);
        data = null;
    }

    public TextTranslationResult(List<String> data) {
        super(Translator.ERROR_NONE, null);
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    TextTranslationResult(TranslationArrayResult translationArrayResult) {
        this(translationArrayResult.errorCode.value, translationArrayResult.errorMessage);
        data = translationArrayResult.getData();
    }
}
