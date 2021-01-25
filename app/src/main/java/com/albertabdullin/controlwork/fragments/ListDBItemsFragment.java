package com.albertabdullin.controlwork.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.NotifierOfBackPressed;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;
import com.albertabdullin.controlwork.recycler_views.RecyclerViewObserver;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

public class ListDBItemsFragment  extends Fragment implements RecyclerViewObserver, BackPressListener {

    EditDeleteDataVM mViewModel;

    @Override
    public void onClick(SimpleEntityForDB eDB) {
        helperMethodForCloseFragment(eDB);
    }

    public enum TableNameForList {
        EMPLOYEES, FIRMS, POW, TOW;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
        ((NotifierOfBackPressed)requireActivity()).addListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_of_db_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar_for_db_items_list);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            helperMethodForCloseFragment(null);
            }
        });
        switch (mViewModel.getSelectedTable()) {
            case EMPLOYEES:
                toolbar.setTitle(R.string.list_of_employees);
                break;
            case FIRMS:
                toolbar.setTitle(R.string.list_of_firms);
                break;
            case POW:
                toolbar.setTitle(R.string.list_of_places_of_work);
                break;
            case TOW:
                toolbar.setTitle(R.string.list_of_types_of_work);
                break;
        }
        final ProgressBar progressBar = view.findViewById(R.id.progressBar_for_list_of_primary_table);
        Observer<Integer> observerOfProgressBarVisible = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                progressBar.setVisibility(integer);
            }
        };
        mViewModel.getVisibleOfProgressBarForPrimaryTableListLD().observe(getViewLifecycleOwner(), observerOfProgressBarVisible);
        final AdapterForItemsFromDB adapter = new AdapterForItemsFromDB(mViewModel.getCurrentListForPrimaryTable());
        adapter.setRVObserver(this);
        Observer<DeleteDataFragment.StateOfRecyclerView> observerOfRecyclerViewState = new Observer<DeleteDataFragment.StateOfRecyclerView>() {
            @Override
            public void onChanged(DeleteDataFragment.StateOfRecyclerView stateOfRecyclerView) {
                if (stateOfRecyclerView == DeleteDataFragment.StateOfRecyclerView.LOAD) {
                    adapter.notifyDataSetChanged();
                }
            }
        };
        mViewModel.getStateOfRVForPrimaryTableLD().observe(getViewLifecycleOwner(), observerOfRecyclerViewState);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView_for_list_of_primary_table);
        Observer<Integer> observerOfRecyclerViewVisible = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                recyclerView.setVisibility(integer);
            }
        };
        mViewModel.getVisibleOfRVForPrimaryTableLD().observe(getViewLifecycleOwner(), observerOfRecyclerViewVisible);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
    }

    private void helperMethodForCloseFragment(SimpleEntityForDB eDB) {
        mViewModel.setSelectedItemForChangeData(eDB);
        requireActivity().onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((NotifierOfBackPressed)requireActivity()).removeListener();
    }

    @Override
    public void OnBackPress() {
        mViewModel.tryToStopLoadDataFromPrimaryTableThread();
        mViewModel.setDefaultValuesToListDBItemsFragmentViews();
        mViewModel.tryToChangeStateOfSaveChangedDataButton();
    }
}
