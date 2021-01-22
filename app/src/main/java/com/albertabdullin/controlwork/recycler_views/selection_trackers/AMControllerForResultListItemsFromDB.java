package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.SelectionTracker;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForResultListFromQuery;

import java.util.Iterator;


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
        switch (item.getItemId()) {
            case (R.id.action_select_all_items):
                Iterator<ComplexEntityForDB> iterator = mAdapter.getIterator();
                while (iterator.hasNext()) {
                    ComplexEntityForDB eDB = iterator.next();
                    if (!mTracker.isSelected(eDB)) mTracker.select(eDB);
                }
                return true;
            case (R.id.action_delete_item):
                return true;
            default: return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mTracker.clearSelection();
    }

}
