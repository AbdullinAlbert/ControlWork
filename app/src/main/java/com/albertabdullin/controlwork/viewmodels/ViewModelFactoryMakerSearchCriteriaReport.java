package com.albertabdullin.controlwork.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class ViewModelFactoryMakerSearchCriteriaReport extends ViewModelFactoryMakerSearchCriteria {
    private final Application mApplication;

    public ViewModelFactoryMakerSearchCriteriaReport(Application application) {
        super(application);
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MakerSearchCriteriaReportVM(mApplication);
    }

}
