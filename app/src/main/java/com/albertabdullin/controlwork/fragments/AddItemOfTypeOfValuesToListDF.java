package com.albertabdullin.controlwork.fragments;

import android.app.Dialog;
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
import com.albertabdullin.controlwork.recycler_views.AdapterForListOfTypeOfValues;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;


public class AddItemOfTypeOfValuesToListDF extends DialogFragment {
    private MakerSearchCriteriaVM model;
    private String mSign;
    private int mTypeOfValue;
    private AdapterForListOfTypeOfValues adapter;
    private static final String KEY_FOR_SIGN = "key_for_sign";
    private static final String KEY_FOR_TYPE_OF_VALUE = "key_for_type_of_value";
    public static final int EMPTY_LIST = 0;
    public static final int ADD_ITEM_TO_LIST = 1;
    public static final int DELETE_ITEM_FROM_LIST = 2;
    public static final int UPDATE_ITEM_FROM_LIST = 3;

    public AddItemOfTypeOfValuesToListDF() {}

    public AddItemOfTypeOfValuesToListDF(int typeOfValue, String sign) {
        mSign = sign;
        mTypeOfValue = typeOfValue;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(MakerSearchCriteriaVM.class);
        if (savedInstanceState != null) {
            mSign = savedInstanceState.getString(KEY_FOR_SIGN);
            mTypeOfValue = savedInstanceState.getInt(KEY_FOR_TYPE_OF_VALUE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
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
        if (mTypeOfValue == SearchCriteriaFragment.DATES_VALUE) toolbar.setTitle("Добавь нужные даты");
        else toolbar.setTitle("Добавь нужные значения");
        final ImageView deleteImage = view.findViewById(R.id.imageView_to_delete_item);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteSearchCriteriaDF deleteSearchCriteriaDF = new DeleteSearchCriteriaDF(mTypeOfValue, mSign);
                deleteSearchCriteriaDF.show(requireActivity().getSupportFragmentManager(), "delete_item_df");
            }
        });
        Observer<Boolean> observerOfDeleteImage = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) deleteImage.setVisibility(View.VISIBLE);
                else deleteImage.setVisibility(View.INVISIBLE);
                deleteImage.setClickable(aBoolean);
            }
        };
        final List<String> hList;
        if (mTypeOfValue == SearchCriteriaFragment.DATES_VALUE) hList = model.getAdapterListOfCurrentSignForDate(mSign);
        else if (mTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) hList = model.getAdapterListOfCurrentSignForNumber(mSign);
        else hList = model.getAdapterListOfCurrentSignForNote(mSign);
        adapter = new AdapterForListOfTypeOfValues(mTypeOfValue, hList, model, mSign, requireActivity());
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
                        List<Integer> hList = model.getListOfSelectedPositionForDeleteSign(mTypeOfValue, mSign);
                        if (hList != null) {
                            for (int i = hList.size() - 1; i > -1; i--) {
                                adapter.notifyItemRemoved(hList.get(i));
                            }
                            hList.clear();
                        }
                        break;
                    case UPDATE_ITEM_FROM_LIST:
                        adapter.notifyItemChanged(model.getPositionOfUpdatedItem(mTypeOfValue));
                        break;
                }
            }
        };
        switch (mTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                switch (mSign) {
                    case "=":
                        model.getDeleteImageViewOnDialogEqualitySignDateLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfOneDateForEqualitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case "\u2260":
                        model.getDeleteImageViewOnDialogInequalitySignDateLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfOneDateForInequalitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case ("\u2a7e" + " " + "\u2a7d"):
                        model.getDeleteImageViewOnDialogMoreAndLessSignsDateLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfRangeOfDatesForMoreAndLessSignsLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                }
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                switch (mSign) {
                    case "=":
                        model.getDeleteImageViewOnDialogEqualitySignNumberLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfOneNumberForEqualitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case "\u2260":
                        model.getDeleteImageViewOnDialogInequalitySignNumberLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfOneNumberForInequalitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case ("\u2a7e" + " " + "\u2a7d"):
                        model.getDeleteImageViewOnDialogMoreAndLessSignsNumberLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfRangeOfNumbersForMoreAndLessSignsLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                }
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                switch (mSign) {
                    case "=":
                        model.getDeleteImageViewOnDialogEqualitySignNoteLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfNoteForEqualitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                    case "\u2260":
                        model.getDeleteImageViewOnDialogInequalitySignNoteLD().observe(getViewLifecycleOwner(), observerOfDeleteImage);
                        model.getAdapterListOfNoteForInequalitySignLD().observe(getViewLifecycleOwner(), adapterListObserver);
                        break;
                }
                break;

        }
        FloatingActionButton fab = view.findViewById(R.id.fab_to_add_one_date);
        switch (mTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
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
                                    String beginOfRangeDate = SearchCriteriaFragment.getStringViewOfDate(calendar);
                                    calendar.setTimeInMillis(selection.second);
                                    String endOfRangeDate = SearchCriteriaFragment.getStringViewOfDate(calendar);
                                    model.addItemToDateList(mSign, beginOfRangeDate, endOfRangeDate);
                                    model.addSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                                            model.getPositionOfSign(mTypeOfValue, mSign), selection.first, selection.second);
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
                                String date = SearchCriteriaFragment.getStringViewOfDate(calendar);
                                model.addItemToDateList(mSign, date, null);
                                model.addSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                                        model.getPositionOfSign(mTypeOfValue, mSign), selection, null);
                            }
                        });
                        materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
                    }
                });
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                if (("\u2a7e" + " " + "\u2a7d").equals(mSign))
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddItemOfPairOfNumbersValueDF addItemOfPairOfNumbersValueDF = new AddItemOfPairOfNumbersValueDF(mSign, null);
                            addItemOfPairOfNumbersValueDF.show(requireActivity().getSupportFragmentManager(), "add number value");
                        }
                    });
                else fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddItemOfNumberValueDF addItemOfNumberValueDF = new AddItemOfNumberValueDF(mSign, null);
                        addItemOfNumberValueDF.show(requireActivity().getSupportFragmentManager(), "add number value");
                    }
                });
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddItemOfNoteValueDF addItemOfNoteValueDF = new AddItemOfNoteValueDF(mSign, null);
                        addItemOfNoteValueDF.show(requireActivity().getSupportFragmentManager(), "add note value");
                    }
                });
                break;
        }

        Button oKButton = view.findViewById(R.id.ok_button);
        oKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireDialog().dismiss();
                switch (mTypeOfValue) {
                    case SearchCriteriaFragment.DATES_VALUE:
                        String stringViewOfDate = model.createStringViewOfDate(mSign);
                        model.setSelectedSignAndStringViewOfDate(mSign, stringViewOfDate);
                        break;
                    case SearchCriteriaFragment.NUMBERS_VALUE:
                        String stringViewOfNumber = model.createStringViewOfNumber(mSign);
                        model.setSelectedSignAndStringViewOfNumber(mSign, stringViewOfNumber);
                        break;
                    case SearchCriteriaFragment.NOTES_VALUE:
                        String stringViewOfNote = model.createStringViewOfNote(mSign);
                        model.setSelectedSignAndStringViewOfNote(mSign, stringViewOfNote);
                        break;
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FOR_SIGN, mSign);
        outState.putInt(KEY_FOR_TYPE_OF_VALUE, mTypeOfValue);
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
