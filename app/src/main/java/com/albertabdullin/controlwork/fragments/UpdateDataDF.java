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
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.AMControllerForListItems;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;

import java.util.Iterator;

public class UpdateDataDF extends DialogFragment {
    private ListOfItemsVM viewModel;
    private TextView helperTextView;
    private TextView tvUpdateData;
    private AMControllerForListItems amc;

    TextWatcher countCharTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            helperTextView.setText(s.length() + " / 30");
        }
    };

    public UpdateDataDF(AMControllerForListItems amc) {
        super();
        this.amc = amc;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ListOfItemsVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_update_data, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUpdateData = view.findViewById(R.id.UpdateDataDFText);
        tvUpdateData.setHint(R.string.hint_firstname_secondname);
        tvUpdateData.addTextChangedListener(countCharTW);
        helperTextView = view.findViewById(R.id.UpdateDataDFHelper_text);
        Button bAdd = view.findViewById(R.id.UpdateDataDAddButton);
        Button bCancel = view.findViewById(R.id.UpdateDataDFCancelButton);
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = tvUpdateData.getText().toString();
                if(description.length() != 0) {
                    Iterator<SimpleEntityForDB> iterator = amc.getTracker().getSelection().iterator();
                    SimpleEntityForDB eDB = iterator.next();
                    viewModel.updateItem(eDB, description);
                    amc.getTracker().clearSelection();
                    getDialog().dismiss();
                }
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
