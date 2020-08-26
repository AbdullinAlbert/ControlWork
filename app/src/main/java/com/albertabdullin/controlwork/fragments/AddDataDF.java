package com.albertabdullin.controlwork.fragments;

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
    private ListOfItemsVM viewModel;
    private TextView helperTextView;
    private TextView tvAddNewData;

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
                getDialog().dismiss();
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        super.onResume();
    }
}
