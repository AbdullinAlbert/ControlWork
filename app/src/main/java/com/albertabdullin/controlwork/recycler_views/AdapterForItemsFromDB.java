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

import java.util.ArrayList;
import java.util.List;

public class AdapterForItemsFromDB extends RecyclerView.Adapter<AdapterForItemsFromDB.MyViewHolder> implements RecyclerViewObservable{
    private final List<SimpleEntityForDB> listOfEntities;
    private RecyclerViewObserver observer;
    private SelectionTracker selectionTracker;
    private ActionMode actionMode;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView description;
        private final TextView id;
        private final ImageView imageView;
        private final View itemView;

        public MyViewHolder(View v) {
            super(v);
            itemView = v;
            description = v.findViewById(R.id.item_description);
            id = v.findViewById(R.id.item_id);
            imageView = v.findViewById(R.id.imageView_for_list);
            imageView.setVisibility(View.INVISIBLE);
            description.setOnClickListener(v1 -> {
                if ((getBindingAdapterPosition() != RecyclerView.NO_POSITION) && actionMode == null)
                    notifyRVObserver(getBindingAdapterPosition());
            });
            description.setOnLongClickListener(v12 -> {
                if (getBindingAdapterPosition() != RecyclerView.NO_POSITION) return true;
                else return false;
            });
        }

        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new EntityForDBListItemDetails(listOfEntities.get(getBindingAdapterPosition()), getBindingAdapterPosition());
        }

        public void setActivatedState(boolean b) {
            itemView.setActivated(b);
            if (b) imageView.setVisibility(View.VISIBLE);
                else imageView.setVisibility(View.INVISIBLE);
        }

        public TextView getDescription() { return description; }
        public TextView getID() {return id; }
    }

    public AdapterForItemsFromDB(List<SimpleEntityForDB> list) {
        listOfEntities = list;
    }

    public void setRVObserver(RecyclerViewObserver observer) {
        this.observer = observer;
    }

    public void notifyRVObserver(int i) {
        observer.onClick(listOfEntities.get(i));
    }

    public void setActionMode(ActionMode am) {actionMode = am; }

    public void setSelectionTracker(SelectionTracker selectionTracker) { this.selectionTracker = selectionTracker; }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_of_item_from_db_for_rv, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) { }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (selectionTracker != null) holder.setActivatedState(selectionTracker.isSelected(listOfEntities.get(position)));
        holder.getDescription().setText(listOfEntities.get(position).getDescription());
        String text = "id: " + listOfEntities.get(position).getID();
        holder.getID().setText(text);
    }

    @Override
    public int getItemCount() {
        return listOfEntities.size();
    }

    public List<SimpleEntityForDB> getCopyListOfEntities() { return new ArrayList<>(listOfEntities); }

}