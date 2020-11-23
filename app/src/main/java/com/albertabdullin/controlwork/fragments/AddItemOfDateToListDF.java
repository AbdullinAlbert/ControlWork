package com.albertabdullin.controlwork.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.recycler_views.AdapterForListOfDate;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;


public class AddItemOfDateToListDF extends DialogFragment {
    private EditDeleteDataVM model;
    private String mSign;
    private AdapterForListOfDate adapter;
    private static final String KEY_FOR_SIGN = "key_for_sign";
    public static final int EMPTY_LIST = 0;
    public static final int ADD_ITEM_TO_LIST = 1;
    public static final int DELETE_ITEM_FROM_LIST = 2;
    public static final int UPDATE_ITEM_FROM_LIST = 3;

    public AddItemOfDateToListDF() {}

    public AddItemOfDateToListDF(String sign) { mSign = sign; }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
        if (savedInstanceState != null) {
            mSign = savedInstanceState.getString(KEY_FOR_SIGN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_to_add_search_criteria_values_items, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Добавь нужные даты");
        final ImageView deleteImage = view.findViewById(R.id.imageView_to_delete_item);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteSearchCriteriaDF deleteSearchCriteriaDF = new DeleteSearchCriteriaDF(mSign);
                deleteSearchCriteriaDF.show(requireActivity().getSupportFragmentManager(), "delete_item_df");
            }
        });
        Observer<Boolean> observerOfDeleteImage = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == true) deleteImage.setVisibility(View.VISIBLE);
                else deleteImage.setVisibility(View.INVISIBLE);
                deleteImage.setClickable(aBoolean);
            }
        };
        final List<String> hList = model.getAdapterListOfCurrentSignForDate(mSign);
        adapter = new AdapterForListOfDate(hList, model, mSign, requireActivity());
        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setAdapter(adapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(requireActivity());
        rv.setLayoutManager(mLayoutManager);
        DividerItemDecoration divider = new DividerItemDecoration
                (new android.view.ContextThemeWrapper(requireActivity(), R.style.AppTheme), mLayoutManager.getOrientation());
        rv.addItemDecoration(divider);
        Observer<Integer> adapterListObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case EMPTY_LIST:
                        adapter.notifyDataSetChanged();
                        break;
                    case ADD_ITEM_TO_LIST:
                        adapter.notifyItemInserted(hList.size() - 1);
                        break;
                    case DELETE_ITEM_FROM_LIST:
                        List<Integer> hList = model.getListOfSelectedPositionForDelete(mSign);
                        for (int i = 0, j = 0; i < hList.size(); i++, j++) {
                            adapter.notifyItemRemoved(hList.get(i)  - j);
                        }
                        break;
                    case UPDATE_ITEM_FROM_LIST:
                        adapter.notifyItemChanged(model.getPositionOfUpdatedItemFromOneDateList());
                        break;
                }
            }
        };
        switch (mSign) {
            case "=":
                model.getDeleteImageViewOnDialogEqualitySignLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                model.getAdapterListOfOneDateForEqualitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                break;
            case "\u2260":
                model.getDeleteImageViewOnDialogInequalitySignLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                model.getAdapterListOfOneDateForInequalitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                model.getDeleteImageViewOnDialogMoreAndLessSignsLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                model.getAdapterListOfRangeOfDatesForMoreAndLessSignsLD().observe(getViewLifecycleOwner(), adapterListObserver);
                break;
        }
        FloatingActionButton fab = view.findViewById(R.id.fab_to_add_one_date);
        if (("\u2a7e" + " " + "\u2a7d").equals(mSign))
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                    MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
                    materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                        @Override
                        public void onPositiveButtonClick(Pair<Long, Long> selection) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(selection.first);
                            String beginOfRangeDate = SearchCriteriaFragment.convertLongToStringDate(calendar);
                            calendar.setTimeInMillis(selection.second);
                            String endOfRangeDate = SearchCriteriaFragment.convertLongToStringDate(calendar);
                            model.addItemToDateList(mSign, beginOfRangeDate, endOfRangeDate);
                            model.addSearchCriteriaForDate(model.getPositionOfSign(mSign), selection.first, selection.second);
                        }
                    });
                    materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
                }
            });
        else fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                MaterialDatePicker<Long> materialDatePicker = builder.build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection);
                        String date = SearchCriteriaFragment.convertLongToStringDate(calendar);
                        model.addItemToDateList(mSign, date, null);
                        model.addSearchCriteriaForDate(model.getPositionOfSign(mSign), selection, null);
                    }
                });
                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
            }
        });
        Button button = view.findViewById(R.id.ok_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireDialog().dismiss();
                String stringViewOfDate = model.createStringViewOfDate(mSign);
                model.setSelectedSignAndStringViewOfDate(mSign, stringViewOfDate);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FOR_SIGN, mSign);
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), (int) (size.y * 0.80));
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

}
