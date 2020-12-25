package com.albertabdullin.controlwork.fragments;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.customView.SearchEditText;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForPickItems;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PickerItemsDF extends DialogFragment {
    private EditDeleteDataVM model;
    private int selectedTable;
    private static final String EMPLOYEES_TITLE = "Список сотрудников";
    private static final String FIRMS_TITLE = "Список фирм";
    private static final String TYPES_TITLE = "Список типов работы";
    private static final String PLACES_TITLE = "Список мест работы";
    private static final String SAVED_TITLE_OF_TABLE = "saved_table";
    List<SimpleEntityForDB> list = null;
    private Toolbar toolbar;
    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && !model.isStateMenuItemSearchTextActive()) {
                model.setStateMenuItemSearchText(true);
                InputMethodManager imm = (InputMethodManager)
                            requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, 0);
                } else if (!hasFocus) {
                    hideKeyBoard((EditText) v);
                }
        }
    };

    public PickerItemsDF() {}

    public PickerItemsDF(int table) {
        selectedTable = table;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
        if (savedInstanceState != null) selectedTable = savedInstanceState.getInt(SAVED_TITLE_OF_TABLE);
        list = model.getAdapterListOfEntities(selectedTable);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_picker_items, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.title_for_select_items_toolbar);
        String title = "";
        final AdapterForPickItems adapter = new AdapterForPickItems(list, model, this, selectedTable);
        model.showFullListOfItems(selectedTable);
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                title = EMPLOYEES_TITLE;
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                title = FIRMS_TITLE;
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                title = TYPES_TITLE;
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                title = PLACES_TITLE;
                break;
        }
        toolbar.setTitle(title);
        toolbar.inflateMenu(R.menu.menu_for_pick_items);
        final EditText searchEditText = ((SearchEditText) toolbar.getMenu().getItem(0).getActionView()).getTextView();
        searchEditText.setOnFocusChangeListener(focusChangeListener);
        boolean currentStateOfItem = model.getStateOfSelectAllMenuItem(selectedTable);
        if (currentStateOfItem) model.setCurrentVisiblePositionOfOverFlowMenu(1);
        else model.setCurrentVisiblePositionOfOverFlowMenu(2);
        toolbar.getMenu().getItem(1).setVisible(currentStateOfItem);
        toolbar.getMenu().getItem(2).setVisible(!currentStateOfItem);
        toolbar.getMenu().getItem(0).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    toolbar.getMenu().getItem(model.getCurrentVisiblePositionOfOverFlowMenu()).setVisible(false);
                    searchEditText.requestFocus();
                    return true;
                } else return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    toolbar.getMenu().getItem(model.getCurrentVisiblePositionOfOverFlowMenu()).setVisible(true);
                    searchEditText.setText("");
                    return true;
                } else return false;
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_select_all_items:
                        toolbar.getMenu().getItem(1).setVisible(false);
                        toolbar.getMenu().getItem(2).setVisible(true);
                        model.setCurrentVisiblePositionOfOverFlowMenu(2);
                        model.selectAllCheckBoxes(selectedTable);
                        return true;
                    case R.id.action_clear_all_items:
                        toolbar.getMenu().getItem(2).setVisible(false);
                        toolbar.getMenu().getItem(1).setVisible(true);
                        model.setCurrentVisiblePositionOfOverFlowMenu(1);
                        model.clearSelectedCheckBoxes(selectedTable);
                        return true;
                    default:
                        return false;
                }
            }
        });
        RecyclerView rv = view.findViewById(R.id.rv_for_selectable_items);
        rv.setAdapter(adapter);
        Observer<Integer> rvObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (selectedTable == integer) {
                    assert adapter != null;
                    adapter.notifyDataSetChanged();
                }
            }
        };
        model.getEntitiesLiveData().observe(this, rvObserver);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(requireActivity());
        rv.setLayoutManager(mLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration
                (new android.view.ContextThemeWrapper(requireActivity(), R.style.AppTheme), mLayoutManager.getOrientation());
        rv.addItemDecoration(divider);
        Button cancelButton = view.findViewById(R.id.cancel_select_items_for_search);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.clearTransientListOfSelectedItems(selectedTable);
                Dialog dialog = getDialog();
                if (dialog != null) dialog.dismiss();

            }
        });
        Button agreeButton = view.findViewById(R.id.agree_button_for_selected_items);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.commitSelectedList(selectedTable);
                Dialog dialog = getDialog();
                if (dialog != null) dialog.dismiss();
            }
        });
    }

    public void updateVisibilityOfItemsOfOverFlowMenu() {
        boolean visibility = toolbar.getMenu().getItem(1).isVisible();
        toolbar.getMenu().getItem(1).setVisible(!visibility);
        toolbar.getMenu().getItem(2).setVisible(visibility);
        if (model.getCurrentVisiblePositionOfOverFlowMenu() == 1) model.setCurrentVisiblePositionOfOverFlowMenu(2);
        else model.setCurrentVisiblePositionOfOverFlowMenu(1);
    }


    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), (int) (size.y * 0.6));
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    private void hideKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager)
                requireActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        model.setStateMenuItemSearchText(false);
    }

}
