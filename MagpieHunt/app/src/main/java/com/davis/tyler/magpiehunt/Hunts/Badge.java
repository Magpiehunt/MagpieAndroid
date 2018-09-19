package com.davis.tyler.magpiehunt.Hunts;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Badge implements Serializable, Comparable<Badge>{
    /*TODO to save memory, this wont store bitmaps of images, but instead, only the string "path"
    to the image on the phone to save memory.
     */

    private int mID;
    private String mDescription;
    private String mBadgeIcon;
    private String mLandmarkImage;
    private String mLandmarkName;
    private double mLongitude;
    private double mLatitude;
    private String mName;
    private boolean mIsCompleted;
    private double mDistance;
    private int mHuntID;
    private String mQRurl;

    public Badge(int id, String description, String icon, String lmName, double lat,
                 double lon, String name, boolean isCompleted, int huntID, String landmarkPic){
        mID = id;
        mDescription = description;
        mBadgeIcon = icon;
        mLandmarkImage = landmarkPic;
        mLandmarkName = lmName;
        mLatitude = lat;
        mLongitude = lon;
        mName = name;
        mIsCompleted = isCompleted;
        mHuntID = huntID;
    }
    public Badge()
    {

    }


    public void setDistance(double d){
        mDistance = d;
    }
    public void setQRurl(String str){mQRurl = str;}

    public int getID(){return mID;}
    public String getDescription(){return mDescription;}
    public String getIcon(){return mBadgeIcon;}
    public String getLandmarkName(){return mLandmarkName;}
    public LatLng getLatLng(){return new LatLng(mLatitude, mLongitude);}
    public String getName(){return mName;}
    public boolean getIsCompleted(){return mIsCompleted;}
    public int getHuntID(){return mHuntID;}
    public String getLandmarkImage(){return mLandmarkImage;}
    public double getLongitude(){return mLongitude;}
    public double getLatitude(){return mLatitude;}
    public double getDistance(){return mDistance;}
    public int getMinutes(){return (int)(mDistance * 20);}
    public void setLocation(Location l){
        l.setLatitude(mLatitude);
        l.setLongitude(mLongitude);
    }

    public void setmLatitude(double d) {this.mLatitude = d;}
    public void setmLongitude(double d) {this.mLongitude = d;}

    public String getQRurl(){return mQRurl;}

    public int compareTo(Badge b)
    {
        double bdistance = b.getDistance();
        if(this.mDistance < bdistance)
            return -1;
        else if(this.mDistance > bdistance)
            return 1;
        else
            return 0;
    }


    public void setmID(int mID) {
        this.mID = mID;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setmBadgeIcon(String mBadgeIcon) {
        this.mBadgeIcon = mBadgeIcon;
    }

    public void setmLandmarkImage(String mLandmarkImage) {
        this.mLandmarkImage = mLandmarkImage;
    }

    public void setmLandmarkName(String mLandmarkName) {
        this.mLandmarkName = mLandmarkName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmIsCompleted(boolean mIsCompleted) {
        this.mIsCompleted = mIsCompleted;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public void setmHuntID(int mHuntID) {
        this.mHuntID = mHuntID;
    }

}
