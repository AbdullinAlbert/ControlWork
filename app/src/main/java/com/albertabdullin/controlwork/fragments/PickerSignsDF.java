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
    private EditDeleteDataVM model;
    private DFPickerObserver mDfPickerObserver;
    private String selectedSign;

    public PickerSignsDF() {}

    public PickerSignsDF(String selectedSign) {
        this.selectedSign = selectedSign;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_picker_equal_inequal_sign, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final AdapterForPickIneqaulEqualSign adapter;
        if (selectedSign == null ) adapter = new AdapterForPickIneqaulEqualSign(model, this);
        else adapter = new AdapterForPickIneqaulEqualSign(model, this, selectedSign);
        RecyclerView rv = view.findViewById(R.id.rv_for_selectable_items);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(divider);
        Button cancelButton = view.findViewById(R.id.cancel_select_items_for_search);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = getDialog();
                if (dialog != null) dialog.dismiss();

            }
        });
        Button agreeButton = view.findViewById(R.id.agree_button_for_selected_items);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = getDialog();
                if (dialog != null) dialog.dismiss();
                model.notifyAboutTapAddButton();
                notifyAboutSelection();
            }
        });
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

    @Override
    public void setDFSignPickerObserver(DFPickerObserver dfPickerObserver) {
        mDfPickerObserver = dfPickerObserver;
    }

    @Override
    public void notifyAboutSelection() {
        mDfPickerObserver.changeLayoutForCertainCriteria(model.getSelectedEqualSign());
    }
}
