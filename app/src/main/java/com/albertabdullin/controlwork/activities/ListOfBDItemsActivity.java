package com.albertabdullin.controlwork.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.View;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.AMControllerForListItems;
import com.albertabdullin.controlwork.fragments.AddDataDF;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.DBListItemKeyProvider;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.DBListItemLookUP;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;
import com.albertabdullin.controlwork.recycler_views.RecyclerViewObserver;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListOfBDItemsActivity extends AppCompatActivity implements RecyclerViewObserver {
    public static final int ADD = 0;
    public static final int DELETE = 1;
    public static final int UPDATE = 2;
    public static final int LOAD = 3;
    public static final int SEARCH_IS_DONE = 4;
    public static final int OK = 5;
    public static final int NOT_OK = 6;
    private AdapterForItemsFromDB adapterForItemsFromDB;
    private static ListOfItemsVM model;
    private SelectionTracker selectionTracker;
    private ActionMode actionMode = null;
    private FloatingActionButton fab;

    public static Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage (Message msg) {
            switch (msg.what) {
                case ADD:
                    model.notifyAboutAddItem();
                    break;
                case DELETE:
                    if (msg.arg1 == OK) model.notifyAboutDeleteItem(true);
                    else model.notifyAboutDeleteItem(false);
                    break;
                case UPDATE:
                    if (msg.arg1 == OK) model.notifyAboutUpdateItem(true);
                    else model.notifyAboutUpdateItem(false);
                    break;
                case LOAD:
                    model.notifyAboutLoadItems();
                    break;
                case SEARCH_IS_DONE:
                    model.updateSearchAdapterList();
                    break;
            }
        }

    };

    private SelectionTracker.SelectionObserver<Long> selectionObserver = new SelectionTracker.SelectionObserver<Long>() {
        @Override
        public void onSelectionChanged() {
            super.onSelectionChanged();
            if(selectionTracker.hasSelection() && actionMode == null) {
                actionMode = startSupportActionMode(new AMControllerForListItems(selectionTracker, adapterForItemsFromDB,
                        ListOfBDItemsActivity.this));
                adapterForItemsFromDB.setActionMode(actionMode);
                setSelectedTitle(selectionTracker.getSelection().size());
                fab.hide();
            }else if(!selectionTracker.hasSelection() && actionMode != null) {
                actionMode.finish();
                actionMode = null;
                adapterForItemsFromDB.setActionMode(null);
                fab.show();
            }else setSelectedTitle(selectionTracker.getSelection().size());
        }

        private void setSelectedTitle(int i) {
            if(actionMode != null) actionMode.setTitle(Integer.toString(i));
        }
    };

    @Override
    public void onClick(SimpleEntityForDB eDB) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(FillNewData_Activity.ITEM_FROM_DB, eDB);
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
        model = new ViewModelProvider(this, new ViewModelFactory(this.getApplication())).get(ListOfItemsVM.class);
        RecyclerView recyclerView = findViewById(R.id.list_of_emp);
        Observer<List<SimpleEntityForDB>> observerRV = new Observer<List<SimpleEntityForDB>>() {
            @Override
            public void onChanged(List<SimpleEntityForDB> changedList) {
                //если пустой список элементов
                if(model.getAdapterListOfEntitiesVM().size() == 0) {
                    model.getAdapterListOfEntitiesVM().addAll(changedList);
                    adapterForItemsFromDB.notifyDataSetChanged();
                //если добавлены новые элементы в список
                } else if (model.getAdapterListOfEntitiesVM().size() < changedList.size()) {
                    for (int i = model.getAdapterListOfEntitiesVM().size(); i < changedList.size(); i++) {
                        model.getAdapterListOfEntitiesVM().add(changedList.get(i));
                        adapterForItemsFromDB.notifyItemInserted(model.getAdapterListOfEntitiesVM().size() - 1);
                    }
                //если были удалены элементы из списка
                } else if (model.getAdapterListOfEntitiesVM().size() > changedList.size()) {
                    List<Integer> deletedPositions = model.getListOfDeletedPositions();
                    for (int i = 0; i < deletedPositions.size(); i++) {
                        int p = deletedPositions.get(i) - i;
                        model.getAdapterListOfEntitiesVM().remove(p);
                        adapterForItemsFromDB.notifyItemRemoved(p);
                    }
                //если изменили какой-либо из элементов
                } else if (model.getAdapterListOfEntitiesVM().size() == changedList.size()) {
                    adapterForItemsFromDB.notifyItemChanged(model.getUpdatedItemPosition());
                }
            }
        };
        adapterForItemsFromDB = new AdapterForItemsFromDB(model.getAdapterListOfEntitiesVM());
        adapterForItemsFromDB.setRVObserver(this);
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
        if(savedInstanceState != null)
            selectionTracker.onRestoreInstanceState(savedInstanceState);
        selectionTracker.addObserver(selectionObserver);
        adapterForItemsFromDB.setSelectionTracker(selectionTracker);
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDataDF dialogFragment = new AddDataDF();
                dialogFragment.show(getSupportFragmentManager(), "newData");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
        model.setBlankCallTrue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stable_appbar_list_items, menu);
        SearchView.OnQueryTextListener queryTextChangeListener = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) model.sayToStop();
                else if (model.isSearchIsActive()) model.sendNewText(newText);
                else model.startSearch(newText);
                return true;
            }
        };

        SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                model.stopSearch();
                return true;
            }
        };
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(queryTextChangeListener);
        searchView.setOnCloseListener(closeListener);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        return true;
    }

}