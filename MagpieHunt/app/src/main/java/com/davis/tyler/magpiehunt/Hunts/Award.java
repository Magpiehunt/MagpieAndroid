package com.davis.tyler.magpiehunt.Hunts;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Award implements Serializable {
    private int mID;
    private String mAddress;
    private String mDescription;
    private String mName;
    private String mLocationDescription;
    private double mLongitude;
    private double mLatitude;
    private String mSuperBadgeIcon;
    private String mTerms;
    private int mWorth;
    private int mValue;
    private boolean isNew;

    //TODO add terms, worth, value, to constructor after figuring out what they're for...
    //We dont need them at the moment as we are just trying to test our fragments.
    public Award(int id, String address, String desc, String name, String locationDesc,
                 double lat, double lon, String superBadgeIcon) {
        mID = id;
        mAddress = address;
        mDescription = desc;
        mName = name;
        mLocationDescription = locationDesc;
        mLongitude = lon;
        mLatitude = lat;
        mSuperBadgeIcon = superBadgeIcon;

    }

    public Award() {

    }

    public int getID() {
        return mID;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getDescription() {
        return mDescription;
    }
    public String getName() {
        return mName;
    }

    public String getLocationDescription() {
        return mLocationDescription;
    }

    public LatLng getLocation() {
        return new LatLng(mLatitude, mLongitude);
    }
    public double getLat(){return this.mLatitude;}
    public double getLong(){return this.mLongitude;}

    public String getTerms() {
        return mTerms;
    }

    public int getWorth() {
        return mWorth;
    }

    public int getValue() {
        return mValue;
    }
    public boolean getIsNew(){return isNew;}

    public String getSuperBadgeIcon() {
        return mSuperBadgeIcon;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }
    public void setIsNew(boolean b){isNew = b;}

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmLocationDescription(String mLocationDescription) {
        this.mLocationDescription = mLocationDescription;
    }


    public void setmTerms(String mTerms) {
        this.mTerms = mTerms;
    }

    public void setmWorth(int mWorth) {
        this.mWorth = mWorth;
    }

    public void setmValue(int mValue) {
        this.mValue = mValue;
    }
    public void setSuperBadgeIcon(String s){this.mSuperBadgeIcon= s;}

    public String getSuperBadgeImageFileName(){
        return "superbadgeimage"+mID;
    }
}
