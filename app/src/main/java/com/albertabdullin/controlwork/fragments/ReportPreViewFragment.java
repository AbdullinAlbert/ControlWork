package com.albertabdullin.controlwork.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ReportActivity;
import com.albertabdullin.controlwork.recycler_views.AdapterForReportResultList;
import com.albertabdullin.controlwork.viewmodels.ReportViewModel;
import com.albertabdullin.controlwork.viewmodels.ViewModelFactoryForReport;

public class ReportPreViewFragment extends Fragment {

    protected ReportViewModel mViewModel;
    private AdapterForReportResultList mAdapter;
    private RecyclerView recyclerView;
    private String mReportQuery;

    public enum StateOfRecyclerView {
        LOAD
    }

    private final String KEY_FOR_QUERY = "key for mQuery";

    public ReportPreViewFragment() {}

    public ReportPreViewFragment(String reportQuery) {
        mReportQuery = reportQuery;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_preview_in_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity(), new ViewModelFactoryForReport(requireActivity().getApplication())).get(ReportViewModel.class);
        if (mReportQuery == null) mReportQuery = savedInstanceState.getString(KEY_FOR_QUERY);
        mViewModel.setQuery(mReportQuery);
        Toolbar toolbar = view.findViewById(R.id.toolbar_for_delete_data);
        toolbar.inflateMenu(R.menu.create_report);
        MenuItem menuItem = toolbar.getMenu().getItem(0);
        menuItem.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_create_report_item) {
                ((ReportActivity)requireActivity()).launchCreatingReport();
                requireActivity().onBackPressed();
                return true;
            }
            return false;
        });
        toolbar.setTitle(R.string.search_result);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
            mViewModel.resetInitializeOfLiveData();
        });
        final EditText employeeEditText = view.findViewById(R.id.editText_for_employer);
        final EditText firmEditText = view.findViewById(R.id.editText_for_firm);
        final EditText noteEditText = view.findViewById(R.id.editText_for_note);
        final TextView textViewForTypeOfWork = view.findViewById(R.id.textView_for_type_of_work);
        Observer<String> observerOfEmployeeET = employeeEditText::setText;
        Observer<String> observerOfFirmET = firmEditText::setText;
        Observer<String> observerOfNoteET = noteEditText::setText;
        mViewModel.getEmployeeEditTextForResultListLD().observe(getViewLifecycleOwner(), observerOfEmployeeET);
        mViewModel.getFirmEditTextForResultListLD().observe(getViewLifecycleOwner(), observerOfFirmET);
        mViewModel.getNoteEditTextLD().observe(getViewLifecycleOwner(), observerOfNoteET);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        Observer<Integer> observerOfProgressBar = progressBar::setVisibility;
        mViewModel.getVisibleOfProgressBarLD().observe(getViewLifecycleOwner(), observerOfProgressBar);
        mAdapter = new AdapterForReportResultList(mViewModel.getResultList(), mViewModel, getViewLifecycleOwner(), this);
        recyclerView = view.findViewById(R.id.recyclerView_for_result_table);
        recyclerView.setVisibility(View.INVISIBLE);
        Observer<Integer> observerOFVisibleRecyclerView = integer -> recyclerView.setVisibility(integer);
        mViewModel.getVisibleOfRecyclerViewLD().observe(getViewLifecycleOwner(), observerOFVisibleRecyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) ->
            textViewForTypeOfWork.setText(mViewModel.getTypeOfWorkDescriptionAtPosition(layoutManager.findFirstVisibleItemPosition())));
        Observer<StateOfRecyclerView> observerOfStateOfRV = stateOfRecyclerView -> {
            if (stateOfRecyclerView == StateOfRecyclerView.LOAD) {
                mAdapter.initializeArrayOfViews();
                mAdapter.notifyDataSetChanged();
            }
        };
        mViewModel.getStateOfRecyclerViewLD().observe(getViewLifecycleOwner(), observerOfStateOfRV);
        mViewModel.initializeResultList();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewModel.setNullToOldItemPosition();
        outState.putString(KEY_FOR_QUERY, mReportQuery);
    }

}
