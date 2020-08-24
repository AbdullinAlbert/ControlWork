package com.albertabdullin.controlwork.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.AMControllerForListItems;
import com.albertabdullin.controlwork.fragments.AddDataDF;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.DBListItemKeyProvider;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.DBListItemLookUP;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.SimpleEntityForDB;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;
import com.albertabdullin.controlwork.recycler_views.RecyclerViewObserver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListOfBDItemsActivity extends AppCompatActivity implements RecyclerViewObserver {
    private AdapterForItemsFromDB adapterForItemsFromDB;
    private ListOfItemsVM model;
    private SelectionTracker selectionTracker;
    private ActionMode actionMode = null;

    private SelectionTracker.SelectionObserver<Long> selectionObserver = new SelectionTracker.SelectionObserver<Long>() {
        @Override
        public void onSelectionChanged() {
            super.onSelectionChanged();
            if(selectionTracker.hasSelection() && actionMode == null) {
                actionMode = startSupportActionMode(new AMControllerForListItems(selectionTracker));
                setSelectedTitle(selectionTracker.getSelection().size());
            }else if(!selectionTracker.hasSelection() && actionMode != null) {
                actionMode.finish();
                actionMode = null;
            }else setSelectedTitle(selectionTracker.getSelection().size());
        }

        private void setSelectedTitle(int i) {
            if(actionMode != null) actionMode.setTitle(Integer.toString(i));
        }
    };

    @Override
    public void onClick(SimpleEntityForDB eDB) {
        Intent resultIntent = new Intent();
        resultIntent
            .putExtra(FillNewData_Activity.ID_FROM_DB, eDB.getID())
            .putExtra(FillNewData_Activity.ITEM_FROM_DB, eDB.getDescription());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_employers);
        Toolbar toolbar = findViewById(R.id.toolbar_list_of_emp);
        toolbar.setTitle("Список сотрудников");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        model = new ViewModelProvider(this, new ListOfItemsVM.ViewModelFactory(this.getApplication())).get(ListOfItemsVM.class);
        RecyclerView recyclerView = findViewById(R.id.list_of_emp);
        Observer<List<SimpleEntityForDB>> observerRV = new Observer<List<SimpleEntityForDB>>() {
            @Override
            public void onChanged(List<SimpleEntityForDB> changedList) {
                if(model.getHelperListOfEntities().size() == 0) {
                    model.getHelperListOfEntities().addAll(changedList);
                    adapterForItemsFromDB.notifyDataSetChanged();
                }else if(model.getHelperListOfEntities().size() != changedList.size())
                    adapterForItemsFromDB.notifyItemInserted(model.getHelperListOfEntities().size() - 1);
            }
        };
        adapterForItemsFromDB = new AdapterForItemsFromDB(model.getHelperListOfEntities(), this);
        model.getLiveDataEmp().observe(this, observerRV);
        recyclerView.setAdapter(adapterForItemsFromDB);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        selectionTracker = new SelectionTracker.Builder<>(
            "listOFItems",
            recyclerView,
            new DBListItemKeyProvider(adapterForItemsFromDB),
            new DBListItemLookUP(recyclerView),
            StorageStrategy.createParcelableStorage(SimpleEntityForDB.class)
        ).build();
        adapterForItemsFromDB.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(selectionObserver);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDataDF dialogFragment = new AddDataDF();
                dialogFragment.show(getSupportFragmentManager(), "newData");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stable_appbar_list_items, menu);
        return true;
    }
}