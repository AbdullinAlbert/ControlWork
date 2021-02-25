package com.albertabdullin.controlwork.fragments;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.NotifierOfBackPressed;
import com.albertabdullin.controlwork.models.DateConverter;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;

public class EditDataFragment extends Fragment implements BackPressListener {

    private EditDeleteDataVM mViewModel;
    private EditText employeeEditText;
    private EditText firmEditText;
    private EditText placeOfWorkEditText;
    private EditText typeOfWorkEditText;
    private EditText dateEditText;
    private EditText resultEditText;
    private EditText noteEditText;
    private TextView incorrectResultValueTV;
    private Button saveChangedDataButton;

    private final Observer<String> observerOfEmployeeEditText = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            employeeEditText.setText(s);
        }
    };

    private final Observer<String> observerOfFirmEditText = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            firmEditText.setText(s);
        }
    };

    private final Observer<String> observerOfPoWEditText = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            placeOfWorkEditText.setText(s);
        }
    };

    private final Observer<String> observerOfToWEditText = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            typeOfWorkEditText.setText(s);
        }
    };

    private final Observer<String> observerOfDateEditText = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            dateEditText.setText(s);
        }
    };

    private final Observer<String> observerOfResultEditText = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            resultEditText.setText(s);
        }
    };

    private final Observer<String> observerOfNoteEditText = new Observer<String>() {
        @Override
        public void onChanged(String s) {
            noteEditText.setText(s);
        }
    };


    private final Observer<Boolean> observerOfSaveChangedDataButton = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if (aBoolean) {
                saveChangedDataButton.setClickable(true);
                int[] attr = new int[] {android.R.attr.selectableItemBackground};
                TypedArray typedArray = (requireActivity()).obtainStyledAttributes(attr);
                Drawable drawableAttr = typedArray.getDrawable(0);
                typedArray.recycle();
                saveChangedDataButton.setForeground(drawableAttr);
                saveChangedDataButton.setTextColor(getResources().getColor(R.color.standardBlack, null));
            } else {
                saveChangedDataButton.setClickable(false);
                saveChangedDataButton.setForeground(null);
                saveChangedDataButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
            }
        }
    };

    private final Observer<Integer> observerOfTextViewOfResultValue = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            if (integer == View.VISIBLE)
                incorrectResultValueTV.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            else if (integer == View.INVISIBLE)
                incorrectResultValueTV.setLayoutParams(
                        new LinearLayout.LayoutParams(0, 0));
        }
    };

    private final View.OnClickListener employeeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mViewModel.setSelectedTable(ListDBItemsFragment.TableNameForList.EMPLOYEES);
            mViewModel.startLoadDataFromTable();
            startListDBItemsFragment();
        }
    };

    private final View.OnClickListener firmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mViewModel.setSelectedTable(ListDBItemsFragment.TableNameForList.FIRMS);
            mViewModel.startLoadDataFromTable();
            startListDBItemsFragment();
        }
    };

    private final View.OnClickListener placeOfWorkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mViewModel.setSelectedTable(ListDBItemsFragment.TableNameForList.POW);
            mViewModel.startLoadDataFromTable();
            startListDBItemsFragment();
        }
    };

    private final View.OnClickListener typeOfWorkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mViewModel.setSelectedTable(ListDBItemsFragment.TableNameForList.TOW);
            mViewModel.startLoadDataFromTable();
            startListDBItemsFragment();
        }
    };

    private final View.OnClickListener dateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            try {
                builder.setSelection(DateConverter.convertStringDateToLong(dateEditText.getText().toString()));
            } catch (java.text.ParseException e) {
                Toast.makeText(requireContext(), getResources().getString(R.string.cannot_convert_string_to_date) +
                        " : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            MaterialDatePicker<Long> materialDatePicker = builder.build();
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(selection);
                    String date = DateConverter.convertLongToStringDate(calendar);
                    mViewModel.attemptToChangeValueOfDateData(date, selection);
                }
            });
            materialDatePicker.show(requireActivity().getSupportFragmentManager(), "date_picker");
        }
    };

    private final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mViewModel.tryToSaveChangedData();
        }
    };

    private final TextWatcher watcherForResult = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            mViewModel.attemptToChangeValueOfResultData(s);
        }
    };

    private final TextWatcher watcherForNote = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            mViewModel.attemptToChangeValueOfNoteData(s);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
        ((NotifierOfBackPressed)requireActivity()).addListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_data_from_result_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar_for_edit_data);
        toolbar.setTitle(R.string.title_for_edit_data_fragment_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { requireActivity().onBackPressed();
            }
        });
        employeeEditText = view.findViewById(R.id.edit_empl_editText);
        employeeEditText.setOnClickListener(employeeClickListener);
        firmEditText = view.findViewById(R.id.edit_firm_editText);
        firmEditText.setOnClickListener(firmClickListener);
        placeOfWorkEditText = view.findViewById(R.id.edit_placeOfWork_editText);
        placeOfWorkEditText.setOnClickListener(placeOfWorkClickListener);
        typeOfWorkEditText = view.findViewById(R.id.edit_typeOfWork_editText);
        typeOfWorkEditText.setOnClickListener(typeOfWorkClickListener);
        dateEditText = view.findViewById(R.id.edit_date_editText);
        dateEditText.setOnClickListener(dateClickListener);
        resultEditText = view.findViewById(R.id.edit_result_editText);
        resultEditText.addTextChangedListener(watcherForResult);
        noteEditText = view.findViewById(R.id.edit_note_editText);
        noteEditText.addTextChangedListener(watcherForNote);
        incorrectResultValueTV = view.findViewById(R.id.incorrectResultValueFocus_for_edit_data);
        saveChangedDataButton = view.findViewById(R.id.save_changed_data_button);
        saveChangedDataButton.setOnClickListener(buttonClickListener);
        mViewModel.getStateOfSaveChangedDataButtonLD().observe(getViewLifecycleOwner(), observerOfSaveChangedDataButton);
        mViewModel.getEmployeeEditTextForEditDataLD().observe(getViewLifecycleOwner(), observerOfEmployeeEditText);
        mViewModel.getFirmEditTextForEditDataLD().observe(getViewLifecycleOwner(), observerOfFirmEditText);
        mViewModel.getPlaceOfWorkEditTextForEditDataLD().observe(getViewLifecycleOwner(), observerOfPoWEditText);
        mViewModel.getTypeOfWorkEditTextForEditDataLD().observe(getViewLifecycleOwner(), observerOfToWEditText);
        mViewModel.getDateEditTextForEditDataLD().observe(getViewLifecycleOwner(), observerOfDateEditText);
        mViewModel.getResultEditTextForEditDataLD().observe(getViewLifecycleOwner(), observerOfResultEditText);
        mViewModel.getNoteEditTextForEditDataLD().observe(getViewLifecycleOwner(), observerOfNoteEditText);
        mViewModel.getVisibleOfTextViewResultValueLD().observe(getViewLifecycleOwner(), observerOfTextViewOfResultValue);
        mViewModel.initItemForChangedDataInDB();
    }

    private void startListDBItemsFragment() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        ListDBItemsFragment listDBItemsFragment = new ListDBItemsFragment();
        transaction.replace(R.id.container_for_edit_delete_data_fragment, listDBItemsFragment,
                getResources().getString(R.string.tag_for_list_of_DB_items_fragment)).
                addToBackStack(null).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((NotifierOfBackPressed)requireActivity()).removeListener();
    }

    @Override
    public void OnBackPress() {
        mViewModel.setDefaultValuesToEditDataFragmentViews();
    }
}
