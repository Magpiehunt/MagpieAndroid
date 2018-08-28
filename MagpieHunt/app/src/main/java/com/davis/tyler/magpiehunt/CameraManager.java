package com.davis.tyler.magpiehunt;

import java.io.Serializable;

public class CameraManager implements Serializable {
    private boolean mMoveCloser;
    private double lat;
    private double lon;

    public CameraManager(){
        lat = 0;
        lon = 0;
        mMoveCloser = false;
    }

    public void setLat(double l){lat = l;}
    public void setLon(double l){lon = l;}
    public void setMoveCloser(boolean b){mMoveCloser = b;}

    public double getLat(){return lat;}
    public double getLon(){return lon;}
    public boolean shouldMoveCloser(){
        if(mMoveCloser){
            mMoveCloser = false;
            return true;
        }
        return false;
    }
}
