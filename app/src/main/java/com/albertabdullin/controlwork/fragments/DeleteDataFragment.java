package com.albertabdullin.controlwork.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForResultListFromQuery;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.AMControllerForResultListItemsFromDB;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.DBResultListItemLookup;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.ItemFromResultListKeyProvider;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

public class DeleteDataFragment extends Fragment {

    private SelectionTracker<ComplexEntityForDB> mTracker;
    private ActionMode mActionMode;
    private AdapterForResultListFromQuery mAdapter;
    private EditDeleteDataVM mViewModel;

    private final SelectionTracker.SelectionObserver<ComplexEntityForDB> selectionObserver =
            new SelectionTracker.SelectionObserver<ComplexEntityForDB>() {
                @Override
                public void onSelectionChanged() {
                    if (mTracker.hasSelection() && mActionMode == null) {
                        mActionMode = ((AppCompatActivity)requireActivity())
                                .startSupportActionMode(new AMControllerForResultListItemsFromDB(mTracker, mAdapter,
                                        (AppCompatActivity)requireActivity()));
                        setSelectedTitle(mTracker.getSelection().size());
                    } else if (!mTracker.hasSelection() && mActionMode != null) {
                        mActionMode.finish();
                        mActionMode = null;
                    } else setSelectedTitle(mTracker.getSelection().size());
                }

                private void setSelectedTitle(int size) {
                    if(mActionMode != null) mActionMode.setTitle(Integer.toString(size));
                }
            };

    public enum StateOfRecyclerView {
        LOAD, DELETE, UPDATE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete_data_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.setQuery(requireActivity().getIntent().getStringExtra(SearchCriteriaFragment.KEY_FOR_QUERY));
        Toolbar toolbar = view.findViewById(R.id.toolbar_for_delete_data);
        toolbar.inflateMenu(R.menu.stable_appbar_result_list_items);
        final MenuItem menuItem = toolbar.getMenu().getItem(0);
        menuItem.setVisible(false);
        Observer<Boolean> observerOfEditMenuItem = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                menuItem.setVisible(aBoolean);
            }
        };
        mViewModel.getVisibleOfEditMenuItem().observe(getViewLifecycleOwner(), observerOfEditMenuItem);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_edit) {
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    EditDataFragment deleteDataFragment = new EditDataFragment();
                    transaction.replace(R.id.container_for_edit_delete_data_fragment, deleteDataFragment,
                            getResources().getString(R.string.tag_for_edit_data_fragment)).
                            addToBackStack(null).commit();
                    return true;
                }
                return false;
            }
        });
        toolbar.setTitle(R.string.title_for_delete_data_fragment_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { requireActivity().onBackPressed();
            }
        });
        final EditText employeeEditText = view.findViewById(R.id.editText_for_employer);
        final EditText firmEditText = view.findViewById(R.id.editText_for_firm);
        final EditText typeOfWorkEditText = view.findViewById(R.id.editText_for_type_of_work);
        final EditText placeOfWorkEditText = view.findViewById(R.id.editText_for_place_of_work);
        Observer<String> observerOfEmployeeET = new Observer<String>() {
            @Override
            public void onChanged(String s) { employeeEditText.setText(s);
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
            public void onChanged(String s) { typeOfWorkEditText.setText(s);
            }
        };
        Observer<String> observerOfPoWET = new Observer<String>() {
            @Override
            public void onChanged(String s) { placeOfWorkEditText.setText(s);
            }
        };
        mViewModel.getEmployeeEditTextLD().observe(getViewLifecycleOwner(), observerOfEmployeeET);
        mViewModel.getFirmEditTextLD().observe(getViewLifecycleOwner(), observerOfFirmET);
        mViewModel.getTOWEditTextLD().observe(getViewLifecycleOwner(), observerOfToWET);
        mViewModel.getPOWEditTextLD().observe(getViewLifecycleOwner(), observerOfPoWET);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        Observer<Integer> observerOfProgressBar = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) { progressBar.setVisibility(integer); }
        };
        mViewModel.getVisibleOfProgressBarLD().observe(getViewLifecycleOwner(), observerOfProgressBar);
        mAdapter = new AdapterForResultListFromQuery(mViewModel.getResultList(), mViewModel, getViewLifecycleOwner(), this);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView3);
        recyclerView.setVisibility(View.INVISIBLE);
        Observer<Integer> observerOFVisibleRecyclerView = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                recyclerView.setVisibility(integer);
            }
        };
        mViewModel.getVisibleOfRecyclerViewLD().observe(getViewLifecycleOwner(), observerOFVisibleRecyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);
        Observer<DeleteDataFragment.StateOfRecyclerView> observerOfStateOfRV = new Observer<DeleteDataFragment.StateOfRecyclerView>() {
            @Override
            public void onChanged(DeleteDataFragment.StateOfRecyclerView stateOfRecyclerView) {
                switch (stateOfRecyclerView) {
                    case LOAD:
                        mAdapter.initializeArrayOfViews();
                        mAdapter.notifyDataSetChanged();
                        break;
                    case DELETE:
                        for (int i: mViewModel.getDeletedPositionsFromDB()) mAdapter.notifyItemRemoved(i);
                        break;
                }
            }
        };
        mViewModel.getStateOfRecyclerViewLD().observe(getViewLifecycleOwner(), observerOfStateOfRV);
        mViewModel.initializeResultList();
        mTracker = new SelectionTracker.Builder<>(
                "resultListItems",
                recyclerView,
                new ItemFromResultListKeyProvider(mAdapter),
                new DBResultListItemLookup(recyclerView),
                StorageStrategy.createParcelableStorage(ComplexEntityForDB.class)
        ).build();
        mAdapter.setSelectionTracker(mTracker);
        mTracker.addObserver(selectionObserver);
        if (savedInstanceState != null)
            mTracker.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTracker != null) mTracker.onSaveInstanceState(outState);
        mViewModel.setNullToOldItemPosition();
    }

    public SelectionTracker<ComplexEntityForDB> getSelectionTracker() {
        return mTracker;
    }

}
