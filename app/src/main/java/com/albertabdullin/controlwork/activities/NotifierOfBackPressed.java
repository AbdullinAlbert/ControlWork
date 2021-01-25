package com.albertabdullin.controlwork.activities;

import com.albertabdullin.controlwork.fragments.BackPressListener;

public interface NotifierOfBackPressed {
    void addListener(BackPressListener backPressListener);
    void removeListener();
    void notifyListeners();
}
