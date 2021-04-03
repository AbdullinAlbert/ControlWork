package com.albertabdullin.controlwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

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

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.customView.SearchEditText;
import com.albertabdullin.controlwork.fragments.CommonAddDataDF;
import com.albertabdullin.controlwork.fragments.InsertDataButtonClickExecutor;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;
import com.albertabdullin.controlwork.recycler_views.RecyclerViewObserver;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.AMControllerForListItemsFromDB;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.DBListItemKeyProvider;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.DBListItemLookUP;
import com.albertabdullin.controlwork.viewmodels.DialogFragmentStateHolder;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryListItems;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListOfDBItemsActivity extends AppCompatActivity implements RecyclerViewObserver, ProviderOfHolderFragmentState {

    private AdapterForItemsFromDB adapterForItemsFromDB;
    private ListOfItemsVM mViewModel;
    private SelectionTracker<SimpleEntityForDB> selectionTracker;
    private ActionMode actionMode = null;
    private FloatingActionButton fab;
    private String hintForDialogFragment;
    private int mCurrentTable;

    public enum adapterState {
        LOAD, UPDATE, DELETE, ADD
    }

    public static Handler handler = new Handler(Looper.getMainLooper());

    private final SelectionTracker.SelectionObserver<SimpleEntityForDB> selectionObserver = new SelectionTracker.SelectionObserver<SimpleEntityForDB>() {
        @Override
        public void onSelectionChanged() {
            if (selectionTracker.hasSelection() && actionMode == null) {
                actionMode = startSupportActionMode(new AMControllerForListItemsFromDB(selectionTracker, adapterForItemsFromDB,
                        ListOfDBItemsActivity.this));
                adapterForItemsFromDB.setActionMode(actionMode);
                setSelectedTitle(selectionTracker.getSelection().size());
                fab.hide();
            } else if(!selectionTracker.hasSelection() && actionMode != null) {
                actionMode.finish();
                actionMode = null;
                adapterForItemsFromDB.setActionMode(null);
                fab.show();
            } else setSelectedTitle(selectionTracker.getSelection().size());
        }

        private void setSelectedTitle(int i) {
            if(actionMode != null) actionMode.setTitle(Integer.toString(i));
        }
    };

    @Override
    public void onClick(SimpleEntityForDB eDB) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(FillNewData_Activity.ITEM_FROM_DB, eDB);
        resultIntent.putExtra(FillNewData_Activity.LAUNCH_DEFINITELY_DB_TABLE, mViewModel.getNumberOfNeededTable());
        setResult(RESULT_OK, resultIntent);
        mViewModel.closeSearchThread();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_items);
        mViewModel = new ViewModelProvider(this, new ViewModelFactoryListItems(this.getApplication())).get(ListOfItemsVM.class);
        Toolbar toolbar = findViewById(R.id.toolbar_list_of_emp);
        mCurrentTable = getIntent().getIntExtra(FillNewData_Activity.LAUNCH_DEFINITELY_DB_TABLE, -1);
        switch (mCurrentTable) {
            case FillNewData_Activity.TABLE_OF_EMPLOYERS:
                toolbar.setTitle(R.string.list_of_employees);
                hintForDialogFragment = getResources().getString(R.string.employee_name_surname);
                mViewModel.setCurrentDBTable(FillNewData_Activity.TABLE_OF_EMPLOYERS);
                break;
            case FillNewData_Activity.TABLE_OF_FIRMS:
                toolbar.setTitle(R.string.list_of_firms);
                hintForDialogFragment = getResources().getString(R.string.firm_name);
                mViewModel.setCurrentDBTable(FillNewData_Activity.TABLE_OF_FIRMS);
                break;
            case FillNewData_Activity.TABLE_OF_TYPES_OF_WORK:
                toolbar.setTitle(R.string.list_of_types_of_work);
                hintForDialogFragment = getResources().getString(R.string.firm_name);
                mViewModel.setCurrentDBTable(FillNewData_Activity.TABLE_OF_TYPES_OF_WORK);
                break;
            case FillNewData_Activity.TABLE_OF_PLACES_OF_WORK:
                toolbar.setTitle(R.string.list_of_places_of_work);
                hintForDialogFragment = getResources().getString(R.string.place_of_work);
                mViewModel.setCurrentDBTable(FillNewData_Activity.TABLE_OF_PLACES_OF_WORK);
                break;
            case FillNewData_Activity.TABLE_OF_RESULT_TYPE:
                toolbar.setTitle(R.string.list_of_types_of_result);
                hintForDialogFragment = getResources().getString(R.string.result_type);
                mViewModel.setCurrentDBTable(FillNewData_Activity.TABLE_OF_RESULT_TYPE);
                break;
            default:
                Toast toast = Toast.makeText(getApplication(), "App works incorrect", Toast.LENGTH_SHORT);
                toast.show();
                finish();
        }
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.list_of_emp);
        adapterForItemsFromDB = new AdapterForItemsFromDB(mViewModel.getAdapterListOfEntitiesVM());
        adapterForItemsFromDB.setRVObserver(this);
        recyclerView.setAdapter(adapterForItemsFromDB);
        Observer<adapterState> observerRV = state -> {
            switch (state) {
                case LOAD:
                    adapterForItemsFromDB.notifyDataSetChanged();
                    break;
                case ADD:
                    adapterForItemsFromDB.notifyItemInserted(mViewModel.getAdapterListOfEntitiesVM().size() - 1);
                    break;
                case UPDATE:
                    adapterForItemsFromDB.notifyItemChanged(mViewModel.getUpdatedItemPosition());
                    break;
                case DELETE:
                    for (int index : mViewModel.getListOfDeletedPositions()) {
                        mViewModel.getAdapterListOfEntitiesVM().remove(index);
                        adapterForItemsFromDB.notifyItemRemoved(index);
                    }
            }
        };
        mViewModel.getLiveData().observe(this, observerRV);
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
        fab.setOnClickListener(v -> {
            CommonAddDataDF commonAddDataDF = new CommonAddDataDF()
                    .setHint(hintForDialogFragment)
                    .setInputType(CommonAddDataDF.EditTextInputType.TEXT_PERSON_NAME)
                    .setLengthOfText(mCurrentTable == FillNewData_Activity.TABLE_OF_RESULT_TYPE ? getResources().getInteger(R.integer.length_of_string_value_for_result_type)
                            : getResources().getInteger(R.integer.max_length_of_string_value))
                    .setExecutor(new InsertDataButtonClickExecutor() {
                        @Override
                        public void executeYesButtonClick(AppCompatActivity activity, String text) {
                            if (text.length() != 0)
                                ((ListOfDBItemsActivity)activity).mViewModel.addItem(text);
                            else {
                                Toast toast = Toast.makeText(ListOfDBItemsActivity.this,
                                        "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        @Override
                        public void executeNoButtonClick() { }
                    });
            commonAddDataDF.show(getSupportFragmentManager(), "newData");
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionTracker.onSaveInstanceState(outState);
        mViewModel.setBlankCallTrue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stable_appbar_list_items, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                mViewModel.setItemSearchText(newText);
                if (newText.equals("")) mViewModel.sayToStopSearch(before);
                else if (mViewModel.isSearchIsActive()) mViewModel.sendNewText(newText);
                else mViewModel.startSearch(newText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (hasFocus && !mViewModel.isStateMenuItemSearchTextActive()) {
                mViewModel.setStateMenuItemSearchText(true);
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, 0);

            }else if (!hasFocus) {
                hideKeyBoard((EditText) v);
            }
        };
        EditText searchEditText = ((SearchEditText)menuItem.getActionView()).getTextView();
        searchEditText.addTextChangedListener(textWatcher);
        searchEditText.setOnFocusChangeListener(focusChangeListener);
        MenuItem.OnActionExpandListener actionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mViewModel.setStateMenuItemSearchText(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mViewModel.sayToStopSearch(-1);
                mViewModel.closeSearchThread();
                mViewModel.setStateMenuItemSearchText(false);
                fab.show();
                return true;
            }
        };

        menuItem.setOnActionExpandListener(actionExpandListener);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mViewModel.isStateMenuItemSearchTextActive()) {
            menu.performIdentifierAction(R.id.action_search, 0);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                EditText searchEditText = ((SearchEditText)item.getActionView()).getTextView();
                searchEditText.setText(mViewModel.getItemSearchText());
                searchEditText.requestFocus();
                fab.hide();
                break;
            case android.R.id.home:
                Intent resultIntent = new Intent();
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        fab.show();
    }

    public String getHintForDialogFragment() {
        return hintForDialogFragment;
    }

    public SelectionTracker<SimpleEntityForDB> getSelectionTracker() {
        return selectionTracker;
    }

    public int getCurrentTable() { return mCurrentTable; }

    @Override
    public DialogFragmentStateHolder getHolder() {
        return mViewModel;
    }
}