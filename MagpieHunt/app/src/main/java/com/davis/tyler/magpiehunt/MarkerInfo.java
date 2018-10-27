package com.davis.tyler.magpiehunt;

import android.graphics.Bitmap;

import com.davis.tyler.magpiehunt.Hunts.Badge;

public class MarkerInfo {
    private Bitmap img;
    private Badge badge;


    public Bitmap getImg(){return  img;}
    public Badge getBadge(){return badge;}
    public void setImg(Bitmap b){img = b;}
    public void setBadge(Badge b){badge = b;}
}
