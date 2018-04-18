# Local feature for the Translator app on Android

## Get Started

How to get started with the Microsoft Translator local feature:

1. Sign up for a free subscription to the Microsoft Translator text API. [Learn more](https://docs.microsoft.com/en-us/azure/cognitive-services/translator/translator-text-how-to-signup)
2. Use the documentation below and the sample app to learn how to add online and offline translation to your Android app.
3. Start coding!

## Local Feature

**Translator Local Feature** works on the most recent version of the [Microsoft Translator Android App](https://play.google.com/store/apps/details?id=com.microsoft.translator).  It allows developers to add translation capabilities to their apps, utilizing the Translator app already installed on a user device.  If the user has language packs downloaded, the translations will also be done offline.

## API Usage

The Local feature API is still very simple -- it currently only allows for text translation. 

There are only a few methods:

- Start and stop the service
- Get a list of supported Languages
- Perform a text translation from language A to language B
- Initialize offline engines for better performance

The `translatorapi` library included in this repository contains the necessary classes to use the Local API service. There is also a sample app in the `app` folder.

`com.microsoft.translator.localapi.TranslatorApi` is the main class for interfacing with the API.

1. Sign up for your API key at the link above in the **Get Started** section

2. Start the service by calling `start()` and check for TranslatorApi.ERROR_NONE.  If another value is returned, the Translator app may not have been installed on the user device, or may not support the Local API.

3. Use `getLanguageList()` to return a list of language codes to translate to and from. These will be passed as arguments to the `translate` method.  These are language codes like `en` for English, and `es` for Spanish.  If the user has the offline language pack downloaded for a certain language, the `isOnDevice` flag will be `true`.

4. Perform a translation with the `translate()` method. providing your API key, a category (leave blank for now), the language codes for the to and from languages, and a list of Strings to translate.

The `getLanguageList` and `translate` methods are synchronous and may take some time to perform, so they should be done on a background thread.


Here is a simple call flow to perform a translation:

```
TranslatorApi translatorApi = TranslatorApi.getInstance();
int result = translatorApi.start(context, null);

if (result == TranslatorApi.ERROR_NONE) {
    LanguageListResult languageResult = translatorApi.getLanguageList();
    if (!languageResult.isError()) {
        ArrayList<String> texts = new ArrayList<>();
        texts.add("Hello World!");
        TextTranslationResult textResult = translatorApi.translate("BAD_KEY", null, "en", languageResult.getLanguages().get(0).code, texts);
        if (!textResult.isError()) {
            System.out.println("Got result: " + textResult.getData().get(0));
        }
    }
}

//stop the service when you are finished using it.
translatorApi.stop();
```

## Sample App

There is a sample app provided in the `app` folder of this repository to play around with the API.

You will need to provide your own API key at `final String API_KEY = "";` in `MainActivity.java`

