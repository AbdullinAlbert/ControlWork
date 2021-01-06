package com.albertabdullin.controlwork.models;

import android.view.View;

public class PairOfItemPositions {
    private int mNewPos = -1;
    private int mOldPos = -1;

    public PairOfItemPositions(int mNewPos) {
        this.mNewPos = mNewPos;
    }

    public PairOfItemPositions(PairOfItemPositions pair) {
        this.mNewPos = pair.getNewPos();
    }

    public int getNewPos() {
        return mNewPos;
    }

    public void setNewPos(PairOfItemPositions pair) {
        this.mOldPos = this.mNewPos;
        this.mNewPos = pair.getNewPos();
    }

    public int getOldPos() { return mOldPos; }

    public void setDefaultValueToOldPos() {
        mOldPos = -1;
    }

}
