package com.microsoft.translator.service.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */

public class TranslationResult implements Parcelable {

    public TranslationError errorCode;
    public String errorMessage;
    public String data;

    public boolean isSuccess() {
        return errorCode == TranslationError.ERROR_NONE;
    }

    public TranslationError getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getData() {
        return data;
    }

    TranslationResult(TranslationError errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = null;
    }

    TranslationResult(String data) {
        this.errorCode = TranslationError.ERROR_NONE;
        this.errorMessage = null;
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.errorCode == null ? -1 : this.errorCode.ordinal());
        dest.writeString(this.errorMessage);
        dest.writeString(this.data);
    }

    protected TranslationResult(Parcel in) {
        int tmpErrorCode = in.readInt();
        this.errorCode = tmpErrorCode == -1 ? null : TranslationError.values()[tmpErrorCode];
        this.errorMessage = in.readString();
        this.data = in.readString();
    }

    public static final Creator<TranslationResult> CREATOR = new Creator<TranslationResult>() {
        @Override
        public TranslationResult createFromParcel(Parcel source) {
            return new TranslationResult(source);
        }

        @Override
        public TranslationResult[] newArray(int size) {
            return new TranslationResult[size];
        }
    };
}
