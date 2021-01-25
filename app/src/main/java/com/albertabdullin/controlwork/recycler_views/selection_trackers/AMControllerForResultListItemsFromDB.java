package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.SelectionTracker;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.EditDeleteDataActivity;
import com.albertabdullin.controlwork.activities.ListOfDBItemsActivity;
import com.albertabdullin.controlwork.activities.ProviderOfHolderFragmentState;
import com.albertabdullin.controlwork.fragments.CommonDeleteDataDF;
import com.albertabdullin.controlwork.fragments.DeleteDataButtonClickExecutor;
import com.albertabdullin.controlwork.fragments.DeleteDataFragment;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForResultListFromQuery;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AMControllerForResultListItemsFromDB implements ActionMode.Callback {
    private final SelectionTracker<ComplexEntityForDB> mTracker;
    private final AdapterForResultListFromQuery mAdapter;
    private final AppCompatActivity mActivity;

    public AMControllerForResultListItemsFromDB(SelectionTracker<ComplexEntityForDB> tracker,
                                                AdapterForResultListFromQuery adapter,
                                                AppCompatActivity activity) {
        mTracker = tracker;
        mAdapter = adapter;
        mActivity = activity;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.actions_for_result_list_items, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Iterator<ComplexEntityForDB> iterator;
        switch (item.getItemId()) {
            case (R.id.action_select_all_items):
                iterator = mAdapter.getIterator();
                while (iterator.hasNext()) {
                    ComplexEntityForDB eDB = iterator.next();
                    if (!mTracker.isSelected(eDB)) mTracker.select(eDB);
                }
                return true;
            case (R.id.action_delete_item):
                CommonDeleteDataDF commonDeleteDataDF = new CommonDeleteDataDF();
                String header = "Выбрано записей: " + mTracker.getSelection().size();
                commonDeleteDataDF.setHeader(header);
                if (mTracker.getSelection().size() == 1) {
                    iterator = mTracker.getSelection().iterator();
                    String mainText = "Вы действительно хотите удалить запись № " + iterator.next().getID() + " ?";
                    commonDeleteDataDF.setMainText(mainText);
                }
                commonDeleteDataDF.setExecutor(new DeleteDataButtonClickExecutor() {
                    @Override
                    public void executeYesButtonClick(AppCompatActivity appCompatActivity) {
                        DeleteDataFragment deleteDataFragment =
                                (DeleteDataFragment) appCompatActivity
                                        .getSupportFragmentManager().findFragmentByTag("delete_result_of_search_criteria_fragment");
                        SelectionTracker<ComplexEntityForDB> localTracker = deleteDataFragment.getSelectionTracker();
                        Iterator<ComplexEntityForDB> iterator = localTracker.getSelection().iterator();
                        List<ComplexEntityForDB> deletedItemsList = new ArrayList<>();
                        while (iterator.hasNext()) deletedItemsList.add(iterator.next());
                        ProviderOfHolderFragmentState provider = ((EditDeleteDataActivity) appCompatActivity);
                        ((EditDeleteDataVM)provider.getHolder()).deleteItem(deletedItemsList);
                        localTracker.clearSelection();
                    }

                    @Override
                    public void executeNoButtonClick(AppCompatActivity appCompatActivity) {

                    }
                });
                commonDeleteDataDF.show(mActivity.getSupportFragmentManager(), "delete_data");
                return true;
            default: return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mTracker.clearSelection();
    }

}
