package com.albertabdullin.controlwork.models;

public class ResultTypeInfo {
    private final int mResultTypeID;
    private final String mStringViewOfRT;

    public ResultTypeInfo(int id, String stringView) {
        mResultTypeID = id;
        mStringViewOfRT = stringView;
    }

    public int getResultTypeID() {
        return mResultTypeID;
    }

    public String getStringViewOfRT() {
        return mStringViewOfRT;
    }

}
