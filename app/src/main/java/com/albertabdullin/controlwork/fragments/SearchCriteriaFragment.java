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
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.EditDeleteDataActivity;
import com.albertabdullin.controlwork.activities.MakerSearchCriteriaActivity;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;


public class SearchCriteriaFragment extends Fragment implements DFPickerObserver {
    public static final int SELECT_EMPLOYEES = 0;
    public static final int SELECT_FIRMS = 1;
    public static final int SELECT_TYPES = 2;
    public static final int SELECT_PLACES = 3;

    public static final int NUMBERS_VALUE = 4;
    public static final int DATES_VALUE = 5;
    public static final int NOTES_VALUE = 6;

    public static final String KEY_FOR_QUERY = "key for query";

    private EditText selectedDateEditText;
    private EditText selectedNumberEditText;
    private EditText selectedNoteEditText;
    private Button addCriteriaForDateButton;
    private Button addCriteriaForNumberButton;
    private Button addCriteriaForNoteButton;
    private static MakerSearchCriteriaVM model;
    private ViewGroup innerLinearLayout;

    private View[] viewsForDates;
    private View[] viewsForNumbers;
    private View[] viewsForNotes;

    View.OnClickListener callPickerEmployersDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_EMPLOYEES);
            pickerItemsDF.show(requireActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerFirmsDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_FIRMS);
            pickerItemsDF.show(requireActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerTOWDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_TYPES);
            pickerItemsDF.show(requireActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerPOWDF = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PickerItemsDF pickerItemsDF = new PickerItemsDF(SELECT_PLACES);
            pickerItemsDF.show(requireActivity().getSupportFragmentManager(), "newData");
        }
    };

    View.OnClickListener callPickerSignForDateDF = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PickerSignsDF pickerSignsDF = new PickerSignsDF(SearchCriteriaFragment.this, DATES_VALUE);
            pickerSignsDF.show(requireActivity().getSupportFragmentManager(), "pickSign");
        }
    };

    View.OnClickListener callPickerSignForNumberDF = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PickerSignsDF pickerSignsDF = new PickerSignsDF(SearchCriteriaFragment.this, NUMBERS_VALUE);
            pickerSignsDF.show(requireActivity().getSupportFragmentManager(), "pickSign");
        }
    };

    View.OnClickListener callPickerSignForNoteDF = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PickerSignsDF pickerSignsDF = new PickerSignsDF(SearchCriteriaFragment.this, NOTES_VALUE);
            pickerSignsDF.show(requireActivity().getSupportFragmentManager(), "pickSign");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(MakerSearchCriteriaVM.class);
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
                requireActivity().onBackPressed();
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
        selectedDateEditText = view.findViewById(R.id.add_criteria_for_data_editText);
        selectedDateEditText.setOnClickListener(callPickerSignForDateDF);
        if (model.getCountOfAddedCriteria(SearchCriteriaFragment.DATES_VALUE) != 0) {
            for (int i = 0; i < model.getCountOfAddedCriteria(SearchCriteriaFragment.DATES_VALUE); i++)
                addViewToLayoutForCertainSearchCriteria(DATES_VALUE, model.getSelectedEqualSignFromList(DATES_VALUE, i), i);
        }
        selectedNumberEditText = view.findViewById(R.id.add_criteria_for_result_editText);
        selectedNumberEditText.setOnClickListener(callPickerSignForNumberDF);
        if (model.getCountOfAddedCriteria(SearchCriteriaFragment.NUMBERS_VALUE) != 0) {
            for (int i = 0; i < model.getCountOfAddedCriteria(SearchCriteriaFragment.NUMBERS_VALUE); i++)
                addViewToLayoutForCertainSearchCriteria(NUMBERS_VALUE, model.getSelectedEqualSignFromList(NUMBERS_VALUE, i), i);
        }
        selectedNoteEditText = view.findViewById(R.id.add_criteria_for_note_edit_text);
        selectedNoteEditText.setOnClickListener(callPickerSignForNoteDF);
        if (model.getCountOfAddedCriteria(SearchCriteriaFragment.NOTES_VALUE) != 0) {
            for (int i = 0; i < model.getCountOfAddedCriteria(SearchCriteriaFragment.NOTES_VALUE); i++)
                addViewToLayoutForCertainSearchCriteria(NOTES_VALUE, model.getSelectedEqualSignFromList(NOTES_VALUE, i), i);
        }
        Button searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String query = model.getQuery();
                Intent intent = new Intent(requireActivity(), EditDeleteDataActivity.class);
                intent.putExtra(KEY_FOR_QUERY, query);
                startActivity(intent);
            }
        });
    }

    private void setOneDateCalendarToEditText(final EditText et, final TextView tv) {
        String sign = tv.getText().toString();
        if ("\u2a7e".equals(sign)) {
            Observer<String> observerEditTextOfDateMoreSign = new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    et.setText(s);
                }
            };
            model.getStringViewOfDateMoreSignLD().observe(getViewLifecycleOwner(), observerEditTextOfDateMoreSign);
        } else {
            Observer<String> observerEditTextOfDateLessSign = new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    et.setText(s);
                }
            };
            model.getStringViewOfDateLessSignLD().observe(getViewLifecycleOwner(), observerEditTextOfDateLessSign);
        }
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                Long selection = model.getSelection(tv.getText().toString(), 0);
                if (selection != null) builder.setSelection(selection);
                MaterialDatePicker<Long> materialDatePicker = builder.build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection);
                        String date = getStringViewOfDate(calendar);
                        model.setSelectedSignAndStringViewOfDate(tv.getText().toString(), date);
                        model.addSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                                model.getPositionOfSign(DATES_VALUE, tv.getText().toString()), selection, null);
                    }
                });
                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
            }
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
                    model.setSelectedSignAndStringViewOfNumber(key, stringViewOfNumber);
                    model.addSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE,
                            position, floatViewOfNumber, null);
                } else {
                    model.deleteStringViewOfNumber(key);
                    model.deleteSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE, key);
                }
            }
        };

        editText.addTextChangedListener(textWatcher);
    }

    private void setDialogFragmentWithListOfValuesToEditText(final int selectedTypeOfValue, final EditText et, final TextView tv) {
        String sign = tv.getText().toString();
        Observer<String> observerEditText = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                et.setText(s);
            }
        };
        switch (sign) {
            case "=":
                if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) model.getStringViewOfDateEqualitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                else if (selectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) model.getStringViewOfNumberEqualitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                else model.getStringViewOfNoteEqualitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                break;
            case "\u2260":
                if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) model.getStringViewOfDateInequalitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                else if (selectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) model.getStringViewOfNumberInequalitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                else model.getStringViewOfNoteInequalitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                if (selectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE) model.getStringViewOfDateMoreAndLessSignsLD().observe(getViewLifecycleOwner(), observerEditText);
                else model.getStringViewOfNumberMoreAndLessSignsLD().observe(getViewLifecycleOwner(), observerEditText);
                break;
        }
        if (selectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) {
            et.setClickable(false);
            et.setFocusable(false);
        }
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemOfTypeOfValuesToListDF addItemOfTypeOfValuesToListDF = new AddItemOfTypeOfValuesToListDF(selectedTypeOfValue, tv.getText().toString());
                addItemOfTypeOfValuesToListDF.show(requireActivity().getSupportFragmentManager(), "DialogFragmentWithOneDateCalendar");
            }
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
        final String currentSign = model.getSelectedEqualSignFromList(selectedTypeOfValue, positionOfView);
        tv.setText(currentSign);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentSign = tv.getText().toString();
                int positionOfSign = model.getPositionOfSign(selectedTypeOfValue, currentSign);
                PickerSignsDF pickerSignsDF;
                if (selectedTypeOfValue == DATES_VALUE)  pickerSignsDF = new PickerSignsDF(currentSign, positionOfSign,SearchCriteriaFragment.this, DATES_VALUE);
                else if (selectedTypeOfValue == NUMBERS_VALUE) pickerSignsDF = new PickerSignsDF(currentSign, positionOfSign,SearchCriteriaFragment.this, NUMBERS_VALUE);
                else pickerSignsDF = new PickerSignsDF(currentSign, positionOfSign,SearchCriteriaFragment.this, NOTES_VALUE);
                pickerSignsDF.show(requireActivity().getSupportFragmentManager(), "pickerSign");
            }
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
        et.setText(model.getStringViewOfSearchCriteria(selectedTypeOfValue, currentSign));
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
        tv.setText(model.getSelectedEqualSignForSelectedTypeOfValue(selectedTypeOfValue));
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

    public static String getStringViewOfDate(Calendar c) {
        int dayOfMonth, month = 1;
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        month += c.get(Calendar.MONTH);
        StringBuilder sb = new StringBuilder();
        if (dayOfMonth < 10) sb.append("0");
        sb.append(dayOfMonth).append(".");
        if (month < 10) sb.append("0");
        sb.append(month).append(".");
        sb.append(c.get(Calendar.YEAR));
        return sb.toString();
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
