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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.FillNewData_Activity;
import com.albertabdullin.controlwork.activities.ListOfBDItemsActivity;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.AMControllerForListItemsFromDB;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;

import java.util.Iterator;

public class UpdateDataDF extends DialogFragment {
    private ListOfItemsVM viewModel;
    private TextView helperTextView;
    private TextView tvUpdateData;

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
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

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
        return inflater.inflate(R.layout.dialog_fragment_update_data, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUpdateData = view.findViewById(R.id.UpdateDataDFText);
        switch (viewModel.getNumberOfNeededTable()) {
            case FillNewData_Activity.TABLE_OF_EMPLOYERS:
                tvUpdateData.setHint(R.string.hint_firstname_secondname);
                break;
            case FillNewData_Activity.TABLE_OF_FIRMS:
                tvUpdateData.setHint(R.string.hint_frim);
                break;
            case FillNewData_Activity.TABLE_OF_TYPES_OF_WORK:
                tvUpdateData.setHint(R.string.hint_type_of_work);
                break;
            case FillNewData_Activity.TABLE_OF_PLACES_OF_WORK:
                tvUpdateData.setHint(R.string.hint_place_of_work);
                break;
        }
        tvUpdateData.addTextChangedListener(countCharTW);
        helperTextView = view.findViewById(R.id.UpdateDataDFHelper_text);
        Button bAdd = view.findViewById(R.id.UpdateDataDAddButton);
        Button bCancel = view.findViewById(R.id.UpdateDataDFCancelButton);
        final SelectionTracker tracker = ((ListOfBDItemsActivity) requireActivity()).getSelectionTracker();
        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = tvUpdateData.getText().toString();
                if(description.length() != 0) {
                    Iterator<SimpleEntityForDB> iterator = tracker.getSelection().iterator();
                    SimpleEntityForDB eDB = iterator.next();
                    viewModel.updateItem(eDB, description);
                    tracker.clearSelection();
                    hideKeyBoard();
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
        tvUpdateData.setOnFocusChangeListener(focusChangeListener);
        tvUpdateData.requestFocus();
        super.onResume();
    }

    private void hideKeyBoard() {
        tvUpdateData.setFocusable(false);
        viewModel.setActivatedDF(false);
        InputMethodManager imm = (InputMethodManager)
            getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tvUpdateData.getWindowToken(), 0);
    }
}
