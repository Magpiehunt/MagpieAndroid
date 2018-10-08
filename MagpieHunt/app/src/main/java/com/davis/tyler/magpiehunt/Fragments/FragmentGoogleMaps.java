package com.davis.tyler.magpiehunt.Fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerAdapter;
import com.davis.tyler.magpiehunt.Adapters.CustomInfoWindowAdapter;
import com.davis.tyler.magpiehunt.Location.CameraManager;
import com.davis.tyler.magpiehunt.Dialogs.DialogMoveCloser;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Spinners.SpinnerHuntFilter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class FragmentGoogleMaps extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Fragment_Google_Maps";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;



    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private ImageView btn_center_camera;
    private boolean mShowInfoWindow;
    private boolean mAnimateCamera;
    private HuntManager mHuntManager;
    private LatLng mLocationToFocus;


    private RelativeLayout slidableview;
    private Button btn_slideIn, btn_slideOut;

    private CameraManager mCameraManager;
    private HashMap<Integer, Marker> markers; //key: badgeid
    private boolean slidableViewUp;
    private DialogMoveCloser customDialog;
    private Set<Hunt> selected_items;
    private SpinnerHuntFilter spinner;
    private CheckableSpinnerAdapter checkableSpinnerAdapter;

    private int width, height;
    private Bitmap defaultMarker;
    private Bitmap greyMarker;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_maps, container, false);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        customDialog =new DialogMoveCloser(getActivity());

        //Get reference to FilterSpinner and Center_Camera button
        spinner = (SpinnerHuntFilter) view.findViewById(R.id.spinner);
        btn_center_camera = (ImageView)view.findViewById(R.id.ic_gps);

        slidableViewUp = false;
        slidableview = view.findViewById(R.id.slidable_view);
        btn_slideIn = view.findViewById(R.id.btn_slide_in);
        btn_slideOut = view.findViewById(R.id.btn_slide_down);
        slidableview.setVisibility(View.INVISIBLE);

        initSlideView();

        setupFilter();

        setupMarkerGraphics();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        if(hasLocPermission()){
            getLocationPermission();
        }
        //getLocationPermission();
        initMap();
        return view;
    }

    public void initSlideView(){
        btn_slideIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidableview.setVisibility(View.VISIBLE);
                if(isSlidableViewUp()){
                    return;
                }
                AnimationSet set = new AnimationSet(true);

                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(200);
                TranslateAnimation animation = new TranslateAnimation(0, 0, slidableview.getHeight(),0);
                animation.setDuration(200);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slidableViewUp = true;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                set.addAnimation(anim);
                set.addAnimation(animation);
                slidableview.startAnimation(set);
            }
        });
        btn_slideOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSlidableViewUp()){
                    return;
                }
                AnimationSet set = new AnimationSet(true);

                Animation anim = new AlphaAnimation(1.0f, 0.0f);
                anim.setDuration(200);
                TranslateAnimation animation = new TranslateAnimation(0, 0, 0,slidableview.getHeight());
                animation.setDuration(200);

                set.addAnimation(anim);
                set.addAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        slidableViewUp = false;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slidableview.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                slidableview.startAnimation(set);
            }
        });

        //Set onclick listener to center camera when button is clicked
        btn_center_camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: clicked gps icon");
                if(!isSlidableViewUp()) {
                    mAnimateCamera = true;
                    centerOnPosition();
                }
            }
        });
    }
    public boolean isSlidableViewUp(){return slidableViewUp;}

    public void setupMarkerGraphics(){
        //Obtain screen size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Make marker width 1/15th of screen width so markers are a good size no matter the screen size
        width = size.x / 15;
        height = (int)(width * 1.4);

        //Get bitmap Drawable for default blue-green gradient marker
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.badgepin2);
        Bitmap bitmap=bitmapdraw.getBitmap();
        //Scale the bitmap so we dont waste space.
        defaultMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        //free up space for new bitmaps
        bitmap.recycle();

        //Now do the same for the grey marker.
        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_grey_marker);
        bitmap=bitmapdraw.getBitmap();
        height =(int)( width * 1.6);
        greyMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        bitmap.recycle();
    }
    public boolean hasLocPermission(){
        return preferences.getBoolean("fine", false) && preferences.getBoolean("coarse", false);
    }
    public void setupFilter(){

        selected_items = mHuntManager.getSelectedHunts();
        String headerText = "Filter";

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

        }

        checkableSpinnerAdapter = new CheckableSpinnerAdapter(getContext(), headerText, mHuntManager, selected_items);
        spinner.setAdapter(checkableSpinnerAdapter);
        spinner.setSpinnerEventsListener((ActivityBase)getActivity());
    }
    public static FragmentGoogleMaps newInstance(HuntManager huntManager, CameraManager cameraManager) {
        /*
        This is the constructor ( public 'classname'() constructors dont work, so this is a way to get references
        into the fragment at start-up
         */

        FragmentGoogleMaps f = new FragmentGoogleMaps();
        Bundle args = new Bundle();
        //args.putSerializable("huntmanager", huntManager);
        args.putSerializable("cameramanager", cameraManager);
        f.setArguments(args);
        return f;
    }
    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");
        mCameraManager = (CameraManager)args.getSerializable("cameramanager");
    }


    public void setCameraOnLandmark(Badge badge){
        Log.e(TAG,"SETTING LANDMARK TO FOCUS ON");
        /*
        When this method is called, it get to this line before map is ready, so we need
        to set a boolean flag to show the info window as soon as map is ready to be displayed
         */
        mShowInfoWindow = true;

        //Set location to focus on before calling repositionCamera()
        mLocationToFocus = badge.getLatLng();
        /*
        Find the marker in the hashmap being focused on
         */
        if(markers != null) {
            Marker m = markers.get(mHuntManager.getFocusBadge().getID());
            if(m != null)
                m.showInfoWindow();
        }

        /*if(!hasLocPermission()){
            if (mLocationToFocus != null) {

                moveCamera(mLocationToFocus, DEFAULT_ZOOM);
                mLocationToFocus = null;
            } else if (mCameraManager.getLat() != 0) {
                Log.e(TAG, "Repositioning camera, lat: " + mCameraManager.getLat() + " lon: " + mCameraManager.getLon());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCameraManager.getLat(), mCameraManager.getLon()), DEFAULT_ZOOM));
            }
        }
        else {*/
            repositionCamera();
        //}
    }
    private void initMap(){
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }


    private void getLocationPermission(){
        //Store the two permissions we need to ask for before proceeding
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};

        //Check to see we have both permissions, if not, ask for permission, else, intialize the map
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                preferences.edit().putBoolean("fine", true).apply();
                preferences.edit().putBoolean("coarse", true).apply();
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
        //This method handles the response to the permission by the user
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    /*
                    For each permission we need, check to see if there is one we dont have permission for,
                    if a single permission isnt granted, we cant initialize our map.
                     */
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            System.out.println("distance permission denied from map");
                            preferences.edit().putBoolean("fine", false).apply();
                            preferences.edit().putBoolean("coarse", false).apply();
                            return;
                        }
                    }
                    preferences.edit().putBoolean("fine", true).apply();
                    preferences.edit().putBoolean("coarse", true).apply();
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    //This loads the map after getMapAsync() is called on SupportMapFragment
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "Map is ready");

        //save reference to the map object
        mMap = googleMap;

        System.out.println("Map is: "+mMap);
        // turns off default googlemaps toolbar
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //Check if we have permission to use user location


            //if camera was already at a location, move to that location, else, center on user location
            if (mCameraManager.getLat() != 0)
                repositionCamera();
            else
                getDeviceLocation();

            //This if() is necessary to use the setMyLocationEnabled() method
        if(hasLocPermission()) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);//marks my location on the map
        }
            mMap.getUiSettings().setMyLocationButtonEnabled(false);// turn off default user location button

            //setup the center on user location button and place markers

            updateFocusHunts();

            //set up our custom info windows for when user clicks on a marker
            mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getContext(), mHuntManager));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    saveCameraPosition();
                    mHuntManager.setFocusBadge(Integer.parseInt(marker.getTitle()));
                    ((IFragmentSwitcherListener) getParentFragment()).setFragment(FragmentMap.FRAGMENT_LANDMARK_INFO);

                }
            });
            if (mShowInfoWindow) {
                mShowInfoWindow = false;
                Marker m = markers.get(mHuntManager.getFocusBadge().getID());
                m.showInfoWindow();
            }

    }



    public void saveCameraPosition(){
        /*
        Save the camera lat and lon into camera manager so that when we leave the fragment and return
        the camera will be in the same spot we left it.
         */
            mCameraManager.setLat(mMap.getCameraPosition().target.latitude);
            mCameraManager.setLon(mMap.getCameraPosition().target.longitude);

    }


    public void repositionCamera(){
        //
        if(hasLocPermission()) {
            if (mCameraManager.shouldMoveCloser()) {
                customDialog.setBadge(mHuntManager.getFocusBadge());
                customDialog.show();
                if (markers != null) {
                    Marker m = markers.get(mHuntManager.getFocusBadge().getID());
                    if (m != null)
                        m.showInfoWindow();
                }
            } else if (mLocationToFocus != null) {

                moveCamera(mLocationToFocus, DEFAULT_ZOOM);
                mLocationToFocus = null;
            } else if (mCameraManager.getLat() != 0) {
                Log.e(TAG, "Repositioning camera, lat: " + mCameraManager.getLat() + " lon: " + mCameraManager.getLon());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCameraManager.getLat(), mCameraManager.getLon()), DEFAULT_ZOOM));
            }
        }
        else{
            if (mLocationToFocus != null) {

                moveCamera(mLocationToFocus, DEFAULT_ZOOM);
                mLocationToFocus = null;
            } else if (mCameraManager.getLat() != 0) {
                Log.e(TAG, "Repositioning camera, lat: " + mCameraManager.getLat() + " lon: " + mCameraManager.getLon());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCameraManager.getLat(), mCameraManager.getLon()), DEFAULT_ZOOM));
            }
        }
    }

    /*
    This method moves the camera.
     */
    private void moveCamera(LatLng latlng, float zoom){
        //If animation is needed, make camera pan over quickly (done when clicking on landmark or centering.
        if(mAnimateCamera)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom), 300, null);
        else {
            //Have camera immediately move to position (done when map is loaded)
            Log.e(TAG, "mmap: "+mMap+"latlng: "+latlng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        }
        mAnimateCamera = false;


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int i =0;
        if(hasLocPermission())
            i = 1;
        outState.putInt("permissions", i);
        Log.e(TAG, "ONSAVE ");
    }


    /*
    This method is called when the user clicks the target button to center the camera on
    the user's current location
     */
    private void centerOnPosition(){
        Log.e(TAG, " centerOnPosition ");
        if(hasLocPermission()) {
            Log.e(TAG, "centerOnPosition has permission");
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            try {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "onComplete from centerOnPosition " + this);
                            if(hasLocPermission()) {
                                Location currentLocation = (Location) task.getResult();

                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            }

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            } catch (SecurityException e) {
                Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
            }
        }
    }

    /*
    This method will query user's current location and upon successful response, move the camera
    to that location when the map is first loaded.
     */
    private void getDeviceLocation(){
        if(hasLocPermission()) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            try {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "onComplete " + this);
                            Location currentLocation = (Location) task.getResult();
                            if (mLocationToFocus != null) {
                                moveCamera(mLocationToFocus, DEFAULT_ZOOM);
                                mLocationToFocus = null;
                            } else {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            }
                        } else {
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (SecurityException e) {
                Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
            }
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*
        This is called to clear the map and redraw all markers for the selected hunts.
     */
    public void updateFocusHunts(){
            if (mMap != null) {
                //clear the map of all markers
                mMap.clear();
                markers = new HashMap<>();

                // for each hunt in the collection of selected hunts, place marker at each badge's location
                for (Hunt h : selected_items) {
                    LinkedList<Badge> ll = h.getAllBadges();
                    Log.e(TAG, "badge list size: " + ll.size());
                    for (Badge b : ll) {
                        Log.e(TAG, "adding marker");
                        MarkerOptions temp;

                        //if badge isn't completed place the corresponding hunt color as the marker color
                        if (!b.getIsCompleted()) {
                            //If a hunt is being focused, give it the default gradient markers
                            if (mHuntManager.getSelectedHuntsSize() == 1) {
                                temp = new MarkerOptions().position(b
                                        .getLatLng())
                                        .title("" + b.getID())
                                        .icon(BitmapDescriptorFactory.fromBitmap(defaultMarker));

                            } else {
                                temp = new MarkerOptions().position(b
                                        .getLatLng())
                                        .title("" + b.getID())
                                        .icon(BitmapDescriptorFactory.fromBitmap(greyMarker));

                            }
                        } else {
                            //if the badge is completed, place grey marker
                            temp = new MarkerOptions().position(b
                                    .getLatLng())
                                    .title("" + b.getID())
                                    .icon(BitmapDescriptorFactory.defaultMarker(getHue(h.getID())));

                        }
                        markers.put(b.getID(), mMap.addMarker(temp));
                    }
                }

                //if (mHuntManager.getSelectedHuntsSize() == 1) all markers in the markers hashmap are what needs to be all on screen
            }

    }

    //temporary method to decide the hunt color.
    //later color is decided by user in the hunt collection list page
    private float getHue(int i){
        i = i%8;
        switch(i){
            case 0:
                return BitmapDescriptorFactory.HUE_ORANGE;
            case 1:
                return BitmapDescriptorFactory.HUE_MAGENTA;
            case 2:
                return BitmapDescriptorFactory.HUE_AZURE;
            case 3:
                return BitmapDescriptorFactory.HUE_VIOLET;
            case 4:
                return BitmapDescriptorFactory.HUE_YELLOW;
            case 5:
                return BitmapDescriptorFactory.HUE_GREEN;
            case 6:
                return BitmapDescriptorFactory.HUE_RED;
            case 7:
                return BitmapDescriptorFactory.HUE_ROSE;
        }

        return BitmapDescriptorFactory.HUE_ORANGE;
    }

    //updates the items in the spinner list
    public void updateSpinner(){
        //TODO eventually make this get all downloaded hunts, as undownloaded hunts may be sitting in huntmanager
        checkableSpinnerAdapter.updateSpinnerItems();
        checkableSpinnerAdapter.notifyDataSetChanged();
    }
    public void updatePermissionLocation(boolean onElseOff){
        if(onElseOff){
            if(mMap != null){
                System.out.println("permission mmap not null, turn on");
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                System.out.println("permission didnt return");

                mMap.setMyLocationEnabled(true);//marks my location on the map
            }
        }else{
            System.out.println("permission mmap not null, turn off");
            if(mMap != null){
                mMap.setMyLocationEnabled(false);
            }
        }
    }
}
