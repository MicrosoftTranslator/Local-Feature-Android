package com.microsoft.translator.local;

import com.microsoft.translator.service.app.TextLanguage;
import com.microsoft.translator.service.app.TextLanguageListResult;

import java.util.ArrayList;
import java.util.List;

public class LanguageListResult extends BaseResult {
    private String localeLangCode;
    private List<Language> languages;

    public String getLocaleLangCode() {
        return localeLangCode;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public LanguageListResult(int error, String errorMessage) {
        super(error, errorMessage);
    }

    public LanguageListResult(String localeLangCode, List<Language> languages) {
        super(Translator.ERROR_NONE, null);
        this.localeLangCode = localeLangCode;
        this.languages = languages;
    }

    LanguageListResult(TextLanguageListResult result) {
        super(result.errorCode.value, result.errorMessage);
        this.localeLangCode = result.localeLangCode;
        if (result.languages != null) {
            this.languages = new ArrayList<>(result.languages.size());
            for (TextLanguage textLanguage : result.languages) {
                this.languages.add(new Language(textLanguage));
            }
        }
    }

}
