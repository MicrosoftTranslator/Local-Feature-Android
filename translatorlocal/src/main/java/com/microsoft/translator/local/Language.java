package com.microsoft.translator.local;

import com.microsoft.translator.service.app.TextLanguage;

public class Language {
    public String code;
    public String name;
    public String nativeName;
    public boolean isOnDevice;

    Language(TextLanguage textLanguage) {
        this.code = textLanguage.code;
        this.name = textLanguage.name;
        this.nativeName = textLanguage.nativeName;
        isOnDevice = textLanguage.isOnDevice;
    }
}
