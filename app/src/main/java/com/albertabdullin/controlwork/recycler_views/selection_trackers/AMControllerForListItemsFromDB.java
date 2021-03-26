package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.SelectionTracker;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ListOfDBItemsActivity;
import com.albertabdullin.controlwork.activities.ProviderOfHolderFragmentState;
import com.albertabdullin.controlwork.fragments.CommonAddDataDF;
import com.albertabdullin.controlwork.fragments.CommonDeleteDataDF;
import com.albertabdullin.controlwork.fragments.DeleteDataButtonClickExecutor;
import com.albertabdullin.controlwork.fragments.InsertDataButtonClickExecutor;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AMControllerForListItemsFromDB implements ActionMode.Callback {
    private final SelectionTracker<SimpleEntityForDB> tracker;
    private final AdapterForItemsFromDB adapter;
    private final ListOfDBItemsActivity activity;

    public AMControllerForListItemsFromDB(SelectionTracker<SimpleEntityForDB> tracker, AdapterForItemsFromDB adapter,
                                          ListOfDBItemsActivity activity) {
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
                String header = activity.getResources().getString(R.string.selected_records_with_colon) + " " + tracker.getSelection().size();
                commonDeleteDataDF.setHeader(header);
                String mainText;
                if(tracker.getSelection().size() == 1) {
                    Iterator<SimpleEntityForDB> iterator = tracker.getSelection().iterator();
                    mainText = "Вы действительно хотите удалить " + iterator.next().getDescription() + " ?";
                    commonDeleteDataDF.setMainText(mainText);
                }
                commonDeleteDataDF.setExecutor(new DeleteDataButtonClickExecutor() {
                    @Override
                    public void executeYesButtonClick(AppCompatActivity appCompatActivity) {
                        SelectionTracker<SimpleEntityForDB> localTracker = ((ListOfDBItemsActivity) appCompatActivity).getSelectionTracker();
                        Iterator<SimpleEntityForDB> iterator = localTracker.getSelection().iterator();
                        List<SimpleEntityForDB> deletedItemsList = new ArrayList<>();
                        while (iterator.hasNext()) deletedItemsList.add(iterator.next());
                        ProviderOfHolderFragmentState provider = ((ListOfDBItemsActivity) appCompatActivity);
                        ((ListOfItemsVM)provider.getHolder()).deleteItem(deletedItemsList);
                        localTracker.clearSelection();
                    }
                    @Override
                    public void executeNoButtonClick(AppCompatActivity appCompatActivity) {
                    }
                });
                commonDeleteDataDF.show(activity.getSupportFragmentManager(), "deleteData");
                return true;
            case (R.id.action_rename_item):
                CommonAddDataDF commonAddDataDF = new CommonAddDataDF()
                        .setHint(activity.getHintForDialogFragment())
                        .setInputType(CommonAddDataDF.EditTextInputType.TEXT_PERSON_NAME)
                        .setLengthOfText(activity.getResources().getInteger(R.integer.max_length_of_string_value))
                        .setExecutor(new InsertDataButtonClickExecutor() {
                            @Override
                            public void executeYesButtonClick(AppCompatActivity activity, String text) {
                                if (text.length() != 0) {
                                    SelectionTracker<SimpleEntityForDB> localTracker =
                                            ((ListOfDBItemsActivity) activity).getSelectionTracker();
                                    Iterator<SimpleEntityForDB> iterator = localTracker.getSelection().iterator();
                                    SimpleEntityForDB eDB = iterator.next();
                                    ProviderOfHolderFragmentState provider = ((ListOfDBItemsActivity) activity);
                                    ((ListOfItemsVM)provider.getHolder()).updateItem(eDB, text);
                                    localTracker.clearSelection();
                                }
                                else {
                                    Toast toast = Toast.makeText(activity,
                                            "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                            @Override
                            public void executeNoButtonClick() {
                            }
                        });
                commonAddDataDF.show(activity.getSupportFragmentManager(), "newData");
                return true;
            default: return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
         tracker.clearSelection();
    }

}