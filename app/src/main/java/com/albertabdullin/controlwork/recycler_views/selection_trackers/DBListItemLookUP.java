package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;

public class DBListItemLookUP extends ItemDetailsLookup<SimpleEntityForDB> {
    private RecyclerView rv;

    public DBListItemLookUP(RecyclerView rv) { this.rv = rv; }

    @Nullable
    @Override
    public ItemDetails<SimpleEntityForDB> getItemDetails(@NonNull MotionEvent e) {
        View view =  rv.findChildViewUnder(e.getX(), e.getY());
        if(view != null) {
            RecyclerView.ViewHolder vh = rv.getChildViewHolder(view);
            if(vh instanceof AdapterForItemsFromDB.MyVeiwHolder) return ((AdapterForItemsFromDB.MyVeiwHolder) vh).getItemDetails();
        }
        return null;
    }
}
