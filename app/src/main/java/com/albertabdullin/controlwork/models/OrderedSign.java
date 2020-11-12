package com.albertabdullin.controlwork.models;

public class OrderedSign {
    private int mId;
    private String mChar;
    public OrderedSign(int id, String character) {
        mId = id;
        mChar = character;
    }
    public int getID() { return  mId; }
    public String getSign() { return mChar; }
}
