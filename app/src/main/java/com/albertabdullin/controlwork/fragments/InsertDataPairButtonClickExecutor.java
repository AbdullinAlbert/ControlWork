package com.albertabdullin.controlwork.fragments;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class InsertDataPairButtonClickExecutor implements Parcelable {

    public abstract void executeYesButtonClick(float firstNumber, float secondNumber);

    public abstract void executeNoButtonClick();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

}
