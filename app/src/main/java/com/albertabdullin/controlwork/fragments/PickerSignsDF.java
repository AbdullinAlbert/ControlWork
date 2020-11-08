package com.albertabdullin.controlwork.fragments;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.recycler_views.AdapterForPickIneqaulEqualSign;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;


public class PickerSignsDF extends DialogFragment implements DFPickerObservable{
    public static final int FULL_SET_SIGNS = 0;
    public static final int MORE_OR_EQUAL_SET_SIGNS = 1;
    public static final int LESS_OR_EQUAL_SET_SIGNS = 2;
    public static final int OTHER_SET_SIGNS = 3;
    public static final String TAG_FOR_SELECTED_SET_SIGNS = "tag for selected set signs";
    private EditDeleteDataVM model;
    private DFPickerObserver dfPickerObserver;
    private int selectedSetOfSigns;

    public PickerSignsDF() {}

    public PickerSignsDF(int selectedSetOfSigns) {
        this.selectedSetOfSigns = selectedSetOfSigns;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
        if (savedInstanceState != null) selectedSetOfSigns = savedInstanceState.getInt(TAG_FOR_SELECTED_SET_SIGNS);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_picker_equal_inequal_sign, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final AdapterForPickIneqaulEqualSign adapter = new AdapterForPickIneqaulEqualSign(model, requireActivity(), this);
        RecyclerView rv = view.findViewById(R.id.rv_for_selectable_items);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(divider);
        Button cancelButton = view.findViewById(R.id.cancel_select_items_for_search);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setSelectedEqualSign(-1);
                notifyAboutSelection();
                Dialog dialog = getDialog();
                if (dialog != null) dialog.dismiss();

            }
        });
        Button agreeButton = view.findViewById(R.id.agree_button_for_selected_items);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAG_FOR_SELECTED_SET_SIGNS, selectedSetOfSigns);
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.7), (int) (size.y * 0.6));
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    public int getSelectedSetOfSigns() {
        return selectedSetOfSigns;
    }

    @Override
    public void setDFSignPickerObserver(DFPickerObserver dfPickerObserver) {
        this.dfPickerObserver = dfPickerObserver;
    }

    @Override
    public void notifyAboutSelection() {
        dfPickerObserver.changeLayoutForCertainCriteria(selectedSetOfSigns);
    }
}
