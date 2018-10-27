package com.davis.tyler.magpiehunt;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.MarkerOptions;

public class HuntMarkerInfo {
    private Bitmap bitmap;
    private MarkerOptions mo;
    public Bitmap getBitmap(){return bitmap;}

    public MarkerOptions getMarkerOptions() {
        return mo;
    }
    public void setBitmap(Bitmap bitmap){this.bitmap = bitmap;}
    public void setMarkerOptions(MarkerOptions mo){this.mo = mo;}
}
