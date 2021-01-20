package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;

import com.albertabdullin.controlwork.models.ComplexEntityForDB;

public class EntityFromDBResultListItemDetails extends ItemDetailsLookup.ItemDetails<ComplexEntityForDB> {
    private final int mPosition;
    private final ComplexEntityForDB mKey;

    public EntityFromDBResultListItemDetails(ComplexEntityForDB key, int position) {
        mKey = key;
        mPosition = position;
    }

    @Override
    public int getPosition() {
        return mPosition;
    }

    @Nullable
    @Override
    public ComplexEntityForDB getSelectionKey() {
        return mKey;
    }
}
