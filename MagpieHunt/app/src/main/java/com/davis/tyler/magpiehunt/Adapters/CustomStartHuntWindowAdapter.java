package com.davis.tyler.magpiehunt.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Fragments.FragmentGoogleMaps;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomStartHuntWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener{

    private final String TAG = "Custom Info Window";
    private final View mWindow;
    private Context mContext;
    private ImageView landmarkImage;
    private HuntManager mHuntManager;
    private Button btn_wingit;
    private boolean isExpanded;
    private RelativeLayout container;
    private TextView wingitTitle;
    private TextView wingitMiles;
    private TextView wingitHrs;
    private FragmentGoogleMaps listener;

    public CustomStartHuntWindowAdapter(Context mContext, HuntManager huntManager, FragmentGoogleMaps listener) {
        this.mContext = mContext;
        mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_start_hunt, null);
        mHuntManager = huntManager;
        this.listener = listener;
    }


    private void renderWindowText(Marker marker, View view){
        isExpanded = false;
        //markertext title will be the badgeID
        int title = Integer.parseInt(marker.getTitle());
        TextView landmarkTitle = (TextView) view.findViewById(R.id.landmarkName);
        landmarkImage = ((ImageView)view.findViewById(R.id.landmarkImage));
        btn_wingit = view.findViewById(R.id.btn_wingit);
        container = view.findViewById(R.id.custom_info_container);
        final Badge badge = mHuntManager.getBadgeByID(title);
        btn_wingit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityBase)listener.getActivity()).onAddHuntEvent(mHuntManager.getHuntByID(badge.getHuntID()));
            }
        });

        landmarkTitle.setText(badge.getName());
        //((ImageView)view.findViewById(R.id.markerprofilepicture)).setImageBitmap(badge.getLandmarkPicture());
        //((ImageView)view.findViewById(R.id.markerprofilepicture)).setImageBitmap(landmarkpic);

        System.out.println("landmark img is: "+badge.getLandmarkImage());
        ImageManager im = new ImageManager();
        im.fillLandmarkImage(mContext,badge, landmarkImage, marker);

        //String snippet = markertext.getSnippet();
        TextView landmarkMiles = (TextView) view.findViewById(R.id.landmarkMiles);


        landmarkMiles.setText(""+badge.getDistance());
        TextView landmarkMinutes = (TextView)view.findViewById(R.id.landmarkTime);
        landmarkMinutes.setText(""+badge.getHours());



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
        if(!isExpanded){
            landmarkImage.animate().
                    scaleX(1.4f).
                    scaleY(1.4f).
                    setDuration(1000).start();
            isExpanded = true;

        }
        Log.d(TAG, "WINDOW CLICKED FROM ADAPTER");

    }

    public int getViewHeight(){
        return container.getHeight();
    }
}