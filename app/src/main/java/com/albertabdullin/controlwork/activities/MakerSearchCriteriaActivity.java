package com.albertabdullin.controlwork.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.viewmodels.DialogFragmentStateHolder;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryMakerSearchCriteria;

public class MakerSearchCriteriaActivity extends AppCompatActivity implements ProviderOfHolderFragmentState{
    private static MakerSearchCriteriaVM model;

    private final Observer<String> exceptionObserver = new Observer<String>() {
        @Override
        public void onChanged(String string) {
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_maker_search_criteria_activity);
        model = new ViewModelProvider(this, new ViewModelFactoryMakerSearchCriteria(this.getApplication())).get(MakerSearchCriteriaVM.class);
        if (model.getExceptionFromBackgroundThreadsLD().hasObservers()) model.getExceptionFromBackgroundThreadsLD().removeObservers(this);
        model.getExceptionFromBackgroundThreadsLD().observe(this, exceptionObserver);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SearchCriteriaFragment searchCriteriaFragment = (SearchCriteriaFragment) getSupportFragmentManager().findFragmentByTag("maker_search_criteria_fragment");
        if (searchCriteriaFragment == null) {
            searchCriteriaFragment = new SearchCriteriaFragment();
            transaction.add(R.id.container_for_maker_search_criteria_fragment, searchCriteriaFragment, "maker_search_criteria_fragment");
        } else transaction.replace(R.id.container_for_maker_search_criteria_fragment, searchCriteriaFragment, "maker_search_criteria_fragment");
        transaction.commit();
    }

    @Override
    public DialogFragmentStateHolder getHolder() {
        return model;
    }
}
