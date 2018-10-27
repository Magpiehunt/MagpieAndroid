package com.davis.tyler.magpiehunt.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedList;

public class CustomInfoWindowHuntAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener{

    private final String TAG = "Custom Info Window";
    private final View mWindow;
    private HuntManager mHuntManager;
    private RelativeLayout container;

    public CustomInfoWindowHuntAdapter(Context mContext, HuntManager huntManager) {
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_hunt_marker, null);
        mHuntManager = huntManager;


    }


    private void renderWindowText(Marker marker, View view){

        //markertext title will be the badgeID
        int title = Integer.parseInt(marker.getTitle());
        TextView huntAcronym = (TextView) view.findViewById(R.id.txt_acronym);
        TextView huntName = view.findViewById(R.id.txt_huntname);
        TextView huntNumBadges = view.findViewById(R.id.txt_hunt_badges);
        TextView huntTime = view.findViewById(R.id.txt_hunt_time);
        container = view.findViewById(R.id.custom_info_container);
        LinkedList<Hunt> ll = mHuntManager.getAllHunts();
        Hunt hunt = null;
        for(Hunt h: ll){
            if(h.getID() == title)
                hunt = h;
        }

        huntName.setText("("+hunt.getName()+")");
        huntAcronym.setText(hunt.getAbbreviation());
        huntNumBadges.setText(""+hunt.getNumBadges());
        huntTime.setText(""+hunt.getTime());



    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
    public int getViewHeight(){
        if(container == null){
            return 100;
        }
        return container.getHeight();
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "WINDOW CLICKED FROM ADAPTER");
    }
}
