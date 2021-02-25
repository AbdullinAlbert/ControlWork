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
import com.albertabdullin.controlwork.activities.DialogFragmentProvider;
import com.albertabdullin.controlwork.fragments.CommonAddPairOfNumbersValueDF;
import com.albertabdullin.controlwork.fragments.CommonAddDataDF;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.List;

public class AdapterForListOfTypeOfValues extends RecyclerView.Adapter<AdapterForListOfTypeOfValues.MyViewHolder>{
    private final List<String> listOfEntities;
    private final MakerSearchCriteriaVM mViewModel;
    private final String mSign;
    private final FragmentActivity mFragmentActivity;
    private final int mSelectedTypeOfValue;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView description;
        private final CheckBox checkBox;

        public MyViewHolder(View v) {
            super(v);
            description = v.findViewById(R.id.editTextForDate);
            checkBox = v.findViewById(R.id.checkBox);
        }

        TextView getDescription() { return description; }

        CheckBox getCheckBox() { return checkBox; }

        int getCurrentPosition() { return getBindingAdapterPosition(); }
    }

    public AdapterForListOfTypeOfValues(int selectedTypeOfValue,
                                        List<String> list,
                                        MakerSearchCriteriaVM model,
                                        String sign,
                                        FragmentActivity fragmentActivity) {
        mSelectedTypeOfValue = selectedTypeOfValue;
        listOfEntities = list;
        mViewModel = model;
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
        switch (mSelectedTypeOfValue) {
            case (SearchCriteriaFragment.DATES_VALUE):
                if (("\u2a7e" + " " + "\u2a7d").equals(mSign))
                    tv.setOnClickListener(v -> {
                        int dateRangeBegin = holder.getCurrentPosition() * 2;
                        int dateRangeEnd = (holder.getCurrentPosition() * 2) + 1;
                        MaterialDatePicker<Pair<Long, Long>> materialDatePicker =
                                ((DialogFragmentProvider)mFragmentActivity).getDateRangeDialogFragment(dateRangeBegin, dateRangeEnd);
                        materialDatePicker.show(mFragmentActivity.getSupportFragmentManager(), "date_picker");
                    });
                else tv.setOnClickListener(v -> {
                    MaterialDatePicker<Long> materialDatePicker =
                            ((DialogFragmentProvider)mFragmentActivity).getDateDialogFragment(holder.getCurrentPosition());
                    materialDatePicker.show(mFragmentActivity.getSupportFragmentManager(), "date_picker");
                });
                break;
            case (SearchCriteriaFragment.NUMBERS_VALUE):
                if (("\u2a7e" + " " + "\u2a7d").equals(mSign))
                    tv.setOnClickListener(v -> {
                        CommonAddPairOfNumbersValueDF commonAddPairOfNumbersValueDF =
                                ((DialogFragmentProvider)mFragmentActivity).getAddDataPairDialogFragment(holder.getCurrentPosition());
                        commonAddPairOfNumbersValueDF.show(mFragmentActivity.getSupportFragmentManager(), "add note value");
                    });
                else tv.setOnClickListener(v -> {
                    CommonAddDataDF commonAddDataDF = ((DialogFragmentProvider)mFragmentActivity)
                            .getAddNumberDataDialogFragment(holder.getCurrentPosition());
                    commonAddDataDF.show(mFragmentActivity.getSupportFragmentManager(), "changeData");
                });
                break;
            case (SearchCriteriaFragment.NOTES_VALUE):
                tv.setOnClickListener(v -> {
                    CommonAddDataDF commonAddDataDF =  ((DialogFragmentProvider)mFragmentActivity)
                            .getAddStringDataDialogFragment(holder.getCurrentPosition());
                    commonAddDataDF.show(mFragmentActivity.getSupportFragmentManager(), "changeData");
                });
                break;
            default:
                throw new RuntimeException("Опечатка констант. Класс AdapterForListOfTypeOfValues. mSelectedTypeOfValue - " + mSelectedTypeOfValue);
        }
        tv.setOnLongClickListener(v -> true);
        CheckBox checkbox = holder.getCheckBox();
        checkbox.setOnClickListener(v -> {
            CheckBox cb = (CheckBox)v;
            if (cb.isChecked()) {
                if (mSelectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE)
                    mViewModel.addSelectedItemToListOfDeletedDate(mSign, holder.getCurrentPosition());
                else if (mSelectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) mViewModel.addSelectedItemToListOfDeletedNumber(mSign, holder.getCurrentPosition());
                else mViewModel.addSelectedItemToListOfDeletedNote(mSign, holder.getCurrentPosition());
            }
            else {
                if (mSelectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE)
                    mViewModel.removeSelectedItemFromListOfDeletedDate(mSign, holder.getCurrentPosition());
                else if (mSelectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE)
                    mViewModel.removeSelectedItemFromListOfDeletedNumber(mSign, holder.getCurrentPosition());
                else  mViewModel.removeSelectedItemFromListOfDeletedNote(mSign, holder.getCurrentPosition());
            }
        });
        holder.getCheckBox().setChecked(
                mSelectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE ? mViewModel.isCheckedSelectableItemFromListOfDeletedDatePosition(mSign, position)
                : mSelectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE ? mViewModel.isCheckedSelectableItemFromListOfDeletedNumberPosition(mSign, position)
                : mViewModel.isCheckedSelectableItemFromListOfDeletedNotePosition(mSign, position));
    }

    @Override
    public int getItemCount() {
        return listOfEntities.size();
    }
}