package com.microsoft.translator.local;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Before
    public void setup() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        int result = Translator.init(appContext);
        assertEquals("ERROR Initializing", Translator.ERROR_NONE, result);

        result = Translator.start(appContext, null);
        assertEquals("ERROR BINDING", Translator.ERROR_NONE, result);
    }

    @After
    public void tearDown() {
        try {
            Translator.stop();
        } catch (Exception ex) {
            ;
        }
    }


    @Test
    public void testBinding() throws Exception {
        Translator.stop();
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.microsoft.translator.local.test", appContext.getPackageName());

        assertEquals("ERROR BINDING", Translator.ERROR_NONE, Translator.start(appContext, null));
    }

    @Test
    public void testVersion() throws Exception {

        assertEquals("API VERSION ", 2, Translator.getVersion());
    }

    @Test
    public void testLanguages() throws Exception {

        LanguageListResult result = Translator.getLanguageList();

        assertEquals("Error getting language list: " + result.errorMessage, Translator.ERROR_NONE, result.errorCode);
        assertFalse(result.isError());
    }

    @Test
    public void testTranslate() throws Exception {

        ArrayList<String> texts = new ArrayList<>();
        texts.add("hello");
        TextTranslationResult result = Translator.translate("blah", null, "en", "es", texts);

        assertEquals("Error Translating " + result.errorMessage, Translator.ERROR_NONE, result.errorCode);
        assertFalse(result.isError());

        assertEquals("Unexpected Translation ", "Hola", result.getData().get(0));
    }

    @Test
    public void testBadKey() throws Exception {

        ArrayList<String> texts = new ArrayList<>();
        texts.add("hello");
        TextTranslationResult result = Translator.translate("BAD_KEY", null, "en", "es", texts);

        assertTrue(result.isError());
        assertEquals("Error Translating: " + result.errorMessage, Translator.ERROR_INVALID_KEY, result.errorCode);
    }
}
