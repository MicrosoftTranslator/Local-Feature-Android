package com.microsoft.translator.local;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.microsoft.translator.service.app.ITranslatorApi;

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class TranslatorImpl {

    private ITranslatorApi iTranslatorApi;

    private CountDownLatch lock;

    private String packageName = "com.microsoft.translator";
    private String appSignature = "b885432e184cf98a27087eab6093879dc3c97151f3daa83a19e5c9f8d3ea8961";

    static final String serviceName = "com.microsoft.translator.service.app.TranslationService";

    String TAG = TranslatorImpl.class.getSimpleName();

    private WeakReference<Context> appContext;
    private ServiceConnection serviceConnection;

    private TranslatorImpl() {
    }

    private Translator.ServiceListener listener = null;

    private boolean isConnected = false;

    public boolean isConnected() {
        return isConnected;
    }

    private static class TranslatorImplHelper {
        private static final TranslatorImpl INSTANCE = new TranslatorImpl();
    }

    static TranslatorImpl getInstance() {
        return TranslatorImplHelper.INSTANCE;
    }

    int init(Context context) {
        this.appContext = new WeakReference<>(context.getApplicationContext());
        return checkAppSignature(appContext.get(), packageName, appSignature);
    }

    int start(Context context, Translator.ServiceListener listener) {
        if (serviceConnection == null) {
            serviceConnection = new ServiceConnection();
        }

        this.appContext = new WeakReference<>(context.getApplicationContext());

        if (iTranslatorApi == null) {
            int initResult = init(context);

            if (initResult != Translator.ERROR_NONE) {
                return initResult;
            }

            lock = new CountDownLatch(1);

            this.listener = listener;

            Intent serviceIntent = new Intent()
                    .setComponent(new ComponentName(
                            packageName,
                            serviceName));

            // this may return false if the binding fails, so check the return value.
            // it may happen if the Translator app has never been launched, or has been force stopped.
            if (appContext.get() == null) {
                return Translator.ERROR_OTHER;
            }
            boolean bindResult = appContext.get().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

            if (bindResult) {
                try {
                    boolean success = lock.await(10, TimeUnit.MILLISECONDS);
                    if (!success) {
                        return Translator.CONNECTION_PENDING;
                    }
                    if (iTranslatorApi.getVersion() >= 2) {
                        return Translator.ERROR_NONE;
                    } else {
                        return Translator.ERROR_APP_VERSION_NOT_SUPPORTED;
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return Translator.CONNECTION_PENDING;
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    return Translator.ERROR_BINDING_FAILURE;
                }
            } else {
                return Translator.ERROR_BINDING_FAILURE;
            }
        } else {
            try {
                if (iTranslatorApi.getVersion() == 2) {
                    return Translator.ERROR_NONE;
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }
        return Translator.ERROR_OTHER;
    }

    void stop() {
        listener = null;
        if (appContext != null) {
            if (serviceConnection != null) {
                if (appContext.get() != null) {
                    appContext.get().unbindService(serviceConnection);
                }
                serviceConnection = null;
                iTranslatorApi = null;
            }
        }
        isConnected = false;
    }

    int getVersion() {
        if (appContext == null) {
            return -1;
        }
        if (iTranslatorApi == null) {
            return -2;
        }
        try {
            return iTranslatorApi.getVersion();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    LanguageListResult getTextLanguages() {
        if (iTranslatorApi == null) {
            return new LanguageListResult(Translator.ERROR_NOT_BOUND, "not bound!");
        }
        try {
            return new LanguageListResult(iTranslatorApi.getTextLanguages());
        } catch (RemoteException ex) {
            return new LanguageListResult(Translator.ERROR_OTHER, ex.getMessage());
        }
    }

    TextTranslationResult translate(String key, String category, String fromCode, String toCode, List<String> texts) {
        if (iTranslatorApi == null) {
            return new TextTranslationResult(Translator.ERROR_NOT_BOUND, "not bound!");
        }
        try {
            return new TextTranslationResult(iTranslatorApi.translateTextArray(key, category, fromCode, toCode, texts));
        } catch (RemoteException ex) {
            return new TextTranslationResult(Translator.ERROR_OTHER, ex.getMessage());
        }
    }

    int initializeOfflineEngines(String fromLanguageCode, String toLanguageCode) {
        if (iTranslatorApi == null) {
            return Translator.ERROR_NOT_BOUND;
        }
        boolean result = false;
        try {
            result = iTranslatorApi.initializeOfflineEngines(fromLanguageCode, toLanguageCode);
            if (result) {
                return Translator.ERROR_NONE;
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return Translator.ERROR_OTHER;
    }

    private class ServiceConnection implements android.content.ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            iTranslatorApi = ITranslatorApi.Stub.asInterface(service);
            lock.countDown();
            isConnected = true;
            if (listener != null) {
                listener.onConnected();
            }
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.v(TAG, "onBindingDied");
            isConnected = false;
            iTranslatorApi = null;
            if (listener != null) {
                listener.onDied();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.v(TAG, "onServiceDisconnected");
            isConnected = false;
            iTranslatorApi = null;
            if (listener != null) {
                listener.onDisconnected();
            }

            // This method is only invoked when the service quits from the other end or gets killed
            // Invoking exit() from the AIDL interface makes the Service kill itself, thus invoking this.
        }
    }

    private static int checkAppSignature(Context context, String packageName, String sha256Sum) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException ex) {
            return Translator.ERROR_APP_NOT_INSTALLED;
        }

        if (packageInfo == null || packageInfo.signatures == null || packageInfo.signatures.length != 1) {
            return Translator.ERROR_APP_MISSING_SIGNATURE;
        }

        byte[] signatureBytes = packageInfo.signatures[0].toByteArray();
        String sha = sha256Sum(signatureBytes);
        if (sha == null) {
            return Translator.ERROR_APP_MISSING_SIGNATURE;
        }
        if (sha.equalsIgnoreCase(sha256Sum)) {
            return checkAppVersion(packageInfo);
        }
        return Translator.ERROR_APP_INVALID_SIGNATURE;
    }

    private static int checkAppVersion(PackageInfo packageInfo) {
        final int versionCode = packageInfo.versionCode;
        final String versionName = packageInfo.versionName;
        if (versionCode < 255) {
            return Translator.ERROR_APP_VERSION_NOT_SUPPORTED;
        }
        if ((versionCode == 255) && (versionName.endsWith("3fc6f1a21"))) {
            return Translator.ERROR_APP_VERSION_NOT_SUPPORTED;
        }

        // ipu versions without translation service
        if ((versionCode >= 260) && (versionCode < 274)) {
            return Translator.ERROR_APP_VERSION_NOT_SUPPORTED;
        }
        if (versionCode == 256 && versionName.endsWith("256")) {
            return Translator.ERROR_APP_VERSION_NOT_SUPPORTED;
        }
        if (versionCode == 257 && versionName.endsWith("257")) {
            return Translator.ERROR_APP_VERSION_NOT_SUPPORTED;
        }
        if (versionCode == 259 && versionName.endsWith("259")) {
            return Translator.ERROR_APP_VERSION_NOT_SUPPORTED;
        }
        return Translator.ERROR_NONE;
    }

    static String sha256Sum(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);
            return bytesToHex(md.digest());
        } catch (Exception ex) {
            //
        }
        return null;
    }

    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
