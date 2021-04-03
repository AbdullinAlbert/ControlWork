package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactoryForReport implements ViewModelProvider.Factory {

    private final Application mAplication;

    public ViewModelFactoryForReport(Application application) {
        mAplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ReportViewModel(mAplication);
    }
}
