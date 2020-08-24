package com.albertabdullin.controlwork.recycler_views.selection_trackers;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleEntityForDB implements Parcelable {
    private int id;
    private String description;

    public SimpleEntityForDB() { }

    protected SimpleEntityForDB(Parcel in) {
        id = in.readInt();
        description = in.readString();
    }

    public static final Creator<SimpleEntityForDB> CREATOR = new Creator<SimpleEntityForDB>() {
        @Override
        public SimpleEntityForDB createFromParcel(Parcel in) {
            return new SimpleEntityForDB(in);
        }

        @Override
        public SimpleEntityForDB[] newArray(int size) {
            return new SimpleEntityForDB[size];
        }
    };

    public void setId(int i) {
        id = i;
    }

    public void setDescription(String s) {
        description = s;
    }

    public int getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(description);
    }
}
