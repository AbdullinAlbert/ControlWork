package com.albertabdullin.controlwork;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;

    public ViewModelFactory(Application application) {
        mApplication = application;
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ListOfEmplVM(mApplication);
    }
}
