package com.albertabdullin.controlwork.activities;

import androidx.annotation.NonNull;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
    private AdapterForItemsFromDB adapterForItemsFromDB;
    private ListOfItemsVM model;
    private SelectionTracker selectionTracker;
    private ActionMode actionMode = null;
    private FloatingActionButton fab;

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
                }else if(model.getAdapterListOfEntitiesVM().size() < changedList.size()) {
                    for(int i = model.getAdapterListOfEntitiesVM().size(); i < changedList.size(); i++) {
                        model.getAdapterListOfEntitiesVM().add(changedList.get(i));
                        adapterForItemsFromDB.notifyItemInserted(model.getAdapterListOfEntitiesVM().size() - 1);
                    }
                //если были удалены элементы из списка
                }else if(model.getAdapterListOfEntitiesVM().size() > changedList.size()) {
                    List<Integer> deletedPositions = model.getListOfDeletedPositions();
                    //список позиций удаленных элементов упорядочен
                    for(int i = 0; i < deletedPositions.size(); i++) {
                        int p = deletedPositions.get(i)-i;
                        model.getAdapterListOfEntitiesVM().remove(p);
                        adapterForItemsFromDB.notifyItemRemoved(p);
                    }
                }//если изменили какой-либо из элементов
                else if(model.getAdapterListOfEntitiesVM().size() == changedList.size()) {
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
                AddDataDF dialogFragment = AddDataDF.getSingletoneObjectAddDataDF();
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
        EditText searchEditText = menu.findItem(R.id.action_search).getActionView()
                .findViewById(R.id.string_of_search);

        TextWatcher twEditTextSearch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                model.setItemSearchText(s.toString());
            }
        };

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !model.isStateMenuItemSearchTextActive()) {
                    model.setStateMenuItemSearchText(true);
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(ListOfBDItemsActivity.this.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, 0);

                }else if (!hasFocus) {
                    hideKeyBoard((EditText) v);
                }
            }
        };
        searchEditText.addTextChangedListener(twEditTextSearch);
        searchEditText.setOnFocusChangeListener(focusChangeListener);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (model.isStateMenuItemSearchTextActive()) {
            menu.performIdentifierAction(R.id.action_search, 0);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_search) :
                EditText searchEditText = item.getActionView().findViewById(R.id.string_of_search);
                searchEditText.setText(model.getItemSearchText());
                searchEditText.requestFocus();
                fab.hide();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyBoard(EditText editText) {
        model.setStateMenuItemSearchText(false);
        model.setItemSearchText("");
        InputMethodManager imm = (InputMethodManager)
                getSystemService(ListOfBDItemsActivity.this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        fab.show();
    }

}