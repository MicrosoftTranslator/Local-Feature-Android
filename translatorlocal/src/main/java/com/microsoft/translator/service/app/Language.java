package com.microsoft.translator.service.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */

public class Language implements Parcelable {
    public String code;
    public String name;
    public String nativeName;

    public Language(String code, String name, String nativeName) {
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
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
    }

    protected Language(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.nativeName = in.readString();
    }

    public static final Creator<Language> CREATOR = new Creator<Language>() {
        @Override
        public Language createFromParcel(Parcel source) {
            return new Language(source);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
        }
    };
}
