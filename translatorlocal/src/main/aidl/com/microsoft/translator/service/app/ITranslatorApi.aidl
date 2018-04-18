// ITranslatorApi.aidl
package com.microsoft.translator.service.app;

import com.microsoft.translator.service.app.LanguageListResult;
import com.microsoft.translator.service.app.TextLanguageListResult;
import com.microsoft.translator.service.app.TranslationArrayResult;
import com.microsoft.translator.service.app.TranslationResult;

interface ITranslatorApi {

    // synchronous call to get lists of supported from/to languages for text translation, in the current locale
	// will return offline languages if offline, online otherwise
    LanguageListResult getLanguages();

    TextLanguageListResult getTextLanguages();

    // synchronous call to translate text either online or offline depending upon the network status. (utf-8)
    // language codes are key values like 'en' or 'zh-Hans' from the requests above, text is UTF-8 encoded, max 500 characters
    TranslationResult translateText(in String apiKey, in String fromLanguageCode, in String toLanguageCode, in String text);

    TranslationArrayResult translateTextArray(in String apiKey, in String category, in String fromLanguageCode, in String toLanguageCode, in List<String> textList);

    boolean initializeOfflineEngines(in String fromLanguageCode, in String toLanguageCode);
    
    int getVersion();
}
