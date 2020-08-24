package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

import com.albertabdullin.controlwork.models.SimpleEntityForDB;

public class EntityForDBListItemDetails extends ItemDetailsLookup.ItemDetails<SimpleEntityForDB> {
    private int position;
    private SimpleEntityForDB entity;
    
    public EntityForDBListItemDetails(SimpleEntityForDB e, int p) {
        entity = e;
        position = p;
    }
    
    @Override
    public int getPosition() { return position; }

    @Nullable
    @Override
    public SimpleEntityForDB getSelectionKey() { return entity; }
}
