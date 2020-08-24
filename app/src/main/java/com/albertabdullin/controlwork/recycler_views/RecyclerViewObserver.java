package com.albertabdullin.controlwork.recycler_views;

import com.albertabdullin.controlwork.recycler_views.selection_trackers.SimpleEntityForDB;

public interface RecyclerViewObserver {
    void onClick(SimpleEntityForDB eDB);
}
