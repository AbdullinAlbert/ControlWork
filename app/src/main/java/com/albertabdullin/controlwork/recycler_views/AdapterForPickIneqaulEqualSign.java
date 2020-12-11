package com.albertabdullin.controlwork.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.PickerSignsDF;
import com.albertabdullin.controlwork.models.SortedEqualSignsList;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;


public class AdapterForPickIneqaulEqualSign extends RecyclerView.Adapter<AdapterForPickIneqaulEqualSign.MyViewHolder> {
    private EditDeleteDataVM mVM;
    private SortedEqualSignsList mSigns;
    private LifecycleOwner mLifeCycleOwner;
    private PickerSignsDF mPickerSignsDF;
    private int mSelectedTypeOfValue;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private RadioButton radioButton;

    public MyViewHolder(View v) {
        super(v);
        description = v.findViewById(R.id.textView_for_equal_sign);
        radioButton = v.findViewById(R.id.radioButton_for_select_equal_sign);
    }

    public TextView getDescription() { return description; }

    public RadioButton getRadioButton() { return radioButton; }
}

    public AdapterForPickIneqaulEqualSign(EditDeleteDataVM model, LifecycleOwner lifeCycleOwner, int selectedTypeOfValue, PickerSignsDF pickerSignsDF) {
        mVM = model;
        mLifeCycleOwner = lifeCycleOwner;
        mSigns = mVM.getAvailableOrderedEqualSignsListForDate(selectedTypeOfValue);
        mPickerSignsDF = pickerSignsDF;
        mSelectedTypeOfValue =selectedTypeOfValue;
    }

    public AdapterForPickIneqaulEqualSign(EditDeleteDataVM model, LifecycleOwner lifeCycleOwner, int selectedTypeOfValue, String selectedSign, PickerSignsDF pickerSignsDF) {
        mVM = model;
        mLifeCycleOwner = lifeCycleOwner;
        mSigns = model.getAvailableOrderedEqualSignsList(selectedTypeOfValue, selectedSign);
        mPickerSignsDF = pickerSignsDF;
        mSelectedTypeOfValue = selectedTypeOfValue;
    }

    @NonNull
    @Override
    public AdapterForPickIneqaulEqualSign.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_equality_inequality_sign_for_rv, parent, false);
        return new AdapterForPickIneqaulEqualSign.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterForPickIneqaulEqualSign.MyViewHolder holder, final int position) {
        TextView description = holder.getDescription();
        description.setText(mSigns.get(position).getSign());
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = holder.getRadioButton();
                rb.toggle();
                if (!mPickerSignsDF.haveSelectedItem()) mPickerSignsDF.setSelectedItem();
                if (rb.isChecked()) mVM.setSelectedEqualSign(mSelectedTypeOfValue, mSigns.get(position).getSign(), position);
            }
        });
        final RadioButton rb = holder.getRadioButton();
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                if (!mPickerSignsDF.haveSelectedItem()) mPickerSignsDF.setSelectedItem();
                if (rb.isChecked()) mVM.setSelectedEqualSign(mSelectedTypeOfValue, mSigns.get(position).getSign(), position);
            }
        });
        Observer<Integer> rbObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (position != integer && rb.isChecked()) rb.setChecked(false);
                else if (position == integer) {
                    mPickerSignsDF.setSelectedItem();
                    rb.setChecked(true);
                }
            }
        };
        mVM.getSelectedEqualSignLD().observe(mLifeCycleOwner, rbObserver);
    }

    @Override
    public int getItemCount() {
        return mSigns.size();
    }
}
