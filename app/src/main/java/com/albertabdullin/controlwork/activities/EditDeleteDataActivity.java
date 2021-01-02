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
import com.albertabdullin.controlwork.fragments.DeleteDataFragment;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryEditDeleteData;

public class EditDeleteDataActivity extends AppCompatActivity {

    private static EditDeleteDataVM viewModel;

    public static final int LOAD_ITEMS_FROM_DB = 0;

    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case LOAD_ITEMS_FROM_DB:
                    viewModel.notifyAboutLoadItems();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_data);
        viewModel = new ViewModelProvider(this, new ViewModelFactoryEditDeleteData(this.getApplication())).get(EditDeleteDataVM.class);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        DeleteDataFragment deleteDataFragment = (DeleteDataFragment) getSupportFragmentManager().findFragmentByTag("delete_result_of_search_criteria_fragment");
        if (deleteDataFragment == null) {
            deleteDataFragment = new DeleteDataFragment();
            transaction.add(R.id.container_for_edit_delete_data_fragment, deleteDataFragment, "delete_result_of_search_criteria_fragment");
        } else transaction.replace(R.id.container_for_edit_delete_data_fragment, deleteDataFragment, "delete_result_of_search_criteria_fragment");
        transaction.commit();
    }
}
