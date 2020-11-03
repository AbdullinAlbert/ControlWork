package com.albertabdullin.controlwork.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.fragments.PickerItemsDF;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.viewmodels.EditDeleteDataVM;

import java.util.List;

public class AdapterForPickItems extends RecyclerView.Adapter<AdapterForPickItems.MyVeiwHolder> {
    private List<SimpleEntityForDB> mListOfEntities;
    private EditDeleteDataVM mVM;
    private PickerItemsDF mLifeCycleOwner;
    private int mSelectedTable;

    public static class MyVeiwHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private TextView id;
        private CheckBox checkBox;

        public MyVeiwHolder(View v) {
            super(v);
            description = v.findViewById(R.id.textview_to_select_item);
            id = v.findViewById(R.id.item_id);
            checkBox = v.findViewById(R.id.checkBox_for_select);
        }

        public TextView getDescription() { return description; }

        public TextView getID() {return id; }

        public CheckBox getCheckBox() { return checkBox; }
    }

    public AdapterForPickItems(List<SimpleEntityForDB> list, EditDeleteDataVM model, PickerItemsDF lifeCycleOwner, int selectedTable) {
        mListOfEntities = list;
        mVM = model;
        mLifeCycleOwner = lifeCycleOwner;
        mSelectedTable = selectedTable;
    }

    @NonNull
    @Override
    public MyVeiwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_selectable_item_form_list, parent, false);
        return new MyVeiwHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyVeiwHolder holder, final int position) {
        holder.getDescription().setText(mListOfEntities.get(position).getDescription());
        holder.getID().setText("id: " + mListOfEntities.get(position).getID());
        holder.getCheckBox().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox)v;
                if (cb.isChecked()) mVM.addSelectedItem(mSelectedTable, position);
                else mVM.removeSelectedItem(mSelectedTable, position);
            }
        });
        if (mVM.getTransientListOfSelectedItems().contains(mListOfEntities.get(position)))
            holder.getCheckBox().setChecked(true);
        Observer<Boolean> observerForCheckBoxes = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (mVM.getTransientListOfSelectedItems().isEmpty())
                    holder.getCheckBox().setChecked(aBoolean);
            }
        };
        mVM.getSelectedCheckBoxesLD().observe(mLifeCycleOwner, observerForCheckBoxes);
    }

    @Override
    public int getItemCount() {
        return mListOfEntities.size();
    }
}
