package com.albertabdullin.controlwork.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComplexEntityForDB implements Parcelable {
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
    private long mLongPresentationOfDate;
    private String mResult;
    private String mNote;
    private int mResultTypeID;
    private String mStringViewOfResultType;
    private boolean mTypeResultEntity = false;
    private Float mTypeResultSum;
    private boolean mTypeOfWorkEntity = false;

    public ComplexEntityForDB() {}

    protected ComplexEntityForDB(Parcel in) {
        mID = in.readString();
        mEmployerID = in.readInt();
        mEmployerDescription = in.readString();
        mFirmID = in.readInt();
        mFirmDescription = in.readString();
        mPlaceOfWorkID = in.readInt();
        mPOWDescription = in.readString();
        mTypeOfWorkID = in.readInt();
        mTOWDescription = in.readString();
        mDate = in.readString();
        mLongPresentationOfDate = in.readLong();
        mResult = in.readString();
        mNote = in.readString();
        mResultTypeID = in.readInt();
        mStringViewOfResultType = in.readString();
        mTypeResultSum = in.readFloat();
    }

    public static final Creator<ComplexEntityForDB> CREATOR = new Creator<ComplexEntityForDB>() {
        @Override
        public ComplexEntityForDB createFromParcel(Parcel in) {
            return new ComplexEntityForDB(in);
        }

        @Override
        public ComplexEntityForDB[] newArray(int size) {
            return new ComplexEntityForDB[size];
        }
    };

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

    public void setLongPresentationOfDate(long date) { mLongPresentationOfDate = date; }

    public long getLongPresentationOfDate() { return mLongPresentationOfDate; }

    public void setResult(Float mResult) {
        String regExp = ".0$";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(mResult.toString());
        if (matcher.find()) this.mResult = matcher.replaceFirst("");
        else this.mResult = mResult.toString();
    }

    public void setResult(String mResult) {
        this.mResult = mResult;
    }

    public void setNote(String mNote) {
        this.mNote = mNote;
    }

    public void setResultTypeID(int resultTypeID) { mResultTypeID = resultTypeID; }

    public int getResultTypeID() { return mResultTypeID; }

    public void setStringViewOfResultType(String s) { mStringViewOfResultType = s; }

    public String getStringViewOfResultType() { return mStringViewOfResultType; }

    public boolean isResultEntity() { return mTypeResultEntity; }

    public void setResultEntity(boolean type) { mTypeResultEntity = type; }

    public void setTypeResultSum(Float sum) { mTypeResultSum = sum; }

    public String getStringViewOfTypeResultSum() {
        String regExp = ".0$";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(mTypeResultSum.toString());
        if (matcher.find()) return matcher.replaceFirst("");
        else return mTypeResultSum.toString();
    }

    public boolean isTypeOfWorkEntity() { return mTypeOfWorkEntity; }

    public void setTypeOfWorkEntity(boolean type) { mTypeOfWorkEntity = type; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeInt(mEmployerID);
        dest.writeString(mEmployerDescription);
        dest.writeInt(mFirmID);
        dest.writeString(mFirmDescription);
        dest.writeInt(mPlaceOfWorkID);
        dest.writeString(mPOWDescription);
        dest.writeInt(mTypeOfWorkID);
        dest.writeString(mTOWDescription);
        dest.writeString(mDate);
        dest.writeLong(mLongPresentationOfDate);
        dest.writeString(mResult);
        dest.writeString(mNote);
        dest.writeInt(mResultTypeID);
        dest.writeString(mStringViewOfResultType);
        dest.writeFloat(mTypeResultSum);
    }
}
