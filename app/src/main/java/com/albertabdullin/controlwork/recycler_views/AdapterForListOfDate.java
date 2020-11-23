package com.albertabdullin.controlwork.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;
import java.util.List;

public class AdapterForListOfDate extends RecyclerView.Adapter<AdapterForListOfDate.MyViewHolder>{
    private List<String> listOfEntities;
    private EditDeleteDataVM mModel;
    private String mSign;
    private FragmentActivity mFragmentActivity;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private CheckBox checkBox;

        public MyViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.editTextForDate);
            checkBox = v.findViewById(R.id.checkBox);
        }

        TextView getDescription() { return description; }

        CheckBox getCheckBox() { return checkBox; }

        int getCurrentPosition() { return getBindingAdapterPosition(); }
    }

    public AdapterForListOfDate(List<String> list,
                                EditDeleteDataVM model,
                                String sign,
                                FragmentActivity fragmentActivity) {
        listOfEntities = list;
        mModel = model;
        mSign = sign;
        mFragmentActivity = fragmentActivity;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_to_add_search_criteria_for_rv, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) { }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull List<Object> payloads) {
        TextView tv = holder.getDescription();
        tv.setText(listOfEntities.get(position));
        if (("\u2a7e" + " " + "\u2a7d").equals(mSign))
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                    Long firstSelect = mModel.getSelection(mSign, holder.getCurrentPosition() * 2);
                    Long secondSelect = mModel.getSelection(mSign, (holder.getCurrentPosition() * 2) + 1);
                    builder.setSelection(new Pair<>(firstSelect, secondSelect));
                    MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builder.build();
                    materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                        @Override
                        public void onPositiveButtonClick(Pair<Long, Long> selection) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(selection.first);
                            String beginOfRangeDate = SearchCriteriaFragment.convertLongToStringDate(calendar);
                            calendar.setTimeInMillis(selection.second);
                            String endOfRangeDate = SearchCriteriaFragment.convertLongToStringDate(calendar);
                            mModel.changeItemToOneDateList(mSign, holder.getCurrentPosition(), beginOfRangeDate, endOfRangeDate);
                            mModel.changeSearchCriteriaValueForDate(mSign, holder.getCurrentPosition() * 2, selection.first, selection.second);
                        }
                    });
                    materialDatePicker.show(mFragmentActivity.getSupportFragmentManager(), "date_picker");
                }
            });
        else tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setSelection(mModel.getSelection(mSign, holder.getCurrentPosition()));
                MaterialDatePicker<Long> materialDatePicker = builder.build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection);
                        String date = SearchCriteriaFragment.convertLongToStringDate(calendar);
                        mModel.changeItemToOneDateList(mSign, holder.getCurrentPosition(), date, null);
                        mModel.changeSearchCriteriaValueForDate(mSign, holder.getCurrentPosition(), selection, null);
                    }
                });
                materialDatePicker.show(mFragmentActivity.getSupportFragmentManager(), "date_picker");
            }
        });
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        CheckBox checkbox = holder.getCheckBox();
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox)v;
                if (cb.isChecked()) mModel.addSelectedItemToListOfDeletedDate(mSign,
                        holder.getCurrentPosition());
                else mModel.removeSelectedItemFromListOfDeletedDate(mSign, holder.getCurrentPosition());
            }
        });
        holder.getCheckBox().setChecked(mModel.isCheckedSelectableItemFromListOfDeletedPosition(mSign, position));
    }

    @Override
    public int getItemCount() {
        return listOfEntities.size();
    }
}