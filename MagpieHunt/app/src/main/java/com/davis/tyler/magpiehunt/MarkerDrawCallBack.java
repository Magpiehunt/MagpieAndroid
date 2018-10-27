package com.davis.tyler.magpiehunt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import com.davis.tyler.magpiehunt.Fragments.FragmentGoogleMaps;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;

public class MarkerDrawCallBack implements Callback {
    private FragmentGoogleMaps fragmentGoogleMaps;
    private MarkerOptions markerOptions;

    public MarkerDrawCallBack(FragmentGoogleMaps fragmentGoogleMaps, MarkerOptions markerOptions) {
        this.fragmentGoogleMaps = fragmentGoogleMaps;
        this.markerOptions = markerOptions;
    }


    @Override
    public void onSuccess() {
        //fragmentGoogleMaps.markerLoaded();
        //fragmentGoogleMaps = null
    }


    @Override
    public void onError(Exception e) {

    }
}
