package com.davis.tyler.magpiehunt;

import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;

public class MarkerCallback implements Callback {
    Marker marker=null;

    public MarkerCallback(Marker marker) {
        this.marker=marker;
    }


    @Override
    public void onSuccess() {
        if (marker != null && marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
            marker.showInfoWindow();
        }
    }

    @Override
    public void onError(Exception e) {

    }
}
