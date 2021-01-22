package com.albertabdullin.controlwork.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ProviderOfHolderFragmentState;
import com.albertabdullin.controlwork.viewmodels.DialogFragmentStateHolder;

public class CommonAddDataDF extends DialogFragment {
    private DialogFragmentStateHolder mViewModel;
    private TextView mHelperTextView;
    private TextView mEditTextAddNewData;
    private String mHint;
    private String mTextOfEditText;
    private int mLengthOfText;
    private EditTextInputType mCurrentInputType;
    private InsertDataButtonClickExecutor mExecutor;

    private final String KEY_FOR_HINT = "key for hint";
    private final String KEY_FOR_LENGTH = "key for length";
    private final String KEY_FOR_EXECUTOR = "key for executor";
    private final String KEY_FOR_CURRENT_INPUT_TYPE = "key for current input type";

    public enum EditTextInputType  {
        TEXT_PERSON_NAME, NUMBER_DECIMAL;

    }

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && mViewModel.isNotActivatedDF()) {
                mViewModel.setActivatedDF(true);
                InputMethodManager inputManager = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    };

    TextWatcher countCharTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mHelperTextView != null) {
                String counter = s.length() + " / " + mLengthOfText;
                mHelperTextView.setText(counter);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ((ProviderOfHolderFragmentState) requireActivity()).getHolder();
        if (savedInstanceState != null) {
            mHint = savedInstanceState.getString(KEY_FOR_HINT);
            mLengthOfText = savedInstanceState.getInt(KEY_FOR_LENGTH);
            mExecutor = savedInstanceState.getParcelable(KEY_FOR_EXECUTOR);
            mCurrentInputType = (EditTextInputType) savedInstanceState.getSerializable(KEY_FOR_CURRENT_INPUT_TYPE);
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
        return inflater.inflate(R.layout.dialog_fragmet_add_data, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHelperTextView = view.findViewById(R.id.AddDataDFHelper_text);
        mEditTextAddNewData = view.findViewById(R.id.editTextNewData);
        mEditTextAddNewData.addTextChangedListener(countCharTW);
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(mLengthOfText);
        mEditTextAddNewData.setFilters(fArray);
        switch (mCurrentInputType) {
            case TEXT_PERSON_NAME:
                mEditTextAddNewData.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case NUMBER_DECIMAL:
                mEditTextAddNewData.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        mEditTextAddNewData.setHint(mHint);
        if (mTextOfEditText != null) {
            mEditTextAddNewData.setText(mTextOfEditText);
            String counter = mTextOfEditText.length() + " / " + mLengthOfText;
            mHelperTextView.setText(counter);
        } else {
            String counter = 0 + " / " + mLengthOfText;
            mHelperTextView.setText(counter);
        }
        Button bAdd = view.findViewById(R.id.add_df_button);
        Button bCancel = view.findViewById(R.id.cancel_df_button);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutor.executeYesButtonClick(((AppCompatActivity)requireActivity()),
                        mEditTextAddNewData.getText().toString());
                tryToHideKeyBoard();
                requireDialog().dismiss();
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExecutor.executeNoButtonClick();
                tryToHideKeyBoard();
                requireDialog().dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        Window window = requireDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        mEditTextAddNewData.setOnFocusChangeListener(focusChangeListener);
        mEditTextAddNewData.requestFocus();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FOR_HINT, mHint);
        outState.putInt(KEY_FOR_LENGTH, mLengthOfText);
        outState.putParcelable(KEY_FOR_EXECUTOR, mExecutor);
        outState.putSerializable(KEY_FOR_CURRENT_INPUT_TYPE, mCurrentInputType);
    }

    private void tryToHideKeyBoard() {
        if (mEditTextAddNewData != null) {
            mEditTextAddNewData.setFocusable(false);
            mEditTextAddNewData.clearFocus();
            mViewModel.setActivatedDF(false);
            hideKeyBoard();
        }
    }

    public CommonAddDataDF setLengthOfText(int length) {
        mLengthOfText = length;
        return this;
    }

    public CommonAddDataDF setInputType(EditTextInputType inputType) {
        mCurrentInputType = inputType;
        return this;
    }

    public CommonAddDataDF setHint(String hint) {
        mHint = hint;
        return this;
    }

    public CommonAddDataDF setExecutor(InsertDataButtonClickExecutor executor) {
        mExecutor = executor;
        return this;
    }

    public CommonAddDataDF setTextForEditText(String text) {
        mTextOfEditText = text;
        return this;
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextAddNewData.getWindowToken(), 0);
    }
}