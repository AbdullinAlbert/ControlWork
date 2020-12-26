package com.albertabdullin.controlwork.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.viewmodels.MakerSearchCriteriaVM;

public class AddItemOfNoteValueDF extends DialogFragment {
    private MakerSearchCriteriaVM viewModel;
    private EditText etAddNewData;
    private String mSign;
    private Integer mCurrentPosition;
    private static final String KEY_OF_SIGN = "key of sign";
    private static final String KEY_OF_CURRENT_POSITION = "key of current position";

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && viewModel.isNotActivatedDF()) {
                viewModel.setActivatedDF(true);
                InputMethodManager inputManager = (InputMethodManager) getContext()
                        .getSystemService(getContext().INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            viewModel.setSelectedSignAndStringViewOfNote(mSign, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public AddItemOfNoteValueDF() {}

    public AddItemOfNoteValueDF(String sign, Integer currentPosition) {
        mSign = sign;
        mCurrentPosition = currentPosition;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MakerSearchCriteriaVM.class);
        if (savedInstanceState != null) {
            mSign = savedInstanceState.getString(KEY_OF_SIGN);
            mCurrentPosition = savedInstanceState.getInt(KEY_OF_CURRENT_POSITION, -1);
            if (mCurrentPosition == -1) mCurrentPosition = null;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragmet_add_item_of_note_value, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etAddNewData = view.findViewById(R.id.editTextNewData);
        etAddNewData.addTextChangedListener(textWatcher);
        if (mCurrentPosition != null) etAddNewData.setText(viewModel.getValueOfNote(mSign, mCurrentPosition));
        Button bAdd = view.findViewById(R.id.add_df_button);
        Button bCancel = view.findViewById(R.id.cancel_df_button);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etAddNewData.getText().toString();
                if(description.length() != 0) {
                    if (mCurrentPosition == null) {
                        viewModel.addItemToNoteList(mSign, description);
                        viewModel.addSearchCriteriaForNote(
                                viewModel.getPositionOfSign(SearchCriteriaFragment.NOTES_VALUE, mSign), description);
                    } else {
                        viewModel.changeItemToNoteList(mSign, mCurrentPosition, description);
                        viewModel.changeSearchCriteriaValueForNote(mSign, mCurrentPosition, description);
                    }
                }
                else {
                    Toast toast = Toast.makeText(getContext(), "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                    toast.show();
                }
                hideKeyBoard();
                requireDialog().dismiss();
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                requireDialog().dismiss();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_OF_SIGN, mSign);
        if (mCurrentPosition != null) outState.putInt(KEY_OF_CURRENT_POSITION, mCurrentPosition);
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        etAddNewData.setOnFocusChangeListener(focusChangeListener);
        etAddNewData.requestFocus();
        super.onResume();
    }

    private void hideKeyBoard() {
        etAddNewData.setFocusable(false);
        etAddNewData.clearFocus();
        viewModel.setActivatedDF(false);
        if (etAddNewData != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etAddNewData.getWindowToken(), 0);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        hideKeyBoard();
        super.onDismiss(dialog);
    }

}
