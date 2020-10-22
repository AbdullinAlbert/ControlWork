package com.albertabdullin.controlwork.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.activities.FillNewData_Activity;
import com.albertabdullin.controlwork.viewmodels.AddNewDataVM;

import java.util.Calendar;

public class CalendarDF extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private int year, month, dayOfMonth;
    private AddNewDataVM model;
    public CalendarDF(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(AddNewDataVM.class);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new DatePickerDialog(getContext(), this, year, month, dayOfMonth);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month++;
        StringBuilder sb = new StringBuilder();
        if (dayOfMonth < 10) sb.append("0");
        sb.append(dayOfMonth);
        sb.append(".");
        if (month < 10) sb.append("0");
        sb.append(month);
        sb.append(".");
        sb.append(year);
        ((FillNewData_Activity)requireActivity()).getDate().setText(sb.toString());
        model.setDateForSql(sb.toString());
    }
}
