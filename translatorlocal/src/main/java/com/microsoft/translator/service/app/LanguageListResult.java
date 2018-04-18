package com.microsoft.translator.service.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 *
 */

public class LanguageListResult implements Parcelable {

    public TranslationError errorCode;
    public String errorMessage;
    public String localeLangCode;
    public List<Language> languages;

    public boolean isSuccess() {
        return errorCode == TranslationError.ERROR_NONE;
    }

    LanguageListResult(TranslationError errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.languages = null;
    }

    LanguageListResult(List<Language> languages) {
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

    protected LanguageListResult(Parcel in) {
        int tmpErrorCode = in.readInt();
        this.errorCode = tmpErrorCode == -1 ? null : TranslationError.values()[tmpErrorCode];
        this.errorMessage = in.readString();
        this.localeLangCode = in.readString();
        this.languages = in.createTypedArrayList(Language.CREATOR);
    }

    public static final Creator<LanguageListResult> CREATOR = new Creator<LanguageListResult>() {
        @Override
        public LanguageListResult createFromParcel(Parcel source) {
            return new LanguageListResult(source);
        }

        @Override
        public LanguageListResult[] newArray(int size) {
            return new LanguageListResult[size];
        }
    };
}
