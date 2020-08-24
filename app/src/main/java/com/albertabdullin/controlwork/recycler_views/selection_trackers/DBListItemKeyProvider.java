package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import com.albertabdullin.controlwork.models.SimpleEntityForDB;
import com.albertabdullin.controlwork.recycler_views.AdapterForItemsFromDB;


public class DBListItemKeyProvider extends ItemKeyProvider<SimpleEntityForDB> {
    private AdapterForItemsFromDB adapter;
    public DBListItemKeyProvider(AdapterForItemsFromDB adapter) {
        super(ItemKeyProvider.SCOPE_CACHED);
        this.adapter = adapter;
    }

    @Nullable
    @Override
    public SimpleEntityForDB getKey(int position) {
        return adapter.getListOfEntities().get(position);
    }

    @Override
    public int getPosition(@NonNull SimpleEntityForDB key) {
        int i = 0;
        while((i < adapter.getListOfEntities().size())) {
            if(adapter.getListOfEntities().get(i).getID() == key.getID()) return i;
            i++;
        }
        return -1;
    }

}
