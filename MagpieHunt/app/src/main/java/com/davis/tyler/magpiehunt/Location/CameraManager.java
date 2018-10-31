package com.davis.tyler.magpiehunt.Location;

import java.io.Serializable;

public class CameraManager implements Serializable {
    private boolean mMoveCloser;
    private double lat;
    private double lon;
    private float zoom;

    public CameraManager(){
        lat = 0;
        lon = 0;
        mMoveCloser = false;
        zoom = 15;
    }

    public void setLat(double l){lat = l;}
    public void setLon(double l){lon = l;}
    public void setMoveCloser(boolean b){mMoveCloser = b;}
    public void setZoom(float f){zoom = f;}

    public double getLat(){return lat;}
    public double getLon(){return lon;}
    public float getZoom(){return zoom;}
    public boolean shouldMoveCloser(){
        if(mMoveCloser){
            mMoveCloser = false;
            return true;
        }
        return false;
    }
}
