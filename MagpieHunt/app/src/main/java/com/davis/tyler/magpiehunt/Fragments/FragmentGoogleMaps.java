package com.davis.tyler.magpiehunt.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerAdapter;
import com.davis.tyler.magpiehunt.Adapters.CustomInfoWindowAdapter;
import com.davis.tyler.magpiehunt.CameraManager;
import com.davis.tyler.magpiehunt.DialogMoveCloser;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.SpinnerHuntFilter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FragmentGoogleMaps extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Fragment_Google_Maps";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private ImageView mGps;
    private boolean mAnimateCamera;
    private HuntManager mHuntManager;
    private LatLng locationToFocus;
    private List<Hunt> spinner_items;
    private Set<Hunt> selected_items;
    private CameraManager mCameraManager;
    private HashMap<Integer, Marker> markers;
    private boolean showInfoWindow;
    DialogMoveCloser customDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_maps, container, false);
          customDialog =new DialogMoveCloser(getActivity());


        mGps = (ImageView)view.findViewById(R.id.ic_gps);
        spinner_items = mHuntManager.getAllHunts();
        selected_items = mHuntManager.getSelectedHunts();
        Log.e(TAG, "OnCreateView");
        String headerText = "Filter";
        SpinnerHuntFilter spinner = (SpinnerHuntFilter) view.findViewById(R.id.spinner);
        //Set the max height of the dropdown spinner:
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(600);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        CheckableSpinnerAdapter adapter = new CheckableSpinnerAdapter(getContext(), headerText, spinner_items, selected_items);
        spinner.setAdapter(adapter);
        spinner.setSpinnerEventsListener((ActivityBase)getActivity());
        init();
        getLocationPermission();

        return view;
    }
    public void init(){
        mGps.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: clicked gps icon");
                mAnimateCamera = true;
                centerOnPosition();
            }
        });
    }

    public static FragmentGoogleMaps newInstance(HuntManager huntManager, CameraManager cameraManager) {
        FragmentGoogleMaps f = new FragmentGoogleMaps();
        Bundle args = new Bundle();
        args.putSerializable("huntmanager", huntManager);
        args.putSerializable("cameramanager", cameraManager);
        f.setArguments(args);
        return f;
    }

    public void setCameraOnLandmark(Badge badge){
        //moveCamera(badge.getLatLng(), DEFAULT_ZOOM, "");
        Log.e(TAG,"SETTING LANDMARK TO FOCUS ON");
        showInfoWindow = true;
        locationToFocus = badge.getLatLng();
        if(markers != null) {
            Marker m = markers.get(mHuntManager.getFocusBadge().getID());
            if(m != null)
                m.showInfoWindow();
        }
        repositionCamera();
    }
    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        mHuntManager = (HuntManager)args.getSerializable("huntmanager");
        mCameraManager = (CameraManager)args.getSerializable("cameramanager");
    }


    private void initMap(){
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }
    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                Log.e(TAG, "init map from getLocationPermission()");
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }

        }
        else{
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize our map
                    Log.e(TAG, "init map from onRequestPermissionResult()");

                    initMap();
                }
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "Map is ready");
        //Toast.makeText(getActivity(), "Map is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if(mLocationPermissionGranted){
            if(mCameraManager.getLat()!= 0)
                repositionCamera();
            else
                getDeviceLocation();
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                return;
            }
            mMap.setMyLocationEnabled(true);//marks my location on the map
            mMap.getUiSettings().setMyLocationButtonEnabled(false);// you can try out different settings
            init();
            /*Log.e(TAG, "mhunt: "+mHuntManager+" from: "+this);
            LinkedList<Hunt> hunts = mHuntManager.getAllUnCompletedHunts();
            Log.e(TAG, "starting marker placement, hunt list size: "+hunts.size());
            */
            updateFocusHunts();


            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext(), mHuntManager));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    saveCameraPosition();
                    mHuntManager.setFocusBadge(Integer.parseInt(marker.getTitle()));
                    ((IFragmentSwitcherListener)getParentFragment()).setFragment(FragmentMap.FRAGMENT_LANDMARK_INFO);

                }
            });
            if(showInfoWindow){
                showInfoWindow = false;
                Marker m = markers.get(mHuntManager.getFocusBadge().getID());
                m.showInfoWindow();
            }
            /*if(mCameraManager.shouldMoveCloser()){
                customDialog.setBadge(mHuntManager.getFocusBadge());
                customDialog.show();
                Marker m = markers.get(mHuntManager.getFocusBadge().getID());
                m.showInfoWindow();
            }*/
        }
    }


    //purpose: save camera position when moving to a new fragment
    public void saveCameraPosition(){
        mCameraManager.setLat(mMap.getCameraPosition().target.latitude);
        mCameraManager.setLon(mMap.getCameraPosition().target.longitude);
    }

    //purpose: position camera where it last was before switching fragments
    public void repositionCamera(){
        if(mCameraManager.shouldMoveCloser()){
            customDialog.setBadge(mHuntManager.getFocusBadge());
            customDialog.show();
            if(markers != null) {
                Marker m = markers.get(mHuntManager.getFocusBadge().getID());
                if(m != null)
                    m.showInfoWindow();
            }
        }
        else if(locationToFocus != null) {

            moveCamera(locationToFocus, DEFAULT_ZOOM);
            locationToFocus = null;
        }
        else if(mCameraManager.getLat() != 0) {
            Log.e(TAG, "Repositioning camera, lat: "+mCameraManager.getLat()+" lon: "+mCameraManager.getLon());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCameraManager.getLat(), mCameraManager.getLon()), DEFAULT_ZOOM));
        }
    }
    private void moveCamera(LatLng latlng, float zoom){
        if(mAnimateCamera)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom), 300, null);
        else {
            Log.e(TAG, "mmap: "+mMap+"latlng: "+latlng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        }
        mAnimateCamera = false;


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int i =0;
        if(mLocationPermissionGranted)
            i = 1;
        outState.putInt("permissions", i);
        Log.e(TAG, "ONSAVE ");
    }


    private void centerOnPosition(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try{
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        Log.e(TAG, "onComplete from centerOnPosition "+this);
                        Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);


                    }
                    else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try{
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()) {
                        Log.e(TAG, "onComplete "+this);
                        Location currentLocation = (Location) task.getResult();
                        if(locationToFocus != null) {
                            moveCamera(locationToFocus, DEFAULT_ZOOM);
                            locationToFocus = null;
                        }
                        else {
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);
                        }
                    }
                    else{
                        Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void updateFocusHunts(){
        if(mMap != null){
            mMap.clear();
            markers = new HashMap<>();
            for(Hunt h: selected_items){
                LinkedList<Badge> ll = h.getAllBadges();
                Log.e(TAG, "badge list size: "+ll.size());
                for(Badge b: ll){
                    Log.e(TAG, "adding marker");
                    MarkerOptions temp = new MarkerOptions().position(b
                            .getLatLng())
                            .title(""+b.getID());
                    markers.put(b.getID(),mMap.addMarker(temp));
                }
            }
        }
    }
}
