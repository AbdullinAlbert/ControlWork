package com.albertabdullin.controlwork.fragments;

import android.os.Bundle;
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
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;


public class SearchCriteriaFragment extends Fragment implements DFPickerObserver {
    public static final int SELECT_EMPLOYEES = 0;
    public static final int SELECT_FIRMS = 1;
    public static final int SELECT_TYPES = 2;
    public static final int SELECT_PLACES = 3;

    private EditText selectedDate;
    private Button addCriteriaForDate;
    private static EditDeleteDataVM model;
    private ViewGroup viewGroup;

    private View[] viewsForDate;

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

    View.OnClickListener callPickerSignDF = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PickerSignsDF pickerSignsDF = new PickerSignsDF(SearchCriteriaFragment.this);
            pickerSignsDF.show(requireActivity().getSupportFragmentManager(), "pickSign");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
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
        selectedDate = view.findViewById(R.id.select_data_editText);
        selectedDate.setOnClickListener(callPickerSignDF);
        if (model.getCountOfAddedCriteriaForDate() != 0) {
            for (int i = 0; i < model.getCountOfAddedCriteriaForDate(); i++)
                addViewToLayoutForCertainCriteria(model.getSelectedEqualSignFromList(i), i);
        }
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
                        String date = convertLongToStringDate(calendar);
                        model.setSelectedSignAndStringViewOfDate(tv.getText().toString(), date);
                        model.addSearchCriteriaForDate(model.getPositionOfSign(tv.getText().toString()), selection, null);
                    }
                });
                materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
            }
        });
    }

    private void setDialogFragmentWithListOfDateToEditText(final EditText et, final TextView tv) {
        String sign = tv.getText().toString();
        Observer<String> observerEditText = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                et.setText(s);
            }
        };
        switch (sign) {
            case "=":
                model.getStringViewOfDateEqualitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                break;
            case "\u2260":
                model.getStringViewOfDateInequalitySignLD().observe(getViewLifecycleOwner(), observerEditText);
                break;
            case ("\u2a7e" + " " + "\u2a7d"):
                model.getStringViewOfDateMoreAndLessSignsLD().observe(getViewLifecycleOwner(), observerEditText);
                break;
        }
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemOfDateToListDF addItemOfDateToListDF = new AddItemOfDateToListDF(tv.getText().toString());
                addItemOfDateToListDF.show(requireActivity().getSupportFragmentManager(), "DialogFragmentWithOneDateCalendar");
            }
        });
    }

    @Override
    public void addViewToLayoutForCertainCriteria(String selectedSign, int positionOfView) {
        LinearLayout.LayoutParams lp;
        if (addCriteriaForDate == null) {
            addCriteriaForDate = getView().findViewById(R.id.addCriteriaForDateButton);
            addCriteriaForDate.setOnClickListener(callPickerSignDF);
        }
        if (positionOfView == 0) {
            lp = new LinearLayout.LayoutParams(0, 0);
            selectedDate.setLayoutParams(lp);
            selectedDate.setVisibility(View.GONE);
            viewsForDate = new View[5];
        }
        viewsForDate[positionOfView] = requireActivity().getLayoutInflater()
                .inflate(R.layout.layout_exactly_criteria_to_compare_values, (ViewGroup) getView(), false);
        if (viewGroup == null) viewGroup = getView().findViewById(R.id.add_certain_criteria_ll);
        if (positionOfView != 0) viewGroup.addView(viewsForDate[positionOfView], viewGroup.indexOfChild(viewsForDate[positionOfView-1]) + 1);
        else viewGroup.addView(viewsForDate[positionOfView], viewGroup.indexOfChild(selectedDate));
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        viewsForDate[positionOfView].setLayoutParams(lp);
        viewsForDate[positionOfView].setVisibility(View.VISIBLE);
        final TextView tv = viewsForDate[positionOfView].findViewById(R.id.textView_for_equal_sign);
        final String firstSign = model.getSelectedEqualSignFromList(positionOfView);
        tv.setText(firstSign);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentSign = tv.getText().toString();
                int positionOfSign = model.getPositionOfSign(currentSign);
                PickerSignsDF pickerSignsDF = new PickerSignsDF(currentSign, positionOfSign,SearchCriteriaFragment.this);
                pickerSignsDF.show(requireActivity().getSupportFragmentManager(), "pickerSign");
            }
        });
        final EditText et = viewsForDate[positionOfView].findViewById(R.id.editText_filled_date);
        if ("=".equals(firstSign) || "\u2260".equals(firstSign)) et.setHint("Выбери дату или даты");
        else if (("\u2a7e" + " " + "\u2a7d").equals(firstSign)) et.setHint("Выбери даты");
        et.setText(model.getStringViewOfDate(firstSign));
        if (positionOfView < 4) {
            if (addCriteriaForDate.getHeight() == 0) showAddButtonForDateCriteria();
        } else {
            lp = new LinearLayout.LayoutParams(0, 0);
            addCriteriaForDate.setLayoutParams(lp);
        }
        switch (selectedSign) {
            case "\u2a7e":
            case "\u2a7d":
                setOneDateCalendarToEditText(et, tv);
                break;
            default:
                setDialogFragmentWithListOfDateToEditText(et, tv);
        }
    }

    @Override
    public void changeLayoutForCertainCriteria(int position) {
        TextView tv = viewsForDate[position].findViewById(R.id.textView_for_equal_sign);
        tv.setText(model.getSelectedEqualSign());
    }

    @Override
    public void deleteViewFormLayoutForCertainCriteria(int position) {
        int lastVisibleView = getLastVisibleView();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, 0);
        viewsForDate[position].setLayoutParams(lp);
        for (int i = position; i < lastVisibleView; i++) viewsForDate[i] = viewsForDate[i+1];
        viewsForDate[lastVisibleView] = null;
        if (lastVisibleView == 0) {
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            selectedDate.setLayoutParams(lp);
            selectedDate.setVisibility(View.VISIBLE);
            addCriteriaForDate.setLayoutParams(new LinearLayout.LayoutParams(0,0));
        } else if (addCriteriaForDate.getHeight() == 0) showAddButtonForDateCriteria();
    }

    public static String convertLongToStringDate(Calendar c) {
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

    private void showAddButtonForDateCriteria() {
        float density = getContext().getResources().getDisplayMetrics().density;
        int width = Math.round(72 * density);
        int height = Math.round(32 * density);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.gravity = Gravity.END;
        lp.setMarginEnd(Math.round(8 * density));
        addCriteriaForDate.setLayoutParams(lp);
        addCriteriaForDate.setVisibility(View.VISIBLE);
    }

    private int getLastVisibleView() {
        int i = 0;
        while (i < viewsForDate.length) {
            if (viewsForDate[i] == null) return i - 1;
            i++;
        }
        return i - 1;
    }

}
