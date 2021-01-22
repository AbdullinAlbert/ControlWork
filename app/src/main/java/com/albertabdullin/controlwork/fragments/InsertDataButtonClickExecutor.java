package com.albertabdullin.controlwork.fragments;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

import com.albertabdullin.controlwork.activities.ProviderOfHolderFragmentState;

public abstract class InsertDataButtonClickExecutor implements Parcelable {

    public abstract void executeYesButtonClick(AppCompatActivity activity, String text);

    public abstract void executeNoButtonClick();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
