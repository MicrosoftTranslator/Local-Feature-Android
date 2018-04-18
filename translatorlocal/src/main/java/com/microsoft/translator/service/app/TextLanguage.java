package com.microsoft.translator.service.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */

public class TextLanguage implements Parcelable {
    final public String code;
    final public String name;
    final public String nativeName;
    final public boolean isOnDevice;

    public TextLanguage(String code, String name, String nativeName, boolean isOnDevice) {
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
        this.isOnDevice = isOnDevice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeString(this.nativeName);
        dest.writeByte(this.isOnDevice ? (byte) 1 : (byte) 0);
    }

    protected TextLanguage(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.nativeName = in.readString();
        this.isOnDevice = in.readByte() != 0;
    }

    public static final Creator<TextLanguage> CREATOR = new Creator<TextLanguage>() {
        @Override
        public TextLanguage createFromParcel(Parcel source) {
            return new TextLanguage(source);
        }

        @Override
        public TextLanguage[] newArray(int size) {
            return new TextLanguage[size];
        }
    };
}
