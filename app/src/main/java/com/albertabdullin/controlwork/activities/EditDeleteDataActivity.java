package com.albertabdullin.controlwork.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.BackPressListener;
import com.albertabdullin.controlwork.fragments.DeleteDataFragment;
import com.albertabdullin.controlwork.fragments.EditDataFragment;
import com.albertabdullin.controlwork.fragments.ListDBItemsFragment;
import com.albertabdullin.controlwork.viewmodels.DialogFragmentStateHolder;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryEditDeleteData;

import java.util.Stack;

public class EditDeleteDataActivity extends AppCompatActivity implements ProviderOfHolderFragmentState, NotifierOfBackPressed {
    private EditDeleteDataVM viewModel;
    private static String failDelete;
    private static String failLoadFromResultTable;
    private static String failLoadFromPrimaryTable;
    private static String failUpdateInResultTable;
    private Stack<BackPressListener> stackOfBackPressListener;

    public static final int FAIL_ABOUT_LOAD_DATA_FROM_RESULT_TABLE = 0;
    public static final int FAIL_ABOUT_DELETE_DATA_FROM_DB = 1;
    public static final int FAIL_ABOUT_LOAD_DATA_FROM_PRIMARY_TABLE = 2;
    public static final int FAIL_ABOUT_UPDATE_DATA_IN_RESULT_TABLE = 3;

    Observer<Boolean> observerOfSuccessUpdateDataToast = aBoolean -> {
        if (aBoolean) Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.data_has_been_updated), Toast.LENGTH_SHORT).show();
    };

    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FAIL_ABOUT_LOAD_DATA_FROM_RESULT_TABLE:
                    throw new RuntimeException(failLoadFromResultTable);
                case FAIL_ABOUT_DELETE_DATA_FROM_DB:
                    throw new RuntimeException(failDelete);
                case FAIL_ABOUT_LOAD_DATA_FROM_PRIMARY_TABLE:
                    throw new RuntimeException(failLoadFromPrimaryTable);
                case FAIL_ABOUT_UPDATE_DATA_IN_RESULT_TABLE:
                    throw new RuntimeException(failUpdateInResultTable);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_data);
        viewModel = new ViewModelProvider(this, new ViewModelFactoryEditDeleteData(this.getApplication())).get(EditDeleteDataVM.class);
        failDelete = getString(R.string.fail_attempt_about_delete_data_from_db);
        failLoadFromResultTable = getString(R.string.fail_attempt_about_load_data_from_result_table);
        failLoadFromPrimaryTable = getString(R.string.fail_attempt_about_load_data_from_primary_table);
        failUpdateInResultTable = getString(R.string.fail_attempt_about_update_data_in_result_table);
        if (viewModel.getToastAboutSuccessUpdateDataLD().hasObservers())
            viewModel.getToastAboutSuccessUpdateDataLD().removeObserver(observerOfSuccessUpdateDataToast);
        viewModel.getToastAboutSuccessUpdateDataLD().observe(this, observerOfSuccessUpdateDataToast);
        DeleteDataFragment deleteDataFragment = (DeleteDataFragment)
                getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_for_delete_data_fragment));
        EditDataFragment editDataFragment = (EditDataFragment)
                getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_for_edit_data_fragment));
        ListDBItemsFragment listDBItemsFragment = (ListDBItemsFragment)
                getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_for_list_of_DB_items_fragment));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (listDBItemsFragment != null) {
            transaction.replace(R.id.container_for_edit_delete_data_fragment, listDBItemsFragment,
                    getString(R.string.tag_for_list_of_DB_items_fragment));
            transaction.commit();
            return;
        }
        if (editDataFragment != null) {
            transaction.replace(R.id.container_for_edit_delete_data_fragment, editDataFragment,
                    getString(R.string.tag_for_edit_data_fragment));
            transaction.commit();
            return;
        }
        if (deleteDataFragment != null) {
            transaction.replace(R.id.container_for_edit_delete_data_fragment, deleteDataFragment,
                    getString(R.string.tag_for_delete_data_fragment));
        } else {
            deleteDataFragment = new DeleteDataFragment();
            transaction.add(R.id.container_for_edit_delete_data_fragment, deleteDataFragment,
                    getString(R.string.tag_for_delete_data_fragment));
        }
        transaction.commit();
    }

    @Override
    public DialogFragmentStateHolder getHolder() {
        return viewModel;
    }

    @Override
    public void addListener(BackPressListener backPressListener) {
        if (stackOfBackPressListener == null) stackOfBackPressListener = new Stack<>();
        stackOfBackPressListener.push(backPressListener);
    }

    @Override
    public void onBackPressed() {
        viewModel.setPressedBackButton(true);
        super.onBackPressed();
        notifyListeners();
        viewModel.setPressedBackButton(false);
    }

    @Override
    public void removeListener() {
        if (stackOfBackPressListener != null && stackOfBackPressListener.size() != 0)  {
            if (viewModel.isBackButtonNotPressed()) stackOfBackPressListener.pop();
        }
    }

    @Override
    public void notifyListeners() {
        if (stackOfBackPressListener != null && stackOfBackPressListener.size() != 0)  {
            stackOfBackPressListener.pop().OnBackPress();
        }
    }
}
