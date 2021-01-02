package com.albertabdullin.controlwork.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComplexEntityForDB {
    private String mID;
    private int mEmployerID;
    private String mEmployerDescription;
    private int mFirmID;
    private String mFirmDescription;
    private int mPlaceOfWorkID;
    private String mPOWDescription;
    private int mTypeOfWorkID;
    private String mTOWDescription;
    private String mDate;
    private String mResult;
    private String mNote;

    public String getID() {
        return mID;
    }

    public int getEmployerID() {
        return mEmployerID;
    }

    public String getEmployerDescription() {
        return mEmployerDescription;
    }

    public int getFirmID() {
        return mFirmID;
    }

    public String getFirmDescription() {
        return mFirmDescription;
    }

    public int getPlaceOfWorkID() {
        return mPlaceOfWorkID;
    }

    public String getPOWDescription() {
        return mPOWDescription;
    }

    public int getTypeOfWorkID() {
        return mTypeOfWorkID;
    }

    public String getTOWDescription() {
        return mTOWDescription;
    }

    public String getDate() {
        return mDate;
    }

    public String getResult() {
        return mResult;
    }

    public String getNote() {
        return mNote;
    }

    public void setID(int mID) {
        this.mID = Integer.toString(mID);
    }

    public void setEmployerID(int mEmployerID) {
        this.mEmployerID = mEmployerID;
    }

    public void setEmployerDescription(String mEmployerDescription) {
        this.mEmployerDescription = mEmployerDescription;
    }

    public void setFirmID(int mFirmID) {
        this.mFirmID = mFirmID;
    }

    public void setFirmDescription(String mFirmDescription) {
        this.mFirmDescription = mFirmDescription;
    }

    public void setPlaceOfWorkID(int mPlaceOfWorkID) {
        this.mPlaceOfWorkID = mPlaceOfWorkID;
    }

    public void setPOWDescription(String mPOWDescription) {
        this.mPOWDescription = mPOWDescription;
    }

    public void setTypeOfWorkID(int mTypeOfWorkID) {
        this.mTypeOfWorkID = mTypeOfWorkID;
    }

    public void setTOWDescription(String mTOWDescription) {
        this.mTOWDescription = mTOWDescription;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public void setResult(Float mResult) {
        String regExp = ".0$";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(mResult.toString());
        if (matcher.find()) this.mResult = matcher.replaceFirst("");
        else this.mResult = mResult.toString();
    }

    public void setNote(String mNote) {
        this.mNote = mNote;
    }
}
