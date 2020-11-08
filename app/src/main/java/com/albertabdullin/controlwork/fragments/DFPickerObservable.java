package com.albertabdullin.controlwork.fragments;

public interface DFPickerObservable {
    void setDFSignPickerObserver(DFPickerObserver dfPickerObserver);
    void notifyAboutSelection();
}
