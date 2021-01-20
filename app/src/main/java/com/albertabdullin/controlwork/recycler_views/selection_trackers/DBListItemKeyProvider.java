package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;


public class DBListItemKeyProvider extends ItemKeyProvider<SimpleEntityForDB> {
    private final AdapterForItemsFromDB adapter;
    public DBListItemKeyProvider(AdapterForItemsFromDB adapter) {
        super(ItemKeyProvider.SCOPE_CACHED);
        this.adapter = adapter;
    }

    @Nullable
    @Override
    public SimpleEntityForDB getKey(int position) {
        return adapter.getCopyListOfEntities().get(position);
    }

    @Override
    public int getPosition(@NonNull SimpleEntityForDB key) {
        int i = 0;
        while((i < adapter.getCopyListOfEntities().size())) {
            if(adapter.getCopyListOfEntities().get(i).getID() == key.getID()) return i;
            i++;
        }
        return -1;
    }

}
