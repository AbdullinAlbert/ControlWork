package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.SelectionTracker;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.DeleteDataDF;
import com.albertabdullin.controlwork.fragments.UpdateDataDF;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;

import java.util.List;


public class AMControllerForListItems implements ActionMode.Callback {
    static private SelectionTracker tracker;
    private AdapterForItemsFromDB adapter;
    private AppCompatActivity activity;

    public AMControllerForListItems(SelectionTracker tracker, AdapterForItemsFromDB adapter,
                                    AppCompatActivity activity) {
        this.tracker = tracker;
        this.adapter = adapter;
        this.activity = activity;
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
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_select_all_items):
                List<SimpleEntityForDB> list = adapter.getCopyListOfEntities();
                for(int i = 0; i < list.size(); i++)
                    if(!tracker.isSelected(list.get(i))) tracker.select(list.get(i));
                return true;
            case (R.id.action_delete_item):
                DeleteDataDF wdf = new DeleteDataDF();
                wdf.show(activity.getSupportFragmentManager(), "deleteData");
                return true;
            case (R.id.action_rename_item):
                UpdateDataDF udf = new UpdateDataDF();
                udf.show(activity.getSupportFragmentManager(), "updateData");
                return true;
            default: return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) { tracker.clearSelection(); }

    public static SelectionTracker getTracker() { return tracker; }

}