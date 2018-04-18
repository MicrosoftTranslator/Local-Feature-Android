package com.microsoft.translator.service.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 *
 */

public class TranslationArrayResult implements Parcelable {

    public TranslationError errorCode;
    public String errorMessage;
    public List<String> data;

    public boolean isSuccess() {
        return errorCode == TranslationError.ERROR_NONE;
    }

    public TranslationError getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<String> getData() {
        return data;
    }

    public TranslationArrayResult(TranslationError errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = null;
    }

    public TranslationArrayResult(List<String> data) {
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
        dest.writeStringList(this.data);
    }

    protected TranslationArrayResult(Parcel in) {
        int tmpErrorCode = in.readInt();
        this.errorCode = tmpErrorCode == -1 ? null : TranslationError.values()[tmpErrorCode];
        this.errorMessage = in.readString();
        this.data = in.createStringArrayList();
    }

    public static final Creator<TranslationArrayResult> CREATOR = new Creator<TranslationArrayResult>() {
        @Override
        public TranslationArrayResult createFromParcel(Parcel source) {
            return new TranslationArrayResult(source);
        }

        @Override
        public TranslationArrayResult[] newArray(int size) {
            return new TranslationArrayResult[size];
        }
    };
}
