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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.recycler_views.AdapterForPickIneqaulEqualSign;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;


public class PickerSignsDF extends DialogFragment implements DFPickerObservable {
    private MakerSearchCriteriaVM model;
    private DFPickerObserver mDfPickerObserver;
    private String fromSelectedSign;
    private int selectedTypeOfValue;
    private int positionOnSelectedAction;
    private int selectedAction;
    private boolean selectedItem;
    private static final String SELECTED_SIGN_TAG = "selected sign";
    private static final String SELECTED_POSITION_TAG = "selected position";
    private static final String SELECTED_TYPE_OF_VALUE = "selected type of value";
    public static final int ADD_ITEM = 0;
    public static final int CHANGE_ITEM = 1;
    public static final int DELETE_ITEM = 2;

    public PickerSignsDF() {  }

    public PickerSignsDF(DFPickerObserver dfPickerObserver, int selectedTypeOfValue) {
        mDfPickerObserver = dfPickerObserver;
        this.selectedTypeOfValue = selectedTypeOfValue;
    }

    public PickerSignsDF(String selectedSign, int position, DFPickerObserver dfPickerObserver, int selectedTypeOfValue) {
        this.fromSelectedSign = selectedSign;
        mDfPickerObserver = dfPickerObserver;
        positionOnSelectedAction = position;
        this.selectedTypeOfValue = selectedTypeOfValue;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(MakerSearchCriteriaVM.class);
        if (mDfPickerObserver == null) mDfPickerObserver = (SearchCriteriaFragment) requireActivity().getSupportFragmentManager().findFragmentByTag("edit_delete_fragment");
        if (savedInstanceState != null) {
            fromSelectedSign = savedInstanceState.getString(SELECTED_SIGN_TAG);
            positionOnSelectedAction = savedInstanceState.getInt(SELECTED_POSITION_TAG);
            selectedTypeOfValue = savedInstanceState.getInt(SELECTED_TYPE_OF_VALUE);
        }
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
        return inflater.inflate(R.layout.dialog_fragment_picker_equal_inequal_sign, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.title_for_search_criteria);
        int subTitleString = selectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE ? R.string.results
                : selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE ? R.string.dates : R.string.notes;
        toolbar.setSubtitle(getResources().getString(subTitleString));
        final AdapterForPickIneqaulEqualSign adapter;
        if (fromSelectedSign == null ) adapter = new AdapterForPickIneqaulEqualSign(model, getViewLifecycleOwner(), selectedTypeOfValue, this);
        else adapter = new AdapterForPickIneqaulEqualSign(model, getViewLifecycleOwner(), selectedTypeOfValue, fromSelectedSign, this);
        RecyclerView rv = view.findViewById(R.id.rv_for_selectable_items);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL);
        rv.addItemDecoration(divider);
        Button cancelButton = view.findViewById(R.id.cancel_select_items_for_search);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.clearSelectedEqualSign();
                Dialog dialog = getDialog();
                if (dialog != null) dialog.dismiss();
            }
        });
        Button agreeButton = view.findViewById(R.id.agree_button_for_selected_items);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveSelectedItem()) {
                    model.clearSelectedEqualSign();
                    Dialog dialog = getDialog();
                    if (dialog != null) dialog.dismiss();
                    if (fromSelectedSign == null) selectedAction = ADD_ITEM;
                    else selectedAction = "Удалить критерий".equals(model.getSelectedEqualSignForSelectedTypeOfValue(selectedTypeOfValue)) ? DELETE_ITEM : CHANGE_ITEM;
                    model.notifyAboutTapAddButton(selectedTypeOfValue, selectedAction, positionOnSelectedAction);
                    notifyAboutSelection();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_SIGN_TAG, fromSelectedSign);
        outState.putInt(SELECTED_POSITION_TAG, positionOnSelectedAction);
        outState.putInt(SELECTED_TYPE_OF_VALUE, selectedTypeOfValue);
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.7), (int) (size.y * 0.5));
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    public void notifyAboutSelection() {
        switch (selectedAction) {
            case ADD_ITEM:
                mDfPickerObserver.addViewToLayoutForCertainSearchCriteria(selectedTypeOfValue,
                        model.getSelectedEqualSignForSelectedTypeOfValue(selectedTypeOfValue),
                        model.getPositionOfAddedCriteriaForSelectedTypeOfValue(selectedTypeOfValue));
                break;
            case CHANGE_ITEM:
                mDfPickerObserver.changeLayoutForCertainSearchCriteria(selectedTypeOfValue, positionOnSelectedAction);
                break;
            case DELETE_ITEM:
                mDfPickerObserver.deleteViewFormLayoutForCertainSearchCriteria(selectedTypeOfValue, positionOnSelectedAction);
                break;
            default:
                throw new RuntimeException("опечатка в константах поля selectedAction - " + selectedAction);
            }
    }

    public boolean haveSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem() {
        selectedItem = true;
    }
}
