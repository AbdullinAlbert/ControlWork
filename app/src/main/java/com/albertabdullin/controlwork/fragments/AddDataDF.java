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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;

public class AddDataDF extends DialogFragment {
    public static final String TAG = "DialogFragmnet for add items";
    private ListOfItemsVM viewModel;
    private TextView helperTextView;
    private TextView tvAddNewData;

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && !viewModel.isActivatedDF()) {
                viewModel.setActivatedDF(true);
                InputMethodManager inputMananger = (InputMethodManager) getContext()
                        .getSystemService( getContext().INPUT_METHOD_SERVICE);
                inputMananger.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
            helperTextView.setText(s.length() + " / 30");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ListOfItemsVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragmet_add_data, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvAddNewData = view.findViewById(R.id.editTextNewData);
        tvAddNewData.setHint(R.string.hint_firstname_secondname);
        tvAddNewData.addTextChangedListener(countCharTW);
        helperTextView = view.findViewById(R.id.AddDataDFHelper_text);
        Button bAdd = view.findViewById(R.id.add_df_button);
        Button bCancel = view.findViewById(R.id.cancel_df_button);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = tvAddNewData.getText().toString();
                if(description.length() != 0) viewModel.addItem(description);
                else {
                    Toast toast = Toast.makeText(getContext(), "Нельзя добавлять пустые строки", Toast.LENGTH_SHORT);
                    toast.show();
                }
                hideKeyBoard();
                getDialog().dismiss();
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                getDialog().dismiss();
            }
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
        tvAddNewData.setOnFocusChangeListener(focusChangeListener);
        tvAddNewData.requestFocus();
        super.onResume();
    }

    private void hideKeyBoard() {
        tvAddNewData.setFocusable(false);
        tvAddNewData.clearFocus();
        viewModel.setActivatedDF(false);
        if (tvAddNewData != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tvAddNewData.getWindowToken(), 0);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        hideKeyBoard();
        super.onDismiss(dialog);
    }
}
