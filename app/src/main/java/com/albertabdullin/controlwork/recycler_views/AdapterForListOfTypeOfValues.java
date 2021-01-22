package com.albertabdullin.controlwork.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ProviderOfHolderFragmentState;
import com.albertabdullin.controlwork.fragments.AddItemOfPairOfNumbersValueDF;
import com.albertabdullin.controlwork.fragments.CommonAddDataDF;
import com.albertabdullin.controlwork.fragments.InsertDataButtonClickExecutor;
import com.albertabdullin.controlwork.fragments.SearchCriteriaFragment;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;
import java.util.List;

public class AdapterForListOfTypeOfValues extends RecyclerView.Adapter<AdapterForListOfTypeOfValues.MyViewHolder>{
    private final List<String> listOfEntities;
    private final MakerSearchCriteriaVM mModel;
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
        switch (mSelectedTypeOfValue) {
            case (SearchCriteriaFragment.DATES_VALUE):
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
                                    String beginOfRangeDate = SearchCriteriaFragment.getStringViewOfDate(calendar);
                                    calendar.setTimeInMillis(selection.second);
                                    String endOfRangeDate = SearchCriteriaFragment.getStringViewOfDate(calendar);
                                    mModel.changeItemToOneDateList(mSign, holder.getCurrentPosition(), beginOfRangeDate, endOfRangeDate);
                                    mModel.changeSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                                            mSign, holder.getCurrentPosition() * 2, selection.first, selection.second);
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
                                String date = SearchCriteriaFragment.getStringViewOfDate(calendar);
                                mModel.changeItemToOneDateList(mSign, holder.getCurrentPosition(), date, null);
                                mModel.changeSearchCriteria(SearchCriteriaFragment.DATES_VALUE,
                                        mSign, holder.getCurrentPosition(), selection, null);
                            }
                        });
                        materialDatePicker.show(mFragmentActivity.getSupportFragmentManager(), "date_picker");
                    }
                });
                break;
            case (SearchCriteriaFragment.NUMBERS_VALUE):
                if (("\u2a7e" + " " + "\u2a7d").equals(mSign))
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddItemOfPairOfNumbersValueDF addItemOfPairOfNumbersValueDF =
                                    new AddItemOfPairOfNumbersValueDF(mSign, holder.getCurrentPosition());
                            addItemOfPairOfNumbersValueDF.show(mFragmentActivity.getSupportFragmentManager(), "add note value");
                        }
                    });
                else tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonAddDataDF commonAddDataDF = new CommonAddDataDF()
                                .setHint(mFragmentActivity.getResources().getString(R.string.hint_for_insert_number_for_search_criteria))
                                .setInputType(CommonAddDataDF.EditTextInputType.NUMBER_DECIMAL)
                                .setLengthOfText(mFragmentActivity.getResources().getInteger(R.integer.max_digit_length_of_value))
                                .setTextForEditText(mModel.getValueOfNumber(mSign, holder.getCurrentPosition()))
                                .setExecutor(new InsertDataButtonClickExecutor() {
                                    @Override
                                    public void executeYesButtonClick(AppCompatActivity activity, String text) {
                                        if (text.length() != 0) {
                                            MakerSearchCriteriaVM localVM =
                                                    (MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)activity).getHolder();
                                            localVM.changeItemToOneNumberList(mSign, holder.getCurrentPosition(), text, null);
                                            localVM.changeSearchCriteria(SearchCriteriaFragment.NUMBERS_VALUE, mSign,
                                                    holder.getCurrentPosition(), text, null);
                                        } else {
                                            Toast toast = Toast.makeText(mFragmentActivity,
                                                    "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                    @Override
                                    public void executeNoButtonClick() {
                                    }
                                });
                        commonAddDataDF.show(mFragmentActivity.getSupportFragmentManager(), "changeData");
                    }
                });
                break;
            case (SearchCriteriaFragment.NOTES_VALUE):
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonAddDataDF commonAddDataDF = new CommonAddDataDF()
                                .setHint(mFragmentActivity.getResources().getString(R.string.hint_for_insert_number_for_search_criteria))
                                .setInputType(CommonAddDataDF.EditTextInputType.TEXT_PERSON_NAME)
                                .setLengthOfText(30)
                                .setTextForEditText(mModel.getValueOfNote(mSign, holder.getCurrentPosition()))
                                .setExecutor(new InsertDataButtonClickExecutor() {
                                    @Override
                                    public void executeYesButtonClick(AppCompatActivity activity, String text) {
                                        if (text.length() != 0) {
                                            MakerSearchCriteriaVM localVM =
                                                    (MakerSearchCriteriaVM)((ProviderOfHolderFragmentState)activity).getHolder();
                                            localVM.changeItemToNoteList(mSign, holder.getCurrentPosition(), text);
                                            localVM.changeSearchCriteria(SearchCriteriaFragment.NOTES_VALUE, mSign,
                                                    holder.getCurrentPosition(), text, null);
                                        } else {
                                            Toast toast = Toast.makeText(mFragmentActivity,
                                                    "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                    @Override
                                    public void executeNoButtonClick() {
                                    }
                                });
                        commonAddDataDF.show(mFragmentActivity.getSupportFragmentManager(), "changeData");
                    }
                });
                break;
            default:
                throw new RuntimeException("Опечатка констант. Класс AdapterForListOfTypeOfValues. mSelectedTypeOfValue - " + mSelectedTypeOfValue);
        }
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
                if (cb.isChecked()) {
                    if (mSelectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE)
                        mModel.addSelectedItemToListOfDeletedDate(mSign, holder.getCurrentPosition());
                    else if (mSelectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE) mModel.addSelectedItemToListOfDeletedNumber(mSign, holder.getCurrentPosition());
                    else mModel.addSelectedItemToListOfDeletedNote(mSign, holder.getCurrentPosition());
                }
                else {
                    if (mSelectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE)
                        mModel.removeSelectedItemFromListOfDeletedDate(mSign, holder.getCurrentPosition());
                    else if (mSelectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE)
                        mModel.removeSelectedItemFromListOfDeletedNumber(mSign, holder.getCurrentPosition());
                    else  mModel.removeSelectedItemFromListOfDeletedNote(mSign, holder.getCurrentPosition());
                }
            }
        });
        holder.getCheckBox().setChecked(
                mSelectedTypeOfValue == SearchCriteriaFragment.DATES_VALUE ? mModel.isCheckedSelectableItemFromListOfDeletedDatePosition(mSign, position)
                : mSelectedTypeOfValue == SearchCriteriaFragment.NUMBERS_VALUE ? mModel.isCheckedSelectableItemFromListOfDeletedNumberPosition(mSign, position)
                : mModel.isCheckedSelectableItemFromListOfDeletedNotePosition(mSign, position));
    }

    @Override
    public int getItemCount() {
        return listOfEntities.size();
    }
}