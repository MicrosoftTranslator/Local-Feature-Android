package com.microsoft.translator.sample.localexample;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.translator.local.Language;
import com.microsoft.translator.local.LanguageListResult;
import com.microsoft.translator.local.TextTranslationResult;
import com.microsoft.translator.local.Translator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private boolean isBound = false;

    private TextView logView;

    private EditText fromLanguage;
    private Spinner fromSpinner;

    private EditText toLanguage;
    private Spinner toSpinner;

    private EditText inputTextView;
    private EditText inputTextView2;
    private TextView outputTextView;
    private TextView outputTextView2;

    final String SEPARATOR = " / ";
    final String API_KEY = "";

    static final String TAG = MainActivity.class.getName();

    final String BIND_SERVICE_MESSAGE = "Service bound!\n";
    final String BIND_SERVICE_ERROR_MESSAGE = "Service not yet bound, bind first!\n";
    final String UNBIND_SERVICE_MESSAGE = "Service unbound!\n";
    final String UNBIND_SERVICE_ERROR_MESSAGE = "Service not bound!\n";

    final int MAX_LENGTH = 500;

    private ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logView = findViewById(R.id.log);

        fromLanguage = findViewById(R.id.from_language);
        toLanguage = findViewById(R.id.to_language);
        fromSpinner = findViewById(R.id.from_spinner);
        toSpinner = findViewById(R.id.to_spinner);

        inputTextView = findViewById(R.id.data);
        outputTextView = findViewById(R.id.result);
        inputTextView2 = findViewById(R.id.data2);
        outputTextView2 = findViewById(R.id.result2);

        // Gets a handle to the clipboard service.
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        this.setTitle("Example " + BuildConfig.VERSION_NAME);
        showSpinners(false);
    }

    public void bindClick(View view) {
        if (!isBound) {
            int result = Translator.start(this, new Translator.ServiceListener() {
                @Override
                public void onConnected() {
                    isBound = true;
                }

                @Override
                public void onDisconnected() {
                    Log.e(TAG, "LOCAL API SERVICE DISCONNECTED");
                    isBound = false;
                    logView.setText(UNBIND_SERVICE_MESSAGE);
                }

                @Override
                public void onDied() {
                    Log.e(TAG, "LOCAL API SERVICE DIED");
                    isBound = false;
                    logView.setText(UNBIND_SERVICE_MESSAGE);
                }
            });
            logView.append("Binding result = " + result + "\n" + Translator.getVersion() + "\n");

            logView.post(new Runnable() {
                @Override
                public void run() {
                    logView.append("API VERSION: " + Translator.getVersion() + "\n");
                }
            });
        } else {
            logView.append("Service already bound!\n");
        }
    }

    public void unbindClick(View view) {
        showSpinners(false);
        if (isBound) {
            Translator.stop();
            isBound = false;

            logView.setText(UNBIND_SERVICE_MESSAGE);
        } else {
            logView.append(UNBIND_SERVICE_ERROR_MESSAGE);
        }
    }

    private void showSpinners(boolean show) {
        fromSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        toSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void languageClick(View view) {
        if (!isBound) {
            logView.append(BIND_SERVICE_ERROR_MESSAGE);
            return;
        }

        new LanguageOperation().execute();
    }

    //                                         //params, progress, result
    private class LanguageOperation extends AsyncTask<String, String, LanguageListResult> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected LanguageListResult doInBackground(String... params) {
            final LanguageListResult languageList = getLanguageList();
            return languageList;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (progress != null) {
                final String msg = progress[0];
                logView.post(new Runnable() {
                    @Override
                    public void run() {
                        logView.append(msg);
                    }
                });
            }
        }

        // this should be run on a background thread
        private LanguageListResult getLanguageList() {
            long start = SystemClock.elapsedRealtime();
            LanguageListResult list = null;

            onProgressUpdate("\nAPI CALL: getLanguages()\n\n");
            list = Translator.getLanguageList();
            if (list == null) {
                onProgressUpdate("null languages list\n");
                return null;
            }
            if (!list.isError() && (list.getLanguages() != null)) {
                onProgressUpdate("Found " + list.getLanguages().size() + " languages for language code '" + list.getLocaleLangCode() + "'\n\n");
                for (Language lang : list.getLanguages()) {
                    onProgressUpdate(String.format("LANG: %10s / %20s / %20s\n", lang.code, lang.name, lang.nativeName));
                }
                long end = SystemClock.elapsedRealtime();
                onProgressUpdate("Took " + (end - start) + "ms");
                return list;
            } else {
                onProgressUpdate(String.format("List response: %s %s %s\n", list.errorCode, list.errorMessage, list.getLocaleLangCode()));
                onProgressUpdate("empty languages list\n");
                return null;
            }
        }


        @Override
        protected void onPostExecute(LanguageListResult languageList) {
            boolean isOnline = isConnected(getBaseContext());
            if (languageList == null) {
                showSpinners(false);
                return;
            }

            final ArrayList<String> stringList = new ArrayList<>();
            for (Language lang : languageList.getLanguages()) {
                if ((!isOnline && lang.isOnDevice) || isOnline) {
                    stringList.add(String.format("%2$s%1$s%3$s%1$s%4$s", SEPARATOR, lang.code, lang.name, lang.nativeName));
                }
            }
            stringList.add(0, "Choose(" + stringList.size() + ")");

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, stringList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            //set the ArrayAdapter to the spinners
            // for text translation from and to will have the same languages available.
            fromSpinner.setAdapter(dataAdapter);
            toSpinner.setAdapter(dataAdapter);

            fromSpinner.setOnItemSelectedListener(selectedListener);
            toSpinner.setOnItemSelectedListener(selectedListener);

            showSpinners(true);
        }

    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();

        Log.d(TAG, "isConnected: " + isConnected);

        return isConnected;
    }

    AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                return;
            }
            String item = (String) parent.getAdapter().getItem(position);
            String langCode = item.split(SEPARATOR)[0];
            if (parent.getId() == R.id.from_spinner) {
                fromLanguage.setText(langCode);
            } else if (parent.getId() == R.id.to_spinner) {
                toLanguage.setText(langCode);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (parent.getId() == R.id.from_spinner) {
                fromLanguage.setText("");
            } else if (parent.getId() == R.id.to_spinner) {
                toLanguage.setText("");
            }
        }
    };

    public void doTranslation(View view) {
        logView.setText("");
        if (TextUtils.isEmpty(inputTextView2.getText())) {
            new LongOperation().execute(API_KEY, fromLanguage.getText().toString(), toLanguage.getText().toString(), inputTextView.getText().toString());
        } else {
            new LongOperation().execute(API_KEY, fromLanguage.getText().toString(), toLanguage.getText().toString(), inputTextView.getText().toString(), inputTextView2.getText().toString());
        }

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void copyTranslation(View view) {
        String text = outputTextView.getText().toString();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "Nothing to Copy", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("translatedText", text);

        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied " + text.length() + " chars to clipboard", Toast.LENGTH_SHORT).show();
    }

    public void pasteText(View view) {
        String text = getClipboardText();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "Nothing to Paste", Toast.LENGTH_SHORT).show();
            return;
        }
        if (text.length() > MAX_LENGTH) {
            Toast.makeText(this, "Text cropped to " + MAX_LENGTH + " characters", Toast.LENGTH_SHORT).show();
        }
        inputTextView.setText(text);
    }

    private String resultString(TextTranslationResult result) {
        if (!result.isError()) {
            return String.format("RESPONSE: %s\n", result.getData());
        } else {
            return String.format("RESPONSE: %s -- %s\n", result.errorCode, result.errorMessage);
        }
    }

    public void warmUpClick(View view) {
        // hide keyboard
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (!isBound) {
            logView.append(BIND_SERVICE_ERROR_MESSAGE);
            return;
        }
        if (TextUtils.isEmpty(fromLanguage.getText()) || TextUtils.isEmpty(toLanguage.getText())) {
            outputTextView.setText("ERROR: Enter from and to languages!");
            return;
        }
        new WarmUpOperation().execute(fromLanguage.getText().toString(), toLanguage.getText().toString());
    }

    //                                         //params, progress, result
    private class WarmUpOperation extends AsyncTask<String, String, Integer> {

        String fromCode;
        String toCode;

        private long startAsync;
        private long endAsync;

        @Override
        protected void onPreExecute() {
            startAsync = SystemClock.elapsedRealtime();
            outputTextView.setText("\n\n");
        }

        @Override
        protected Integer doInBackground(String... params) {
            fromCode = params[0];
            toCode = params[1];
            int result = Translator.initializeOfflineEngines(fromCode, toCode);
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            endAsync = SystemClock.elapsedRealtime();
            if (result == null || result != Translator.ERROR_NONE) {
                outputTextView.setText("ERROR: Initialize engines failed!");
                logView.append(String.format("initialize engines '%s' -> '%s' failed\n", fromCode, toCode, (endAsync - startAsync)));
            } else {
                outputTextView.setText(" Engines Initialized");
                logView.append(String.format("initialize engines '%s' -> '%s' took %dms\n\n", fromCode, toCode, (endAsync - startAsync)));
            }
        }
    }

    //                                         //params, progress, result
    private class LongOperation extends AsyncTask<String, String, TextTranslationResult> {

        private long startAsync;
        private long endAsync;

        @Override
        protected void onPreExecute() {
            startAsync = SystemClock.elapsedRealtime();
            outputTextView.setText("\n\n");
        }

        @Override
        protected TextTranslationResult doInBackground(String... params) {
            if (!isBound) {
                publishProgress(BIND_SERVICE_ERROR_MESSAGE);
                return null;
            }

            TextTranslationResult result;
            final String apiKey = params[0];
            final String fromLanguageCode = params[1];
            final String toLanguageCode = params[2];
            final String text = params[3];
            ArrayList<String> textList = new ArrayList<>(2);
            for (int i = 3; i < params.length; i++) {
                textList.add(params[i]);
            }

            publishProgress(String.format("\nAPI CALL: translateTextArray(\"%s\", \"\", \"%s\", \"%s\", %s)\n\n",
                    apiKey,
                    fromLanguageCode,
                    toLanguageCode,
                    arrayToString(textList)));

            // the call to the translation api.
            // needs to be done on a background thread as it may take some time.
            result = Translator.translate(apiKey, "", fromLanguageCode, toLanguageCode, textList);

            publishProgress(resultString(result));
            return result;
        }

        @Override
        protected void onPostExecute(TextTranslationResult result) {
            endAsync = SystemClock.elapsedRealtime();
            logView.append("aSyncTask took " + (endAsync - startAsync) + "ms\n\n");
            if (result == null) {
                outputTextView.setText("ERROR: null result, see log");
            } else if (!result.isError()) {
                outputTextView.setText(result.getData().get(0));
                if (result.getData().size() > 1) {
                    outputTextView2.setText(result.getData().get(1));
                    outputTextView2.setVisibility(View.VISIBLE);
                } else {
                    outputTextView2.setText("");
                    outputTextView2.setVisibility(View.GONE);
                }
            } else {
                outputTextView.setText(String.format("ERROR: %s -- %s\n", result.errorCode, result.errorMessage));
            }
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (progress != null) {
                logView.append(progress[0]);
            }
        }

        String arrayToString(ArrayList<String> list) {
            if (list == null || list.size() == 0) {
                return "[]";
            }
            if (list.size() == 1) {
                return "[\"" + list.get(0) + "\"]";
            }

            StringBuilder builder = new StringBuilder("[");
            for (int idx = 0; idx <= list.size() - 2; idx++) {
                builder.append("\"");
                builder.append(list.get(idx));
                builder.append("\", ");
            }
            builder.append("\"");
            builder.append(list.get(list.size() - 1));
            builder.append("\"]");
            return builder.toString();
        }
    }

    String getClipboardText() {
        // Examines the item on the clipboard. If getText() does not return null, the clip item contains the
        // text. Assumes that this application can only handle one item at a time.
        if (clipboard == null || clipboard.getPrimaryClip() == null) {
            return null;
        }
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        if (item == null) {
            return null;
        }

        // Gets the clipboard as text.
        CharSequence pasteData = item.getText();

        // If the string contains data, then the paste operation is done
        if (pasteData != null) {
            return pasteData.toString();
        } else {
            // The clipboard does not contain text. If it contains a URI, attempts to get data from it
            Uri pasteUri = item.getUri();

            // If the URI contains something, try to get text from it
            if (pasteUri != null) {
                return pasteUri.toString();
            } else {
                // Something is wrong. The MIME type was plain text, but the clipboard does not contain either
                // text or a Uri. Report an error.
                return null;
            }
        }
    }
}
