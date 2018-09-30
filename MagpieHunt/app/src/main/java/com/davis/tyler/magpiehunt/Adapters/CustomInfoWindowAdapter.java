package com.davis.tyler.magpiehunt.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.MarkerCallback;
import com.davis.tyler.magpiehunt.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener{

    private final String TAG = "Custom Info Window";
    private final View mWindow;
    private Context mContext;
    private ImageView mProfilePicture;
    private HuntManager mHuntManager;
    private int iconWidth=-1;
    private int iconHeight=-1;

    public CustomInfoWindowAdapter(Context mContext, HuntManager huntManager) {
        this.mContext = mContext;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window, null);
        mHuntManager = huntManager;

        iconWidth=
                mContext.getResources().getDimensionPixelSize(R.dimen.custom_info_picture_width);
        iconHeight=
                mContext.getResources().getDimensionPixelSize(R.dimen.custom_info_picture_height);

    }


    private void renderWindowText(Marker marker, View view){

        //marker title will be the badgeID
        int title = Integer.parseInt(marker.getTitle());
        TextView landmarkTitle = (TextView) view.findViewById(R.id.landmarkName);
        mProfilePicture = ((ImageView)view.findViewById(R.id.markerprofilepicture));
        Badge badge = mHuntManager.getBadgeByID(title);

            landmarkTitle.setText(badge.getName());
            //((ImageView)view.findViewById(R.id.markerprofilepicture)).setImageBitmap(badge.getLandmarkPicture());
        //((ImageView)view.findViewById(R.id.markerprofilepicture)).setImageBitmap(landmarkpic);

        System.out.println("landmark img is: "+badge.getLandmarkImage());
        ImageManager im = new ImageManager();
        im.fillCustomInfoWindowLandmark(mContext,badge,mProfilePicture,iconWidth,iconHeight, marker);

        //String snippet = marker.getSnippet();
        TextView landmarkMiles = (TextView) view.findViewById(R.id.landmarkMiles);


        landmarkMiles.setText(""+badge.getDistance());
        TextView landmarkMinutes = (TextView)view.findViewById(R.id.landmarkTime);
        landmarkMinutes.setText(""+badge.getMinutes());



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


    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "WINDOW CLICKED FROM ADAPTER");
    }
}
