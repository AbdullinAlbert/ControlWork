package com.albertabdullin.controlwork.recycler_views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.R;
import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.selection_trackers.EntityForDBListItemDetails;

import java.util.List;

public class AdapterForItemsFromDB extends RecyclerView.Adapter<AdapterForItemsFromDB.MyVeiwHolder> {
    private List<SimpleEntityForDB> listOfEntities;
    private RecyclerViewObserver observer;
    private SelectionTracker selectionTracker;
    private ActionMode actionMode;

    public class MyVeiwHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private TextView id;
        private ImageView imageView;
        private View itemView;

        public MyVeiwHolder(View v) {
            super(v);
            itemView = v;
            description = v.findViewById(R.id.item_description);
            id = v.findViewById(R.id.item_id);
            imageView = v.findViewById(R.id.imageView_for_list);
            imageView.setVisibility(View.INVISIBLE);
            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((getBindingAdapterPosition() != RecyclerView.NO_POSITION) && actionMode == null)
                        AdapterForItemsFromDB.this.observer.onClick(listOfEntities.get(getBindingAdapterPosition()));
                }
            });
            description.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (getBindingAdapterPosition() != RecyclerView.NO_POSITION) return true;
                    else return false;
                }
            });
        }

        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new EntityForDBListItemDetails(listOfEntities.get(getBindingAdapterPosition()), getBindingAdapterPosition());
        }

        public void setActivatedState(boolean b) {
            itemView.setActivated(b);
            if(b) imageView.setVisibility(View.VISIBLE);
                else imageView.setVisibility(View.INVISIBLE);
        }

        public TextView getDescription() { return description; }
        public TextView getID() {return id; }
    }

    public AdapterForItemsFromDB(List<SimpleEntityForDB> list, RecyclerViewObserver activity) {
        listOfEntities = list;
        observer = activity;
    }

    public void setActionMode(ActionMode am) {actionMode = am; }

    public void setSelectionTracker(SelectionTracker selectionTracker) { this.selectionTracker = selectionTracker; }

    @NonNull
    @Override
    public MyVeiwHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_of_recyclerview, parent, false);
        return new MyVeiwHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVeiwHolder holder, int position) { }

    @Override
    public void onBindViewHolder(@NonNull MyVeiwHolder holder, int position, @NonNull List<Object> payloads) {
        holder.setActivatedState(selectionTracker.isSelected(listOfEntities.get(position)));
        holder.getDescription().setText(listOfEntities.get(position).getDescription());
        holder.getID().setText("id: " + Integer.toString(listOfEntities.get(position).getID()));
    }

    @Override
    public int getItemCount() {
        return listOfEntities.size();
    }

    public List<SimpleEntityForDB> getListOfEntities() { return listOfEntities; }

}