package com.microsoft.translator.local;

import android.content.Context;

import java.util.List;

public class Translator {

    public static final int ERROR_NONE = 0;

    public static final int CONNECTION_PENDING = 1;

    public static final int ERROR_APP_NOT_INSTALLED = 50;
    public static final int ERROR_APP_MISSING_SIGNATURE = 51;
    public static final int ERROR_APP_INVALID_SIGNATURE = 52;
    public static final int ERROR_APP_VERSION_NOT_SUPPORTED = 53;

    public static final int ERROR_BINDING_FAILURE = 71;

    public static final int ERROR_INVALID_KEY = 100;
    public static final int ERROR_INVALID_FROM_LANGUAGE = 101;
    public static final int ERROR_INVALID_TO_LANGUAGE = 102;

    public static final int ERROR_INVALID_DATA = 103;
    public static final int ERROR_USER_PRIVACY_AGREEMENT = 105;

    public static final int ERROR_LIMIT_REACHED = 106;

    public static final int ERROR_FROM_LANGUAGE_NOT_OFFLINE = 201;
    public static final int ERROR_TO_LANGUAGE_NOT_OFFLINE = 202;

    public static final int ERROR_OTHER = 500;

    public static final int ERROR_NOT_BOUND = 501;

    public static final int ERROR_NETWORK = 1000;

    public interface ServiceListener {
        void onConnected();

        void onDisconnected();

        void onDied();
    }

    public static int init(Context context) {
        return TranslatorImpl.getInstance().init(context);
    }

    public static int start(Context context, ServiceListener listener) {
        return TranslatorImpl.getInstance().start(context, listener);
    }

    public static boolean isConnected() {
        return TranslatorImpl.getInstance().isConnected();
    }

    public static void stop() {
        if (isConnected()) {
            TranslatorImpl.getInstance().stop();
        }
    }

    public static LanguageListResult getLanguageList() {
        return TranslatorImpl.getInstance().getTextLanguages();
    }

    public static TextTranslationResult translate(String apiKey, String category, String fromLanguageCode, String toLanguageCode, List<String> text) {
        return TranslatorImpl.getInstance().translate(apiKey, category, fromLanguageCode, toLanguageCode, text);
    }

    public static int initializeOfflineEngines(String fromLanguageCode, String toLanguageCode) {
        return TranslatorImpl.getInstance().initializeOfflineEngines(fromLanguageCode, toLanguageCode);
    }

    public static int getVersion() {
        return TranslatorImpl.getInstance().getVersion();
    }

}
