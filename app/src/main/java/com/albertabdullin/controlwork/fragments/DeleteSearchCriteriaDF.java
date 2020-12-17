package com.albertabdullin.controlwork.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ListOfBDItemsActivity;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeleteSearchCriteriaDF extends DialogFragment {
    private EditDeleteDataVM viewModel;
    private String mSign;
    private int mSelectedTypeOfValue;
    private static final String KEY_OF_SIGN = "key of sign";
    private static final String KEY_OF_SELECTED_TYPE = "key of selected type";

    public DeleteSearchCriteriaDF() {}

    public DeleteSearchCriteriaDF(int selectedTypeOfValue, String sign) {
        mSelectedTypeOfValue = selectedTypeOfValue;
        mSign = sign;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
        if (savedInstanceState != null) mSign = savedInstanceState.getString(KEY_OF_SIGN);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_delete_data, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvSelectSize = view.findViewById(R.id.DeleteDFSelectSize);
        TextView tvQuestion = view.findViewById(R.id.DeleteDFQuestion);
        int count = viewModel.getListOfSelectedPositionForDeleteSign(mSelectedTypeOfValue, mSign).size();
        tvSelectSize.setText("Выбрано записей: " + count);
        if(count == 1) {
            String description;
            int pos = viewModel.getListOfSelectedPositionForDeleteSign(mSelectedTypeOfValue, mSign).get(0);
            if (mSelectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) description = viewModel.getAdapterListOfCurrentSignForDate(mSign).get(pos);
            else if (mSelectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) description = viewModel.getAdapterListOfCurrentSignForNumber(mSign).get(pos);
            else description = viewModel.getAdapterListOfCurrentSignForNote(mSign).get(pos);
            tvQuestion.setText("Вы действительно хотите удалить " + description + "?");
        }
        Button bYES = view.findViewById(R.id.DeleteDFButtonYES);
        Button bNO = view.findViewById(R.id.DeleteDFButtonNO);
        bYES.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSelectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) viewModel.deleteSearchCriteriaValueForDate(mSign);
                else if (mSelectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) viewModel.deleteSearchCriteriaValueForNumber(mSign);
                else viewModel.deleteSearchCriteriaValueForNote(mSign);
                getDialog().dismiss();
            }
        });
        bNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getDialog().dismiss(); }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_OF_SIGN, mSign);
        outState.putInt(KEY_OF_SELECTED_TYPE, mSelectedTypeOfValue);
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }
}
