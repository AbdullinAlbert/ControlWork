package com.albertabdullin.controlwork.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
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

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ProviderOfHolderFragmentState;
import com.albertabdullin.controlwork.viewmodels.DialogFragmentStateHolder;

public class CommonAddPairOfNumbersValueDF extends DialogFragment {
    private DialogFragmentStateHolder viewModel;
    private EditText etFirstNumber;
    private EditText etSecondNumber;
    private String firstFieldText;
    private String secondFieldText;
    private InsertDataPairButtonClickExecutor mExecutor;

    private final String KEY_FOR_EXECUTOR = "key for mExecutor";

    private final View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && viewModel.isNotActivatedDF()) {
                viewModel.setActivatedDF(true);
                getContext();
                InputMethodManager inputManager = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    };

    public CommonAddPairOfNumbersValueDF() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mExecutor = savedInstanceState.getParcelable(KEY_FOR_EXECUTOR);
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
        return inflater.inflate(R.layout.dialog_fragmet_add_pair_of_items_of_number_value, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = ((ProviderOfHolderFragmentState)requireActivity()).getHolder();
        etFirstNumber = view.findViewById(R.id.editText_firstNumber);
        etSecondNumber = view.findViewById(R.id.editText_secondNumber);
        etFirstNumber.setText(firstFieldText != null ? firstFieldText : "");
        etSecondNumber.setText(secondFieldText  != null ? secondFieldText : "");
        Button bAdd = view.findViewById(R.id.add_df_button);
        Button bCancel = view.findViewById(R.id.cancel_df_button);
        bAdd.setOnClickListener(v -> {
            String stringOfFirstNumber = etFirstNumber.getText().toString();
            String stringOfSecondNumber = etSecondNumber.getText().toString();
            float floatOfFirstNumber;
            float floatOfSecondNumber;
            try {
                floatOfFirstNumber = Float.parseFloat(stringOfFirstNumber);
                floatOfSecondNumber = Float.parseFloat(stringOfSecondNumber);
            } catch (NumberFormatException e) {
                Toast toast = Toast.makeText(getContext(), getString(R.string.incorrect_number_format), Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            if (floatOfFirstNumber >= floatOfSecondNumber) {
                Toast.makeText(getContext(), getString(R.string.second_value_must_be_over_than_first_one), Toast.LENGTH_SHORT).show();
                return;
            }
            mExecutor.executeYesButtonClick(floatOfFirstNumber, floatOfSecondNumber);
            hideKeyBoard();
            requireDialog().dismiss();
        });
        bCancel.setOnClickListener(v -> {
            mExecutor.executeNoButtonClick();
            hideKeyBoard();
            requireDialog().dismiss();
        });
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        etFirstNumber.setOnFocusChangeListener(focusChangeListener);
        etFirstNumber.requestFocus();
        super.onResume();
    }

    private void hideKeyBoard() {
        etFirstNumber.setFocusable(false);
        etFirstNumber.clearFocus();
        viewModel.setActivatedDF(false);
        if (etFirstNumber != null) {
            getActivity();
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etFirstNumber.getWindowToken(), 0);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_FOR_EXECUTOR, mExecutor);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        hideKeyBoard();
        super.onDismiss(dialog);
    }

    public CommonAddPairOfNumbersValueDF setTextForFirstField(String text) {
        firstFieldText = text;
        return this;
    }

    public CommonAddPairOfNumbersValueDF setTextForSecondField(String text) {
        secondFieldText = text;
        return this;
    }

    public void setExecutor(InsertDataPairButtonClickExecutor executor) {
        mExecutor = executor;
    }

}
