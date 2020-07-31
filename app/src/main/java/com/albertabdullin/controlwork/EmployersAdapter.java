package com.albertabdullin.controlwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;
import java.util.TreeMap;

public class EmployersAdapter extends RecyclerView.Adapter<EmployersAdapter.MyVeiwHolder> {
    private Map<Integer, Map<Integer, String>> listOfEmployers;

    static public class MyVeiwHolder extends RecyclerView.ViewHolder {
        public TextView rowItem;
        public MyVeiwHolder(TextView tv) {
            super(tv);
            rowItem = tv;
        }
    }

    public EmployersAdapter( Map<Integer, Map<Integer, String>> map) { listOfEmployers = map; }

    @NonNull
    @Override
    public MyVeiwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);
        return new MyVeiwHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVeiwHolder holder, int position) {
        TreeMap<Integer, String> map = new TreeMap<>(listOfEmployers.get(position));
        holder.rowItem.setText(map.get(map.firstKey()));
    }

    @Override
    public int getItemCount() {
        return listOfEmployers.size();
    }
}