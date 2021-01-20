package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForResultListFromQuery;

public class DBResultListItemLookup extends ItemDetailsLookup<ComplexEntityForDB> {
    private final RecyclerView mRV;

    public DBResultListItemLookup(RecyclerView rv) {
        mRV = rv;
    }

    @Nullable
    @Override
    public ItemDetails<ComplexEntityForDB> getItemDetails(@NonNull MotionEvent e) {
        View view =  mRV.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder vh = mRV.getChildViewHolder(view);
            if (vh instanceof AdapterForResultListFromQuery.MyViewHolder) return ((AdapterForResultListFromQuery.MyViewHolder) vh).getItemDetails();
        }
        return null;
    }
}
