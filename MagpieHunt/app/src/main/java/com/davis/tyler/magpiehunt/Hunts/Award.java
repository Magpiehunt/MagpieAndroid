package com.davis.tyler.magpiehunt.Hunts;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Award implements Serializable{
    private int mID;
    private String mAddress;
    private String mDescription;
    private String mName;
    private String mLocationDescription;
    private double mLongitude;
    private double mLatitude;
    private String mSuperBadge;
    private String mTerms;
    private String mWorth;
    private String mValue;

    //TODO add terms, worth, value, to constructor after figuring out what they're for...
    //We dont need them at the moment as we are just trying to test our fragments.
    public Award(int id, String address, String desc, String name, String locationDesc,
                 double lat, double lon, String superBadge){
        mID = id;
        mAddress = address;
        mDescription = desc;
        mName = name;
        mLocationDescription = locationDesc;
        mLongitude = lon;
        mLatitude = lat;
        mSuperBadge = superBadge;

    }
    public int getID(){return mID;}
    public String getAddress(){return mAddress;}
    public String getDescription(){return mDescription;}
    public String getName(){return mName;}
    public String getLocationDescription(){return mLocationDescription;}
    public LatLng getLocation() { return new LatLng(mLatitude, mLongitude); }
    public String getTerms(){return mTerms;}
    public String getWorth(){return mWorth;}
    public String getValue(){return mValue;}
    public String getSuperBadge(){return mSuperBadge;}
}
