package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import com.albertabdullin.controlwork.models.ComplexEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForResultListFromQuery;

public class ItemFromResultListKeyProvider extends ItemKeyProvider<ComplexEntityForDB> {
    private final AdapterForResultListFromQuery mAdapter;

    public ItemFromResultListKeyProvider(AdapterForResultListFromQuery adapter) {
        super(ItemKeyProvider.SCOPE_CACHED);
        mAdapter = adapter;
    }

    @Nullable
    @Override
    public ComplexEntityForDB getKey(int position) {
        return mAdapter.getElement(position);
    }

    @Override
    public int getPosition(@NonNull ComplexEntityForDB key) {
        return mAdapter.getPosition(key);
    }
}
