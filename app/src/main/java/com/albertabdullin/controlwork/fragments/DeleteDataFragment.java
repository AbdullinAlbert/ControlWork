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
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;

public class DeleteDataFragment extends Fragment {

    public enum StateOfRecyclerView {
        LOAD, ADD, DELETE, UPDATE;
    }

    private EditDeleteDataVM viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
        viewModel.setQuery(requireActivity().getIntent().getStringExtra(SearchCriteriaFragment.KEY_FOR_QUERY));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete_data_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar_for_delete_data);
        toolbar.setTitle(R.string.title_for_delete_data_fragment_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        final EditText employeeEditText = view.findViewById(R.id.editText_for_employer);
        final EditText firmEditText = view.findViewById(R.id.editText_for_firm);
        final EditText typeOfWorkEditText = view.findViewById(R.id.editText_for_type_of_work);
        final EditText placeOfWorkEditText = view.findViewById(R.id.editText_for_place_of_work);
        Observer<String> observerOfEmployeeET = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                employeeEditText.setText(s);
            }
        };
        Observer<String> observerOfFirmET = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                firmEditText.setText(s);
            }
        };
        Observer<String> observerOfToWET = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                typeOfWorkEditText.setText(s);
            }
        };
        Observer<String> observerOfPoWET = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                placeOfWorkEditText.setText(s);
            }
        };
        viewModel.getEmployeeEditTextLD().observe(getViewLifecycleOwner(), observerOfEmployeeET);
        viewModel.getFirmEditTextLD().observe(getViewLifecycleOwner(), observerOfFirmET);
        viewModel.getTOWEditTextLD().observe(getViewLifecycleOwner(), observerOfToWET);
        viewModel.getPOWEditTextLD().observe(getViewLifecycleOwner(), observerOfPoWET);
    }
}
