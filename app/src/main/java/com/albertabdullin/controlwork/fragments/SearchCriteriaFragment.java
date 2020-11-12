package com.albertabdullin.controlwork.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.google.android.material.datepicker.MaterialDatePicker;


public class SearchCriteriaFragment extends Fragment implements DFPickerObserver {
    public static final int SELECT_EMPLOYEES = 0;
    public static final int SELECT_FIRMS = 1;
    public static final int SELECT_TYPES = 2;
    public static final int SELECT_PLACES = 3;

    private EditText selectedDate;
    private static EditDeleteDataVM model;

    View.OnClickListener callPickerEmployersDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_EMPLOYEES);
            pickerItemsDF.show(requireActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerFirmsDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_FIRMS);
            pickerItemsDF.show(requireActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerTOWDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_TYPES);
            pickerItemsDF.show(requireActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerPOWDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_PLACES);
            pickerItemsDF.show(requireActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerSignDF = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PickerSignsDF pickerSignsDF = new PickerSignsDF();
            pickerSignsDF.setDFSignPickerObserver(SearchCriteriaFragment.this);
            pickerSignsDF.show(requireActivity().getSupportFragmentManager(), "pickSign");
        }
    };

    View.OnClickListener callPickOneDateDF = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_criteria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar_search_criteria);
        toolbar.setTitle("Критерии поиска");
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        final EditText selectedEmployersET = view.findViewById(R.id.select_empl_editText);
        selectedEmployersET.setOnClickListener(callPickerEmployersDF);
        Observer<String> editTextEmployeesObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedEmployersET.setText("");
                selectedEmployersET.setText(s);
            }
        };
        model.getEmployeesEditTextLD().observe(getViewLifecycleOwner(), editTextEmployeesObserver);
        final EditText selectedFirmsET = view.findViewById(R.id.select_firm_editText);
        selectedFirmsET.setOnClickListener(callPickerFirmsDF);
        Observer<String> editTextFirmsObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedFirmsET.setText("");
                selectedFirmsET.setText(s);
            }
        };
        model.getFirmsEditTextLD().observe(getViewLifecycleOwner(), editTextFirmsObserver);
        final EditText selectedToWET = view.findViewById(R.id.select_typeOfWork_editText);
        selectedToWET.setOnClickListener(callPickerTOWDF);
        Observer<String> editTextToWObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedToWET.setText("");
                selectedToWET.setText(s);
            }
        };
        model.getToWEditTextLD().observe(getViewLifecycleOwner(), editTextToWObserver);
        final EditText selectedPoWET = view.findViewById(R.id.select_placeOfWork_editText);
        selectedPoWET.setOnClickListener(callPickerPOWDF);
        Observer<String> editTextPoWObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedPoWET.setText("");
                selectedPoWET.setText(s);
            }
        };
        model.getPoWEditTextLD().observe(getViewLifecycleOwner(), editTextPoWObserver);
        selectedDate = view.findViewById(R.id.select_data_editText);
        selectedDate.setOnClickListener(callPickerSignDF);
        PickerSignsDF pickerSignsDF = (PickerSignsDF) getActivity().getSupportFragmentManager().findFragmentByTag("pickSign");
        if (pickerSignsDF != null)
            pickerSignsDF.setDFSignPickerObserver(this);
    }

    private void setCalendarToEditText() {
        if (model.getCountOfAddedCriteriaForDate() == 0) {
            selectedDate.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
            selectedDate.setLayoutParams(lp);
        }
        model.incrementCountOfAddedCriteriaForDate();
        switch (model.getCountOfAddedCriteriaForDate()) {
            case 1:
                View view = getView().findViewById(R.id.first_editText_for_date_criteria);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(lp);
                view.setVisibility(View.VISIBLE);
                TextView tv = view.findViewById(R.id.textView_for_equal_sign);
                tv.setText(model.getSelectedEqualSign());
                EditText et = view.findViewById(R.id.editText_filled_date);
                et.setOnClickListener(callPickOneDateDF);
        }
    }

    @Override
    public void changeLayoutForCertainCriteria(String selectedSign) {
        switch (selectedSign) {
            case "\u2a7e":
            case "\u2a7d":
                setCalendarToEditText();
                break;
        }
    }
}
