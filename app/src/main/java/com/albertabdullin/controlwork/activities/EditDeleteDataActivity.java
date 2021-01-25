package com.albertabdullin.controlwork.activities;

import android.content.Context;
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
import com.albertabdullin.controlwork.fragments.EditDataFragment;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.viewmodels.DialogFragmentStateHolder;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryEditDeleteData;

public class EditDeleteDataActivity extends AppCompatActivity implements ProviderOfHolderFragmentState {
    private EditDeleteDataVM viewModel;
    private static String failDelete;
    private static String failLoad;

    public static final int FAIL_ABOUT_LOAD_DATA_FROM_DB = 0;
    public static final int FAIL_ABOUT_DELETE_DATA_FROM_DB = 1;

    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FAIL_ABOUT_LOAD_DATA_FROM_DB:
                    throw new RuntimeException(failLoad);
                case FAIL_ABOUT_DELETE_DATA_FROM_DB:
                    throw new RuntimeException(failDelete);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_data);
        viewModel = new ViewModelProvider(this, new ViewModelFactoryEditDeleteData(this.getApplication())).get(EditDeleteDataVM.class);
        failDelete = getResources().getString(R.string.fail_attempt_about_delete_data_from_db);
        failLoad = getResources().getString(R.string.fail_attempt_about_load_data_from_db);
        DeleteDataFragment deleteDataFragment = (DeleteDataFragment)
                getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.tag_for_delete_data_fragment));
        EditDataFragment editDataFragment = (EditDataFragment)
                getSupportFragmentManager().findFragmentByTag(getResources().getString(R.string.tag_for_edit_data_fragment));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (editDataFragment != null) {
            transaction.replace(R.id.container_for_edit_delete_data_fragment, editDataFragment,
                    getResources().getString(R.string.tag_for_edit_data_fragment));
            transaction.commit();
            return;
        }
        if (deleteDataFragment != null) {
            transaction.replace(R.id.container_for_edit_delete_data_fragment, deleteDataFragment,
                    getResources().getString(R.string.tag_for_delete_data_fragment));
        } else {
            deleteDataFragment = new DeleteDataFragment();
            transaction.add(R.id.container_for_edit_delete_data_fragment, deleteDataFragment,
                    getResources().getString(R.string.tag_for_delete_data_fragment));
        }
        transaction.commit();
    }

    @Override
    public DialogFragmentStateHolder getHolder() {
        return viewModel;
    }

}
