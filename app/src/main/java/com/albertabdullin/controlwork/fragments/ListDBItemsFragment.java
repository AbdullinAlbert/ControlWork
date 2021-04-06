package com.albertabdullin.controlwork.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.albertabdullin.controlwork.customView.SearchEditText;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;
import com.albertabdullin.controlwork.recycler_views.RecyclerViewObserver;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ListDBItemsFragment  extends Fragment implements RecyclerViewObserver, BackPressListener {

    private EditDeleteDataVM mViewModel;
    private EditText searchEditText;

    private final View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && !mViewModel.isStateMenuItemSearchTextActive()) {
                mViewModel.setStateMenuItemSearchText(true);
                InputMethodManager imm = (InputMethodManager)
                        requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, 0);
            } else if (!hasFocus) {
                hideKeyBoard((EditText) v);
            }
        }
    };

    private final TextWatcher textWatcherForSearchEditText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String newText = s.toString();
            if (newText.equals("")) mViewModel.sayToStopSearch(before);
            else if (mViewModel.isSearchIsActive()) mViewModel.sendNewText(newText);
            else mViewModel.startSearchInResultTable(newText);
        }
        @Override
        public void afterTextChanged(Editable s) {  }
    };

    @Override
    public void onClick(SimpleEntityForDB eDB) {
        helperMethodForCloseFragment(eDB);
    }

    public enum TableNameForList {
        EMPLOYEES, FIRMS, POW, TOW, RESULT_TYPES
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
        final Toolbar toolbar = view.findViewById(R.id.toolbar_for_db_items_list);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> helperMethodForCloseFragment(null));
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
            case RESULT_TYPES:
                toolbar.setTitle(R.string.list_of_types_of_result);
                break;
        }
        toolbar.inflateMenu(R.menu.stable_appbar_list_items);
        searchEditText = ((SearchEditText) toolbar.getMenu().getItem(0).getActionView()).getTextView();
        searchEditText.setOnFocusChangeListener(focusChangeListener);
        searchEditText.addTextChangedListener(textWatcherForSearchEditText);
        toolbar.getMenu().getItem(0).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    mViewModel.setStateMenuItemSearchText(false);
                    searchEditText.requestFocus();
                    return true;
                } else return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    searchEditText.setText("");
                    mViewModel.closeSearchThread();
                    return true;
                } else return false;
            }
        });

        final ProgressBar progressBar = view.findViewById(R.id.progressBar_for_list_of_primary_table);
        Observer<Integer> observerOfProgressBarVisible = progressBar::setVisibility;
        mViewModel.getVisibleOfProgressBarForPrimaryTableListLD().observe(getViewLifecycleOwner(), observerOfProgressBarVisible);
        final AdapterForItemsFromDB adapter = new AdapterForItemsFromDB(mViewModel.getCurrentListForPrimaryTable());
        adapter.setRVObserver(this);
        Observer<DeleteDataFragment.StateOfRecyclerView> observerOfRecyclerViewState = stateOfRecyclerView -> {
            if (stateOfRecyclerView == DeleteDataFragment.StateOfRecyclerView.LOAD) {
                adapter.notifyDataSetChanged();
            }
        };
        mViewModel.getStateOfRVForPrimaryTableLD().observe(getViewLifecycleOwner(), observerOfRecyclerViewState);
        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView_for_list_of_primary_table);
        Observer<Integer> observerOfRecyclerViewVisible = recyclerView::setVisibility;
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
        mViewModel.setBlankCallTrue();
        ((NotifierOfBackPressed)requireActivity()).removeListener();
    }

    @Override
    public void OnBackPress() {
        mViewModel.tryToStopLoadDataFromPrimaryTableThread();
        mViewModel.setDefaultValuesToListDBItemsFragmentViews();
        mViewModel.tryToChangeStateOfSaveChangedDataButton();
    }

    private void hideKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager)
                requireActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
