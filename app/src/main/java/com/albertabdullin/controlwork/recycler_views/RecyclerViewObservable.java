package com.albertabdullin.controlwork.recycler_views;

public interface RecyclerViewObservable {
    void setRVObserver(RecyclerViewObserver observer);
    void notifyRVObserver(int i);
}
