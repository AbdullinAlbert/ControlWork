package com.albertabdullin.controlwork.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryEditDeleteData;

public class EditDeleteDataActivity extends AppCompatActivity {
    private static EditDeleteDataVM model;

    public static final int LIST_OF_EMPLOYEES_IS_READY = 0;

    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case LIST_OF_EMPLOYEES_IS_READY:
                    model.notifyAboutLoadedItems();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_delete_data);
        model = new ViewModelProvider(this, new ViewModelFactoryEditDeleteData(this.getApplication())).get(EditDeleteDataVM.class);
        SearchCriteriaFragment searchCriteriaFragment = new SearchCriteriaFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_for_edit_delete_fragment, searchCriteriaFragment);
        transaction.commit();
    }

}
