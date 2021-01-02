package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactoryMakerSearchCriteria implements ViewModelProvider.Factory {

    private Application mApplication;

    public ViewModelFactoryMakerSearchCriteria(Application application) {
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MakerSearchCriteriaVM(mApplication);
    }
}
