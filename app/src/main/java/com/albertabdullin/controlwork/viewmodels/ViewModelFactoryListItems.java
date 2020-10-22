package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactoryListItems implements ViewModelProvider.Factory {
    private Application mApplication;

    public ViewModelFactoryListItems(Application application) {
        mApplication = application;
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ListOfItemsVM(mApplication);
    }
}