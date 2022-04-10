package com.example.pam_gmaps;

import android.os.Parcelable;

import java.sql.Timestamp;

public class DataModel  {
    String name,loc;
//    Timestamp createdDate;

    public DataModel(){ }

    public DataModel(String name, String loc) {
        this.name = name;
        this.loc = loc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
