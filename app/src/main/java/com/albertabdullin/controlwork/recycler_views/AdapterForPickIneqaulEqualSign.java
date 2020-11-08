package com.albertabdullin.controlwork.recycler_views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.PickerSignsDF;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;


public class AdapterForPickIneqaulEqualSign extends RecyclerView.Adapter<AdapterForPickIneqaulEqualSign.MyVeiwHolder> {
private EditDeleteDataVM mVM;
private String[] mSigns;
private PickerSignsDF mLifeCycleOwner;

public static class MyVeiwHolder extends RecyclerView.ViewHolder {
    private TextView description;
    private RadioButton radioButton;

    public MyVeiwHolder(View v) {
        super(v);
        description = v.findViewById(R.id.textView_for_equal_sign);
        radioButton = v.findViewById(R.id.radioButton_for_select_equal_sign);
    }

    public TextView getDescription() { return description; }

    public RadioButton getRadioButton() { return radioButton; }
}

    public AdapterForPickIneqaulEqualSign(EditDeleteDataVM model, Context context, PickerSignsDF lifeCycleOwner) {
        mVM = model;
        mLifeCycleOwner = lifeCycleOwner;
        switch (mLifeCycleOwner.getSelectedSetOfSigns()) {
            case PickerSignsDF.FULL_SET_SIGNS:
                mSigns = context.getResources().getStringArray(R.array.full_equal_inequal_signs_array);
                break;
            case PickerSignsDF.MORE_OR_EQUAL_SET_SIGNS:
                mSigns = context.getResources().getStringArray(R.array.more_or_equal_signs_array);
                break;
            case PickerSignsDF.LESS_OR_EQUAL_SET_SIGNS:
                mSigns = context.getResources().getStringArray(R.array.less_or_equal_signs_array);
                break;
            case PickerSignsDF.OTHER_SET_SIGNS:
                mSigns = context.getResources().getStringArray(R.array.other_signs_array);
                break;
            default:
                throw new RuntimeException("опечатка констант в конструкторе AdapterForPickIneqaulEqualSign");
        }

    }

    @NonNull
    @Override
    public AdapterForPickIneqaulEqualSign.MyVeiwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_equality_inequality_sign, parent, false);
        return new AdapterForPickIneqaulEqualSign.MyVeiwHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterForPickIneqaulEqualSign.MyVeiwHolder holder, final int position) {
        TextView description = holder.getDescription();
        description.setText(mSigns[position]);
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = holder.getRadioButton();
                rb.toggle();
                if (rb.isChecked()) mVM.setSelectedEqualSign(position);
            }
        });
        final RadioButton rb = holder.getRadioButton();
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                if (rb.isChecked()) mVM.setSelectedEqualSign(position);
            }
        });
        Observer<Integer> rbObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (position != integer && rb.isChecked()) rb.setChecked(false);
            }
        };
        mVM.getSelectedEqualSignLD().observe(mLifeCycleOwner, rbObserver);
    }

    @Override
    public int getItemCount() {
        return mSigns.length;
    }
}
