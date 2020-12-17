package com.albertabdullin.controlwork.fragments;

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
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

public class AddItemOfPairOfNumbersValueDF extends DialogFragment {
    public static final String TAG = "DialogFragment for add items";
    private EditDeleteDataVM viewModel;
    private EditText etFirstNumber;
    private EditText etSecondNumber;
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

    public AddItemOfPairOfNumbersValueDF() {}

    public AddItemOfPairOfNumbersValueDF(String sign, Integer currentPosition) {
        mSign = sign;
        mCurrentPosition = currentPosition;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EditDeleteDataVM.class);
        if (savedInstanceState != null) {
            mSign = savedInstanceState.getString(KEY_OF_SIGN);
            mCurrentPosition = savedInstanceState.getInt(KEY_OF_CURRENT_POSITION, -1);
            if (mCurrentPosition == -1) mCurrentPosition = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragmet_add_pair_of_items_of_number_value, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etFirstNumber = view.findViewById(R.id.editText_firstNumber);
        etSecondNumber = view.findViewById(R.id.editText_secondNumber);
        if (mCurrentPosition != null) {
            etFirstNumber.setText(viewModel.getValueOfNumber(mSign, mCurrentPosition * 2));
            etSecondNumber.setText(viewModel.getValueOfNumber(mSign, mCurrentPosition * 2 + 1));
        }
        Button bAdd = view.findViewById(R.id.add_df_button);
        Button bCancel = view.findViewById(R.id.cancel_df_button);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringOfFirstNumber = etFirstNumber.getText().toString();
                String stringOfSecondNumber = etSecondNumber.getText().toString();
                Float floatOfFirstNumber;
                Float floatOfSecondNumber;
                try {
                    floatOfFirstNumber = Float.parseFloat(stringOfFirstNumber);
                    floatOfSecondNumber = Float.parseFloat(stringOfSecondNumber);
                } catch (NumberFormatException e) {
                    Toast toast = Toast.makeText(getContext(), "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (mCurrentPosition == null) {
                    if (floatOfFirstNumber < floatOfSecondNumber) {
                        viewModel.addItemToNumberList(mSign, stringOfFirstNumber, stringOfSecondNumber);
                        viewModel.addSearchCriteriaForNumber(
                            viewModel.getPositionOfSign(SearchCriteriaFragment.NUMBERS_VALUE, mSign), floatOfFirstNumber, floatOfSecondNumber);
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Первое значение не меньше второго", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                } else {
                    viewModel.changeItemToOneNumberList(mSign, mCurrentPosition, stringOfFirstNumber, stringOfSecondNumber);
                    viewModel.changeSearchCriteriaValueForNumber(mSign, mCurrentPosition * 2, floatOfFirstNumber, floatOfSecondNumber);
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
        etFirstNumber.setOnFocusChangeListener(focusChangeListener);
        etFirstNumber.requestFocus();
        super.onResume();
    }

    private void hideKeyBoard() {
        etFirstNumber.setFocusable(false);
        etFirstNumber.clearFocus();
        viewModel.setActivatedDF(false);
        if (etFirstNumber != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etFirstNumber.getWindowToken(), 0);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        hideKeyBoard();
        super.onDismiss(dialog);
    }

}
