package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactoryAddNewData implements ViewModelProvider.Factory {
    private Application mApplication;
    public ViewModelFactoryAddNewData(Application application) {
        mApplication = application;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddNewDataVM(mApplication) ;
    }
}
