package com.albertabdullin.controlwork.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.SelectionTracker;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.activities.ListOfBDItemsActivity;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.AMControllerForListItemsFromDB;
import com.albertabdullin.controlwork.viewmodels.ListOfItemsVM;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeleteDataDF extends DialogFragment {
    private ListOfItemsVM viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ListOfItemsVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_delete_data, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvSelectSize = view.findViewById(R.id.DeleteDFSelectSize);
        TextView tvQuestion = view.findViewById(R.id.DeleteDFQuestion);
        final SelectionTracker tracker = ((ListOfBDItemsActivity) requireActivity()).getSelectionTracker();
        tvSelectSize.setText("Выбрано записей: " + tracker.getSelection().size());
        if(tracker.getSelection().size() == 1) {
            Iterator<SimpleEntityForDB> iterator = tracker.getSelection().iterator();
            String description = iterator.next().getDescription();
            tvQuestion.setText("Вы действительно хотите удалить " + description + "?");
        }
        Button bYES = view.findViewById(R.id.DeleteDFButtonYES);
        Button bNO = view.findViewById(R.id.DeleteDFButtonNO);
        bYES.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Iterator<SimpleEntityForDB> iterator = tracker.getSelection().iterator();
                List<SimpleEntityForDB> deletedItemsList = new ArrayList<>();
                while (iterator.hasNext()) deletedItemsList.add(iterator.next());
                viewModel.deleteItem(deletedItemsList);
                tracker.clearSelection();
                getDialog().dismiss();
            }
        });
        bNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getDialog().dismiss(); }
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
