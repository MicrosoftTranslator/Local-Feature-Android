package com.microsoft.translator.service.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 *
 */

public class TextLanguageListResult implements Parcelable {

    public TranslationError errorCode;
    public String errorMessage;
    public String localeLangCode;
    public List<TextLanguage> languages;

    public boolean isSuccess() {
        return errorCode == TranslationError.ERROR_NONE;
    }

    public TextLanguageListResult(TranslationError errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.languages = null;
    }

    public TextLanguageListResult(List<TextLanguage> languages) {
        this.errorCode = TranslationError.ERROR_NONE;
        this.errorMessage = null;
        this.languages = languages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.errorCode == null ? -1 : this.errorCode.ordinal());
        dest.writeString(this.errorMessage);
        dest.writeString(this.localeLangCode);
        dest.writeTypedList(this.languages);
    }

    protected TextLanguageListResult(Parcel in) {
        int tmpErrorCode = in.readInt();
        this.errorCode = tmpErrorCode == -1 ? null : TranslationError.values()[tmpErrorCode];
        this.errorMessage = in.readString();
        this.localeLangCode = in.readString();
        this.languages = in.createTypedArrayList(TextLanguage.CREATOR);
    }

    public static final Creator<TextLanguageListResult> CREATOR = new Creator<TextLanguageListResult>() {
        @Override
        public TextLanguageListResult createFromParcel(Parcel source) {
            return new TextLanguageListResult(source);
        }

        @Override
        public TextLanguageListResult[] newArray(int size) {
            return new TextLanguageListResult[size];
        }
    };
}
