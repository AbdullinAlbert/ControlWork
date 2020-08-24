package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.SelectionTracker;

import com.albertabdullin.controlwork.R;

public class AMControllerForListItems implements ActionMode.Callback {
    private SelectionTracker tracker;
    public AMControllerForListItems(SelectionTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.actions_for_items_of_recyclerview, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.action_rename_item).setVisible(true);
        if(tracker.getSelection().size() > 1)
            menu.findItem(R.id.action_rename_item).setVisible(false);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) { return false; }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        tracker.clearSelection();
    }
}