package com.albertabdullin.controlwork.fragments;

import android.graphics.Point;
import android.os.Bundle;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


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
import com.albertabdullin.controlwork.db_of_app.CWDBHelper;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForPickItems;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

import java.util.List;

public class PickerItemsDF extends DialogFragment {
    private EditDeleteDataVM model;
    private int selectedTable;
    private static final String EMPLOEEYS_TITLE = "Список сотрудников";
    private static final String FIRMS_TITLE = "Список фирм";
    private static final String TYPES_TITLE = "Список типов работы";
    private static final String PLACES_TITLE = "Список мест работы";
    private static final String SAVED_TITLE_OF_TABLE = "saved_table";
    List<SimpleEntityForDB> list = null;

    public PickerItemsDF() {}

    public PickerItemsDF(int table) {
        selectedTable = table;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(getActivity()).get(EditDeleteDataVM.class);
        list = model.getAdapterListOfEntities(selectedTable);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_picker_items, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.title_for_select_items_toolbar);
        String title = "";
        final AdapterForPickItems adapter;
        if (savedInstanceState != null) selectedTable = savedInstanceState.getInt(SAVED_TITLE_OF_TABLE);
        switch (selectedTable) {
            case SearchCriteriaFragment.SELECT_EMPLOYEES:
                title = EMPLOEEYS_TITLE;
                adapter = new AdapterForPickItems(list, model, this, selectedTable);
                model.showFullListOfItems(selectedTable);
                break;
            case SearchCriteriaFragment.SELECT_FIRMS:
                title = FIRMS_TITLE;
                adapter = new AdapterForPickItems(list, model, this, selectedTable);
                model.showFullListOfItems(selectedTable);
                break;
            case SearchCriteriaFragment.SELECT_TYPES:
                title = TYPES_TITLE;
                adapter = new AdapterForPickItems(list, model, this, selectedTable);
                model.showFullListOfItems(selectedTable);
                break;
            case SearchCriteriaFragment.SELECT_PLACES:
                title = PLACES_TITLE;
                adapter = new AdapterForPickItems(list, model, this, selectedTable);
                model.showFullListOfItems(selectedTable);
                break;
            default:
                adapter = null;
        }
        final ImageView clearButton = view.findViewById(R.id.button_for_clear_selected_cb);
        if (model.getTransientListOfSelectedItems().isEmpty()) clearButton.setVisibility(View.INVISIBLE);
        else clearButton.setVisibility(View.VISIBLE);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.clearSelectedCheckBoxes(selectedTable);
                clearButton.setVisibility(View.INVISIBLE);
            }
        });
        Observer<Integer> observerClearButton = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (!model.getTransientListOfSelectedItems().isEmpty() && integer == View.VISIBLE)
                    clearButton.setVisibility(View.VISIBLE);
                else if (model.getTransientListOfSelectedItems().isEmpty() && integer == View.INVISIBLE)
                    clearButton.setVisibility(View.INVISIBLE);

            }
        };
        model.getVisibilityOfClearButtonLD().observe(getViewLifecycleOwner(), observerClearButton);
        toolbar.setTitle(title);
        RecyclerView rv = view.findViewById(R.id.rv_for_selectable_items);
        rv.setAdapter(adapter);
        Observer<List<SimpleEntityForDB>> rvObserver = new Observer<List<SimpleEntityForDB>>() {
            @Override
            public void onChanged(List<SimpleEntityForDB> simpleEntityForDBS) {
                if (list.size() == 0) list.addAll(simpleEntityForDBS);
                adapter.notifyDataSetChanged();
            }
        };
        model.getEntitiesLiveData().observe(this, rvObserver);
        rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(divider);
        Button cancelButton = view.findViewById(R.id.cancel_select_items_for_search);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();

            }
        });
        Button agreeButton = view.findViewById(R.id.agree_button_for_selected_items);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.commitSelectedList(selectedTable);
                getDialog().dismiss();
            }
        });
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SAVED_TITLE_OF_TABLE, selectedTable);
        super.onSaveInstanceState(outState);
    }
}
