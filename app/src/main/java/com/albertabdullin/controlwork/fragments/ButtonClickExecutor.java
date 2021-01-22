package com.albertabdullin.controlwork.fragments;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;

public abstract class ButtonClickExecutor implements Parcelable {

    public abstract void executeYesButtonClick(AppCompatActivity appCompatActivity);
    public abstract void executeNoButtonClick(AppCompatActivity appCompatActivity);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
