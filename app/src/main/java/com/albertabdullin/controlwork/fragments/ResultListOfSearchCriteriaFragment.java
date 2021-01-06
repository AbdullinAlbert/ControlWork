package com.albertabdullin.controlwork.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.recycler_views.AdapterForResultListFromQuery;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

public class ResultListOfSearchCriteriaFragment extends Fragment {

    private EditDeleteDataVM mViewModel;
    private AdapterForResultListFromQuery adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result_list_of_search_criteria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        Observer<Integer> observerOfProgressBar = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                progressBar.setVisibility(integer);
            }
        };
        mViewModel.getVisibleOfProgressBarLD().observe(getViewLifecycleOwner(), observerOfProgressBar);
        adapter = new AdapterForResultListFromQuery(mViewModel.getResultList(), mViewModel, getViewLifecycleOwner(), requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView3);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        Observer<DeleteDataFragment.StateOfRecyclerView> observerOfStateOfRV = new Observer<DeleteDataFragment.StateOfRecyclerView>() {
            @Override
            public void onChanged(DeleteDataFragment.StateOfRecyclerView stateOfRecyclerView) {
                switch (stateOfRecyclerView) {
                    case LOAD:
                        adapter.initializeArrayOfViews();
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        mViewModel.getStateOfRecyclerViewLD().observe(getViewLifecycleOwner(), observerOfStateOfRV);
        mViewModel.initializeResultList();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewModel.setNullToOldItemPosition();
    }
}
