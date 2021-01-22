package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import android.os.Parcel;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ListOfDBItemsActivity;
import com.albertabdullin.controlwork.fragments.CommonDeleteDataDF;
import com.albertabdullin.controlwork.fragments.ButtonClickExecutor;
import com.albertabdullin.controlwork.fragments.UpdateDataDF;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AMControllerForListItemsFromDB implements ActionMode.Callback {
    private final SelectionTracker<SimpleEntityForDB> tracker;
    private final AdapterForItemsFromDB adapter;
    private final AppCompatActivity activity;

    public AMControllerForListItemsFromDB(SelectionTracker<SimpleEntityForDB> tracker, AdapterForItemsFromDB adapter,
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
                CommonDeleteDataDF commonDeleteDataDF = new CommonDeleteDataDF();
                String header = activity.getResources().getString(R.string.header_of_delete_dialog_fragment) + " " + tracker.getSelection().size();
                commonDeleteDataDF.setHeader(header);
                String mainText;
                if(tracker.getSelection().size() == 1) {
                    Iterator<SimpleEntityForDB> iterator = tracker.getSelection().iterator();
                    mainText = "Вы действительно хотите удалить " + iterator.next().getDescription();
                    commonDeleteDataDF.setMainText(mainText);
                }
                commonDeleteDataDF.setExecutor(new ButtonClickExecutor() {
                    @Override
                    public void executeYesButtonClick(AppCompatActivity appCompatActivity) {
                        SelectionTracker<SimpleEntityForDB> localTracker = ((ListOfDBItemsActivity) appCompatActivity).getSelectionTracker();
                        Iterator<SimpleEntityForDB> iterator = localTracker.getSelection().iterator();
                        List<SimpleEntityForDB> deletedItemsList = new ArrayList<>();
                        while (iterator.hasNext()) deletedItemsList.add(iterator.next());
                        ((ListOfDBItemsActivity) appCompatActivity).getViewModel().deleteItem(deletedItemsList);
                        localTracker.clearSelection();
                    }
                    @Override
                    public void executeNoButtonClick(AppCompatActivity appCompatActivity) {
                    }
                });
                commonDeleteDataDF.show(activity.getSupportFragmentManager(), "deleteData");
                return true;
            case (R.id.action_rename_item):
                UpdateDataDF udf = new UpdateDataDF();
                udf.show(activity.getSupportFragmentManager(), "updateData");
                return true;
            default: return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
         tracker.clearSelection();
    }

}