package com.albertabdullin.controlwork.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.EditDeleteDataActivity;
import com.albertabdullin.controlwork.activities.SearchCriteriaVMProvider;
import com.albertabdullin.controlwork.models.DateConverter;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;
import com.google.android.material.datepicker.MaterialDatePicker;


import java.util.Calendar;


public class SearchCriteriaFragment extends Fragment implements DFPickerObserver {
    public static final int SELECT_EMPLOYEES = 0;
    public static final int SELECT_FIRMS = 1;
    public static final int SELECT_TYPES = 2;
    public static final int SELECT_PLACES = 3;
    public static final int SELECT_RESULT_TYPES = 7;

    public static final int NUMBERS_VALUE = 4;
    public static final int DATES_VALUE = 5;
    public static final int NOTES_VALUE = 6;

    public static final String KEY_FOR_QUERY = "key for query";

    protected EditText selectedDateEditText;
    private EditText selectedNumberEditText;
    private EditText selectedNoteEditText;
    private Button addCriteriaForDateButton;
    private Button addCriteriaForNumberButton;
    private Button addCriteriaForNoteButton;
    protected MakerSearchCriteriaVM mViewModel;
    private ViewGroup innerLinearLayout;

    private View[] viewsForDates;
    private View[] viewsForNumbers;
    private View[] viewsForNotes;

    private final View.OnClickListener callPickerEmployersDF = v -> openPickerItems(SELECT_EMPLOYEES);

    private final View.OnClickListener callPickerFirmsDF = v -> openPickerItems(SELECT_FIRMS);

    private final View.OnClickListener callPickerTOWDF = v -> openPickerItems(SELECT_TYPES);

    private final View.OnClickListener callPickerPOWDF = v -> openPickerItems(SELECT_PLACES);

    private final View.OnClickListener callPickerResultTypesDF = v -> openPickerItems(SELECT_RESULT_TYPES);

    protected void openPickerItems(int selectedTable) {
        PickerItemsDF pickerItemsDF = new PickerItemsDF(selectedTable);
        pickerItemsDF.show(requireActivity().getSupportFragmentManager(), "newData");
    }

    View.OnClickListener callPickerSignForDateDF = v -> openPickerSigns(DATES_VALUE);

    View.OnClickListener callPickerSignForNumberDF = v -> openPickerSigns(NUMBERS_VALUE);

    View.OnClickListener callPickerSignForNoteDF = v -> openPickerSigns(NOTES_VALUE);

    protected void openPickerSigns(int selectedValue) {
        PickerSignsDF pickerSignsDF = new PickerSignsDF(SearchCriteriaFragment.this, selectedValue);
        pickerSignsDF.show(requireActivity().getSupportFragmentManager(), "pickSign");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_criteria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = ((SearchCriteriaVMProvider)requireActivity()).getMakerSearchCriteriaVM();
        Toolbar toolbar = view.findViewById(R.id.toolbar_search_criteria);
        setTitleForToolBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        final EditText selectedEmployersET = view.findViewById(R.id.select_empl_editText);
        selectedEmployersET.setOnClickListener(callPickerEmployersDF);
        Observer<String> editTextEmployeesObserver =  s -> {
            selectedEmployersET.setText("");
            selectedEmployersET.setText(s);
        };
        mViewModel.getEmployeesEditTextLD().observe(getViewLifecycleOwner(), editTextEmployeesObserver);
        final EditText selectedFirmsET = view.findViewById(R.id.select_firm_editText);
        selectedFirmsET.setOnClickListener(callPickerFirmsDF);
        Observer<String> editTextFirmsObserver = s -> {
            selectedFirmsET.setText("");
            selectedFirmsET.setText(s);
        };
        mViewModel.getFirmsEditTextLD().observe(getViewLifecycleOwner(), editTextFirmsObserver);
        final EditText selectedToWET = view.findViewById(R.id.select_typeOfWork_editText);
        selectedToWET.setOnClickListener(callPickerTOWDF);
        Observer<String> editTextToWObserver = s -> {
            selectedToWET.setText("");
            selectedToWET.setText(s);
        };
        mViewModel.getToWEditTextLD().observe(getViewLifecycleOwner(), editTextToWObserver);
        final EditText selectedPoWET = view.findViewById(R.id.select_placeOfWork_editText);
        selectedPoWET.setOnClickListener(callPickerPOWDF);
        Observer<String> editTextPoWObserver = s -> {
            selectedPoWET.setText("");
            selectedPoWET.setText(s);
        };
        mViewModel.getPoWEditTextLD().observe(getViewLifecycleOwner(), editTextPoWObserver);
        final EditText selectedResultTypes = view.findViewById(R.id.select_result_type_editText);
        selectedResultTypes.setOnClickListener(callPickerResultTypesDF);
        Observer<String> editTextResultType = s -> {
            selectedResultTypes.setText("");
            selectedResultTypes.setText(s);
        };
        mViewModel.getResultTypeEditTextLD().observe(getViewLifecycleOwner(), editTextResultType);
        selectedDateEditText = view.findViewById(R.id.add_criteria_for_data_editText);
        setSearchCriteriaForValuesWithoutTable(selectedDateEditText, callPickerSignForDateDF, DATES_VALUE);
        selectedNumberEditText = view.findViewById(R.id.add_criteria_for_result_editText);
        setSearchCriteriaForValuesWithoutTable(selectedNumberEditText, callPickerSignForNumberDF, NUMBERS_VALUE);
        selectedNoteEditText = view.findViewById(R.id.add_criteria_for_note_edit_text);
        setSearchCriteriaForValuesWithoutTable(selectedNoteEditText, callPickerSignForNoteDF, NOTES_VALUE);
        Button searchButton = view.findViewById(R.id.search_button);
        setTextToSearchButton(searchButton);
        searchButton.setOnClickListener(v -> startViewForResult(mViewModel.createQuery()));
    }

    protected void setTitleForToolBar(Toolbar toolbar) {
        toolbar.setTitle(R.string.search_criteria);
    }

    protected void startViewForResult(String query) {
        Intent intent = new Intent(requireActivity(), EditDeleteDataActivity.class);
        intent.putExtra(KEY_FOR_QUERY, query);
        startActivity(intent);
    }

    private void setSearchCriteriaForValuesWithoutTable(EditText et, View.OnClickListener listener, int typeOfValue) {
        et.setOnClickListener(listener);
        if (mViewModel.getCountOfAddedCriteria(typeOfValue) != 0) {
            for (int i = 0; i < mViewModel.getCountOfAddedCriteria(typeOfValue); i++)
                addViewToLayoutForCertainSearchCriteria(typeOfValue, mViewModel.getSelectedEqualSignFromList(typeOfValue, i), i);
        }
    }

    protected void setTextToSearchButton(Button button) {
        button.setText(R.string.search);
    }

    private void setOneDateCalendarToEditText(final EditText et, final TextView tv) {
        String sign = tv.getText().toString();
        if ("\u2a7e".equals(sign)) {
            Observer<String> observerEditTextOfDateMoreSign = et::setText;
            mViewModel.getStringViewOfDateMoreSignLD().observe(getViewLifecycleOwner(), observerEditTextOfDateMoreSign);
        } else {
            Observer<String> observerEditTextOfDateLessSign = et::setText;
            mViewModel.getStringViewOfDateLessSignLD().observe(getViewLifecycleOwner(), observerEditTextOfDateLessSign);
        }
        et.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            Long selection = mViewModel.getSelection(tv.getText().toString(), 0);
            if (selection != null) builder.setSelection(selection);
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.addOnPositiveButtonClickListener(selection1 -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection1);
                String date = DateConverter.getStringViewOfDate(calendar);
                mViewModel.setSelectedSignAndStringViewOfDate(tv.getText().toString(), date);
                mViewModel.addSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                        mViewModel.getPositionOfSign(DATES_VALUE, tv.getText().toString()), selection1, null);
            });
            materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
        });
    }

    private void prepareEditTextToDisplayAndPreserveNumber(EditText editText, final TextView textView, final int position) {
        editText.setId(View.generateViewId());
        final String key = textView.getText().toString();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String stringViewOfNumber = s.toString();
                if (stringViewOfNumber.length() > 0) {
                    Float floatViewOfNumber = Float.parseFloat(stringViewOfNumber);
                    mViewModel.setSelectedSignAndStringViewOfNumber(key, stringViewOfNumber);
                    mViewModel.addSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE,
                            position, floatViewOfNumber, null);
                } else {
                    mViewModel.deleteStringViewOfNumber(key);
                    mViewModel.deleteSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE, key);
                }
            }
        };

        editText.addTextChangedListener(textWatcher);
    }

    private void setDialogFragmentWithListOfValuesToEditText(final int selectedTypeOfValue, final EditText et, final TextView tv) {
        String sign = tv.getText().toString();
        Observer<String> observerEditText = et::setText;
        switch (sign) {
            case "=":
                if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) mViewModel.getStringViewOfDateEqualitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                else if (selectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) mViewModel.getStringViewOfNumberEqualitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                else mViewModel.getStringViewOfNoteEqualitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                break;
            case "\u2260":
                if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) mViewModel.getStringViewOfDateInequalitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                else if (selectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) mViewModel.getStringViewOfNumberInequalitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                else mViewModel.getStringViewOfNoteInequalitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) mViewModel.getStringViewOfDateMoreAndLessSignsLD().observe(getViewLifecycleOwner(), observerEditText);
                else mViewModel.getStringViewOfNumberMoreAndLessSignsLD().observe(getViewLifecycleOwner(), observerEditText);
                break;
        }
        if (selectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) {
            et.setClickable(false);
            et.setFocusable(false);
        }
        et.setOnClickListener(v -> {
            mViewModel.setCommonSelectedSign(sign);
            AddItemOfTypeOfValuesToListDF addItemOfTypeOfValuesToListDF = new AddItemOfTypeOfValuesToListDF(selectedTypeOfValue, tv.getText().toString());
            addItemOfTypeOfValuesToListDF.show(requireActivity().getSupportFragmentManager(), "DialogFragmentWithOneDateCalendar");
        });
    }

    @Override
    public void addViewToLayoutForCertainSearchCriteria(final int selectedTypeOfValue, String selectedSign, int positionOfView) {
        LinearLayout.LayoutParams lp;
        Button helperButton;
        EditText helperEditText;
        View[] helperArrayOfView;
        View helperView;
        if (innerLinearLayout == null) innerLinearLayout = getView().findViewById(R.id.add_certain_criteria_ll);
        switch (selectedTypeOfValue) {
            case DATES_VALUE:
                if (addCriteriaForDateButton == null)
                    addCriteriaForDateButton = getView().findViewById(R.id.addCriteriaForDateButton);
                helperEditText = selectedDateEditText;
                helperButton = addCriteriaForDateButton;
                if (viewsForDates == null)
                    viewsForDates = new View[5];
                helperArrayOfView = viewsForDates;
                helperView = requireActivity().getLayoutInflater()
                        .inflate(R.layout.layout_exactly_criteria_to_compare_values, (ViewGroup) getView(), false);
                break;
            case NUMBERS_VALUE:
                if (addCriteriaForNumberButton == null)
                    addCriteriaForNumberButton = getView().findViewById(R.id.addCriteriaForNumberButton);
                helperEditText = selectedNumberEditText;
                helperButton = addCriteriaForNumberButton;
                if (viewsForNumbers == null) viewsForNumbers = new View[5];
                helperArrayOfView = viewsForNumbers;
                helperView = requireActivity().getLayoutInflater()
                        .inflate(R.layout.layout_exactly_criteria_to_compare_number_values, (ViewGroup) getView(), false);
                break;
            case NOTES_VALUE:
                if (addCriteriaForNoteButton == null)
                    addCriteriaForNoteButton = getView().findViewById(R.id.addCriteriaForNoteButton);
                helperButton = addCriteriaForNoteButton;
                helperEditText = selectedNoteEditText;
                if (viewsForNotes == null) viewsForNotes = new View[2];
                helperArrayOfView = viewsForNotes;
                helperView = requireActivity().getLayoutInflater()
                        .inflate(R.layout.layout_exactly_criteria_to_compare_values, (ViewGroup) getView(), false);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. Метод " +
                        "void addViewToLayoutForCertainSearchCriteria(int selectedTypeOfValue, String selectedSign, int positionOfView). selectedTypeOfValue - " + selectedTypeOfValue);
        }
        if (positionOfView == 0) {
            lp = new LinearLayout.LayoutParams(0, 0);
            helperEditText.setLayoutParams(lp);
            helperEditText.setVisibility(View.GONE);
        }
        helperArrayOfView[positionOfView] = helperView;
        if (positionOfView == 0) innerLinearLayout.addView(helperArrayOfView[positionOfView], innerLinearLayout.indexOfChild(helperEditText));
        else innerLinearLayout.addView(helperArrayOfView[positionOfView], innerLinearLayout.indexOfChild(helperArrayOfView[positionOfView-1]) + 1);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        helperArrayOfView[positionOfView].setLayoutParams(lp);
        helperArrayOfView[positionOfView].setVisibility(View.VISIBLE);
        final TextView tv = helperArrayOfView[positionOfView].findViewById(R.id.textView_for_equal_sign);
        final String currentSign = mViewModel.getSelectedEqualSignFromList(selectedTypeOfValue, positionOfView);
        tv.setText(currentSign);
        tv.setOnClickListener(v -> {
            String currentSign1 = tv.getText().toString();
            int positionOfSign = mViewModel.getPositionOfSign(selectedTypeOfValue, currentSign1);
            PickerSignsDF pickerSignsDF;
            if (selectedTypeOfValue == DATES_VALUE)  pickerSignsDF = new PickerSignsDF(currentSign1, positionOfSign,SearchCriteriaFragment.this, DATES_VALUE);
            else if (selectedTypeOfValue == NUMBERS_VALUE) pickerSignsDF = new PickerSignsDF(currentSign1, positionOfSign,SearchCriteriaFragment.this, NUMBERS_VALUE);
            else pickerSignsDF = new PickerSignsDF(currentSign1, positionOfSign,SearchCriteriaFragment.this, NOTES_VALUE);
            pickerSignsDF.show(requireActivity().getSupportFragmentManager(), "pickerSign");
        });
        final EditText et = helperArrayOfView[positionOfView].findViewById(R.id.editText_filled_value);
        if ("=".equals(currentSign) || "\u2260".equals(currentSign)) {
            if (selectedTypeOfValue == DATES_VALUE) et.setHint("Выбери дату или даты");
            else et.setHint("Введи одно или несколько значений");
        }
        else if (("\u2a7e" + " " + "\u2a7d").equals(currentSign)) {
            if (selectedTypeOfValue == DATES_VALUE) et.setHint("Выбери даты");
            else et.setHint("Введи значения");
        }
        et.setText(mViewModel.getStringViewOfSearchCriteria(selectedTypeOfValue, currentSign));
        if (positionOfView < helperArrayOfView.length - 1)  {
           if (helperButton.getHeight() == 0) showAddButtonForValueCriteria(selectedTypeOfValue);
        } else helperButton.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        switch (selectedSign) {
            case "\u2a7e":
            case "\u2a7d":
                if (selectedTypeOfValue == DATES_VALUE) setOneDateCalendarToEditText(et, tv);
                else prepareEditTextToDisplayAndPreserveNumber(et, tv, positionOfView);
                break;
            default:
                setDialogFragmentWithListOfValuesToEditText(selectedTypeOfValue, et, tv);
        }
    }

    @Override
    public void changeLayoutForCertainSearchCriteria(int selectedTypeOfValue, int position) {
        TextView tv;
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                tv = viewsForDates[position].findViewById(R.id.textView_for_equal_sign);
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                tv = viewsForNumbers[position].findViewById(R.id.textView_for_equal_sign);
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                tv = viewsForNotes[position].findViewById(R.id.textView_for_equal_sign);
                break;
            default:
                throw new RuntimeException("Опечатка в константах. void changeLayoutForCertainSearchCriteria(int selectedTypeOfValue, int position)." +
                        "selectedTypeOfValue -" + selectedTypeOfValue);
        }
        tv.setText(mViewModel.getSelectedEqualSignForSelectedTypeOfValue(selectedTypeOfValue));
    }

    @Override
    public void deleteViewFormLayoutForCertainSearchCriteria(int selectedTypeOfValue, int position) {
        View[] helperViews;
        EditText helperEditText;
        Button helperButton;
        switch (selectedTypeOfValue) {
            case SearchCriteriaFragment.DATES_VALUE:
                helperViews = viewsForDates;
                helperEditText = selectedDateEditText;
                helperButton = addCriteriaForDateButton;
                break;
            case SearchCriteriaFragment.NUMBERS_VALUE:
                helperViews = viewsForNumbers;
                helperEditText = selectedNumberEditText;
                helperButton = addCriteriaForNumberButton;
                break;
            case SearchCriteriaFragment.NOTES_VALUE:
                helperViews = viewsForNotes;
                helperEditText = selectedNoteEditText;
                helperButton = addCriteriaForNoteButton;
                break;
            default:
                throw new RuntimeException("Опечатка в коснтантах. void deleteViewFormLayoutForCertainSearchCriteria(int selectedTypeOfValue, int position). " +
                        "selectedTypeOfValue" + selectedTypeOfValue);
        }
        int lastVisibleView = getLastVisibleView(selectedTypeOfValue);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
        helperViews[position].setLayoutParams(lp);
        for (int i = position; i < lastVisibleView; i++) helperViews[i] = helperViews[i+1];
        helperViews[lastVisibleView] = null;
        if (lastVisibleView == 0) {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            helperEditText.setLayoutParams(lp);
            helperEditText.setVisibility(View.VISIBLE);
            helperButton.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        } else if (helperButton.getHeight() == 0) showAddButtonForValueCriteria(selectedTypeOfValue);
    }

    private void showAddButtonForValueCriteria(int typeOfValue) {
        float density = getContext().getResources().getDisplayMetrics().density;
        int width = Math.round(72 * density);
        int height = Math.round(32 * density);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.gravity = Gravity.END;
        lp.setMarginEnd(Math.round(8 * density));
        Button helperButton;
        View.OnClickListener helperListener;
        switch (typeOfValue) {
            case DATES_VALUE:
                helperButton = addCriteriaForDateButton;
                helperListener = callPickerSignForDateDF;
                break;
            case NUMBERS_VALUE:
                helperButton = addCriteriaForNumberButton;
                helperListener = callPickerSignForNumberDF;
                break;
            case NOTES_VALUE:
                helperButton = addCriteriaForNoteButton;
                helperListener = callPickerSignForNoteDF;
                break;
            default:
                throw new RuntimeException("опечатка в константах. Метод void showAddButtonForValueCriteria(int typeOfValue).  typedOfValue - " + typeOfValue);
        }
        helperButton.setLayoutParams(lp);
        helperButton.setVisibility(View.VISIBLE);
        helperButton.setOnClickListener(helperListener);
    }

    private int getLastVisibleView(int selectedTypeOfValue) {
        View[] hArray;
        if (selectedTypeOfValue == DATES_VALUE) hArray = viewsForDates;
        else if (selectedTypeOfValue == NUMBERS_VALUE) hArray = viewsForNumbers;
        else hArray = viewsForNotes;
        int i = 0;
        while (i < hArray.length) {
            if (hArray[i] == null) return i - 1;
            i++;
        }
        return i - 1;
    }

}
