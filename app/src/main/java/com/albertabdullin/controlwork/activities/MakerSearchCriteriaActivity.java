package com.albertabdullin.controlwork.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryMakerSearchCriteria;

public class MakerSearchCriteriaActivity extends AppCompatActivity {
    private static MakerSearchCriteriaVM model;

    public static final int LIST_OF_ENTITIES_IS_READY = 0;
    public static final int SEARCH_IS_DONE = 1;

    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case LIST_OF_ENTITIES_IS_READY:
                    model.notifyAboutLoadedItems();
                    break;
                case SEARCH_IS_DONE:
                    model.updateSearchAdapterList();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_maker_search_criteria_activity);
        model = new ViewModelProvider(this, new ViewModelFactoryMakerSearchCriteria(this.getApplication())).get(MakerSearchCriteriaVM.class);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SearchCriteriaFragment searchCriteriaFragment = (SearchCriteriaFragment) getSupportFragmentManager().findFragmentByTag("maker_search_criteria_fragment");
        if (searchCriteriaFragment == null) {
            searchCriteriaFragment = new SearchCriteriaFragment();
            transaction.add(R.id.container_for_maker_search_criteria_fragment, searchCriteriaFragment, "maker_search_criteria_fragment");
        } else transaction.replace(R.id.container_for_maker_search_criteria_fragment, searchCriteriaFragment, "maker_search_criteria_fragment");
        transaction.commit();
    }

}
