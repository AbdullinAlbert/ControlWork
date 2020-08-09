package com.albertabdullin.controlwork;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmployersAdapter extends RecyclerView.Adapter<EmployersAdapter.MyVeiwHolder> {
    private List<EntityForDB> listOfEntities;
    private RecyclerViewObserver observer;

    public class MyVeiwHolder extends RecyclerView.ViewHolder {
        public TextView rowItem;
        public MyVeiwHolder(TextView tv) {
            super(tv);
            rowItem = tv;
            rowItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION)
                        EmployersAdapter.this.observer.onClick(listOfEntities.get(getAdapterPosition()));
                }
            });
        }
    }

    public EmployersAdapter(List<EntityForDB> list, RecyclerViewObserver activity) {
        listOfEntities = list;
        observer = activity;
    }

    @NonNull
    @Override
    public MyVeiwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);
        return new MyVeiwHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyVeiwHolder holder, int position) {
        holder.rowItem.setText(listOfEntities.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return listOfEntities.size();
    }
}