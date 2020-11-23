package com.albertabdullin.controlwork.fragments;

public interface DFPickerObserver {
    void addViewToLayoutForCertainCriteria(String selectedSign, int position);
    void changeLayoutForCertainCriteria(int position);
    void deleteViewFormLayoutForCertainCriteria(int position);
}

