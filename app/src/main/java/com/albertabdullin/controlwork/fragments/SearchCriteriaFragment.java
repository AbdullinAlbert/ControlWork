package com.albertabdullin.controlwork.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;


public class SearchCriteriaFragment extends Fragment {
    public static final int SELECT_EMPLOYEES = 0;
    public static final int SELECT_FIRMS = 1;
    public static final int SELECT_TYPES = 2;
    public static final int SELECT_PLACES = 3;

    private static EditDeleteDataVM model;

    View.OnClickListener callPickerEmployersDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_EMPLOYEES);
            pickerItemsDF.show(getActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerFirmsDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_FIRMS);
            pickerItemsDF.show(getActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerTOWDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_TYPES);
            pickerItemsDF.show(getActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerPOWDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_PLACES);
            pickerItemsDF.show(getActivity().getSupportFragmentManager(), "newData");
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
    }

}
