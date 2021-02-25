package com.albertabdullin.controlwork.activities;

import androidx.core.util.Pair;

import com.albertabdullin.controlwork.fragments.CommonAddDataDF;
import com.albertabdullin.controlwork.fragments.CommonAddPairOfNumbersValueDF;
import com.albertabdullin.controlwork.fragments.CommonDeleteDataDF;
import com.google.android.material.datepicker.MaterialDatePicker;

public interface DialogFragmentProvider {
    CommonDeleteDataDF getDeleteDataDialogFragment();
    CommonAddPairOfNumbersValueDF getAddDataPairDialogFragment();
    CommonAddPairOfNumbersValueDF getAddDataPairDialogFragment(int selectedPosition);
    CommonAddDataDF getAddNumberDataDialogFragment();
    CommonAddDataDF getAddNumberDataDialogFragment(int selectedPosition);
    CommonAddDataDF getAddStringDataDialogFragment();
    CommonAddDataDF getAddStringDataDialogFragment(int selectedPosition);
    MaterialDatePicker<Pair<Long, Long>> getDateRangeDialogFragment();
    MaterialDatePicker<Pair<Long, Long>> getDateRangeDialogFragment(int posOfDateRangeBegin, int posOfDateRangeEnd);
    MaterialDatePicker<Long> getDateDialogFragment();
    MaterialDatePicker<Long> getDateDialogFragment(int selectedDate);
}

