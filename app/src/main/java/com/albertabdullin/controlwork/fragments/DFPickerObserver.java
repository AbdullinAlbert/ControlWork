package com.albertabdullin.controlwork.fragments;

public interface DFPickerObserver {
    void addViewToLayoutForCertainSearchCriteria(int selectedTypeOfValue, String selectedSign, int position);
    void changeLayoutForCertainSearchCriteria(int selectedTypeOfValue, int position);
    void deleteViewFormLayoutForCertainSearchCriteria(int selectedTypeOfValue, int position);
}

