# Local feature for the Translator app on Android [Preview]

## Local Feature

**Translator Local Feature** works on the most recent version of the [Microsoft Translator Android App](https://translator.microsoft.com/apps/android/).  It allows developers to add translation capabilities to their apps utilizing the Translator app already installed on a user device.  If the user has language packs downloaded, the translations may also be done offline.

Note: The Local feature is in Preview mode. Please do not use it for any production purposes.
## Get Started

How to get started with the Microsoft Translator local feature:

1. Sign up for a subscription to the Microsoft Translator text API. Select the free 2 million character per month tier, or paid tiers for higher volumes. [Learn more](https://docs.microsoft.com/en-us/azure/cognitive-services/translator/translator-text-how-to-signup). 
2. Use the documentation below and the sample app to learn how to add online and offline translation to your Android app.
3. Start coding!
4. Question? Post them to our [support forum](https://stackoverflow.com/questions/tagged/microsoft-translator)

## Release notes

| Feature    | Status |
|:----------- |:-------------:|
|Online usage charged through Azure portal| In development|
|Offline usage charged through Azure portal| In development|
|Customization with Category ID| In development|

**April 18, 2017:** Documentation and sample apps added 

## Usage

The following methods are available with the local feature:

- Start and stop the service
- Get a list of supported Languages
- Perform a text translation from language A to language B
- Initialize offline engines (for better performance when offline translations are used more than once)

The `translatorlocal` library included in this repository contains the necessary classes to use the Local Feature. There is also a sample app in the `app` folder.

`com.microsoft.translator.local.Translator` is the main class for interfacing with the feature.

1. Sign up for your API key at the link above in the **Get Started** section

2. Check that the Translator app is installed by calling `init()` it should return `Translator.ERROR_NONE` if a version of the app that supports the feature is installed.

3. Start the service by calling `start()` and check for `Translator.ERROR_NONE`.  If another value is returned, the Translator app may not have been installed on the user device, or may not support the Local Feature.

4. Check that the service is connected with `isConnected()`

5. Use `getLanguageList` to get a list of languages to translate from and to. The `code` field corresponds to the language codes you will need for `translate`. These are language codes like `en` for English, and `es` for Spanish.  If the user has the offline language pack downloaded for a certain language, the `isOnDevice` flag will be `true`. You can use the `nativeName` to get the language in its native text, or `name` to get the name in the devices current locale. ( A cached list of languages will be used if the user is offline. )

6. Perform a translation with the `translate` method. providing your API key, a category (leave blank for now), the language codes for the from and to languages, and a list of Strings to translate.

The `getLanguageList` and `translate` methods are synchronous and may take some time to perform, so they should be done on a background thread.


Here is a simple call flow to perform a translation:

```
// check if Translator is available

int result = Translator.init(context);
if (result != Translator.ERROR_NONE) {
    // Translator isn't installed or version is unsupported
    return;
}

// set your api key
String API_KEY = "";

// start the service
Translator.start(context, null);

// you may need to delay before isConnected returns true

if (Translator.isConnected()) {
    // get the list of available languages
    LanguageListResult languageResult = Translator.getLanguageList();

    if (!languageResult.isError()) {
        ArrayList<String> texts = new ArrayList<>();
        texts.add("Hello World!");
        
        // perform a translation
        TextTranslationResult textResult = Translator.translate(API_KEY, null, "en", languageResult.getLanguages().get(0).code, texts);
    
        if (!textResult.isError()) {
            System.out.println("Got result: " + textResult.getData().get(0));
        } else {
            System.out.println("Got error: " + textResult.errorMessage);
        }
    }
}

//stop the service when you are finished using it.
Translator.stop();
```

## FAQ

**What languages does the local feature support?**

For online translation, the local feature supports all Microsoft Translator text translation languages. For offline translation, the local feature will support any language that has available downloadable language packs for Android. The user will need to download the language pack prior to translation. [View language lists](https://translator.microsoft.com/help/articles/languages/)

For a complete list of Microsoft Translator language codes, refer to the [Translator Text API documentation](https://docs.microsoft.com/en-us/azure/cognitive-services/translator/languages).

**Does the local feature use neural machine translation (NMT) or statistical machine translation (SMT?**

The local feature uses NMT as a default. If NMT is not available for a particular language, the feature will use the SMT system. View supported NMT languages in the [Translator Text API documentation](https://docs.microsoft.com/en-us/azure/cognitive-services/translator/languages).

## Sample App

There is a sample app provided in the `app` folder of this repository to play around with the API.

You will need to provide your own API key at `final String API_KEY = "";` in `MainActivity.java`

