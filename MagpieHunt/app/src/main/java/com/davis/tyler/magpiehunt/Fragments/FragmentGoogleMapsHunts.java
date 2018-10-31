package com.davis.tyler.magpiehunt.Fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerAdapter;
import com.davis.tyler.magpiehunt.Adapters.CustomInfoWindowHuntAdapter;
import com.davis.tyler.magpiehunt.CMS.JSONParser;
import com.davis.tyler.magpiehunt.HuntMarkerInfo;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.Location.CameraManager;
import com.davis.tyler.magpiehunt.Dialogs.DialogMoveCloser;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.MapWrapperLayout;
import com.davis.tyler.magpiehunt.OnInfoWindowElementTouchListener;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Spinners.SpinnerHuntFilter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FragmentGoogleMapsHunts extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Fragment_Maps_tab";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;





    private ViewGroup infoWindow;
    private Button btn_LetsWingit;
    private ImageView wingitLandmarkImg;
    private RelativeLayout wingitContainer;
    private TextView wingitTitle;
    private TextView wingitMiles;
    private TextView wingitHrs;
    private OnInfoWindowElementTouchListener infoButtonListener;
    private MapWrapperLayout mapWrapperLayout;

    private HashMap<Integer, Marker> downloadedHuntMarkers; //key: huntID
    private HashMap<Integer, Marker> nearbyHuntMarkers;     //key: huntID
    private LinkedList<Hunt> nearbyHunts;
    private double userLong, userLat;
    private String cityName;

    private HashMap<Integer, HuntMarkerInfo> superBadgeImages;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    private ImageView btn_center_camera;
    private boolean mShowInfoWindow;
    private boolean mAnimateCamera;
    private HuntManager mHuntManager;
    private LatLng mLocationToFocus;
    private ViewGroup view_marker;
    private ImageView img_markerbadge;

    private Hashtable<Integer, HuntMarkerInfo> table;


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
    private  LatLngBounds.Builder builder;
    private CustomInfoWindowHuntAdapter customInfoWindowAdapter;
    private GoogleMap.InfoWindowAdapter currentInfoWindowAdapter, custominfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_maps, container, false);
        mapWrapperLayout = view.findViewById(R.id.mapwrapper);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        customDialog =new DialogMoveCloser(getActivity());

        initLetsWingIt();
        initMarkerView();
        initSuperBadgeImages();
        customInfoWindowAdapter = new CustomInfoWindowHuntAdapter(getContext(), mHuntManager);
        currentInfoWindowAdapter = custominfo;




        //Get reference to FilterSpinner and Center_Camera button
        //spinner = (SpinnerHuntFilter) view.findViewById(R.id.spinner);
        btn_center_camera = (ImageView)view.findViewById(R.id.ic_gps);





        slidableViewUp = false;
        /*
        slidableview = view.findViewById(R.id.slidable_view);
        btn_slideIn = view.findViewById(R.id.btn_slide_in);
        btn_slideOut = view.findViewById(R.id.btn_slide_down);
        slidableview.setVisibility(View.INVISIBLE);

        initSlideView();
        */
        //setupFilter();
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

        setupMarkerGraphics();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        if(hasLocPermission()){
            getLocationPermission();
        }
        //getLocationPermission();
        initMap();
        return view;
    }

    public void initSuperBadgeImages(){
        superBadgeImages = new HashMap<>();
        /*LinkedList<Hunt> hll = mHuntManager.getAllDownloadedHunts();
        for(Hunt h: hll){
            System.out.println("Hunt added to map: "+h);
            ImageManager im = new ImageManager();
            customTarget = new CustomTarget(h);
            im.fillSuperBadgeImageIntoMarker(getContext(), h, customTarget);
        }*/
    }

    public void initMarkerView(){
        view_marker = (ViewGroup)getLayoutInflater().inflate(R.layout.markerimg, null);
        img_markerbadge = view_marker.findViewById(R.id.img_badge);
    }

    public void initLetsWingIt(){
        custominfo = new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's markertext info
                int title = Integer.parseInt(marker.getTitle());
                final Badge badge = mHuntManager.getBadgeByID(title);

                wingitTitle.setText(badge.getName());
                ImageManager im = new ImageManager();
                im.fillLandmarkImage(getContext(),badge, wingitLandmarkImg, marker);


                wingitMiles.setText(""+badge.getDistance());
                wingitHrs.setText(""+badge.getHours());
                infoButtonListener.setMarker(marker);

                // We must call this to set the current markertext and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        };




        infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.custom_info_start_hunt, null);
        wingitLandmarkImg = infoWindow.findViewById(R.id.landmarkImage);
        wingitContainer = infoWindow.findViewById(R.id.custom_info_container);
        wingitMiles = infoWindow.findViewById(R.id.landmarkMiles);
        wingitHrs = infoWindow.findViewById(R.id.landmarkTime);
        wingitTitle = infoWindow.findViewById(R.id.badgeName);
        btn_LetsWingit = infoWindow.findViewById(R.id.btn_wingit);

        this.infoButtonListener = new OnInfoWindowElementTouchListener(btn_LetsWingit)
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                //Toast.makeText(getActivity(), markertext.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();

                Badge b = mHuntManager.getBadgeByID(Integer.parseInt(marker.getTitle()));
                Hunt h = mHuntManager.getHuntByID(b.getHuntID());
                ((ActivityBase)getActivity()).onAddHuntEvent(h);
            }
        };
        btn_LetsWingit.setOnTouchListener(infoButtonListener);

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

    }
    public boolean isSlidableViewUp(){return slidableViewUp;}

    public void setupMarkerGraphics(){
        //Obtain screen size
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Make markertext width 1/15th of screen width so markers are a good size no matter the screen size
        width = size.x / 15;
        height = (int)(width * 1.4);

        //Get bitmap Drawable for default blue-green gradient markertext
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.badgepin2);

        Bitmap bitmap=bitmapdraw.getBitmap();
        //Scale the bitmap so we dont waste space.
        defaultMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        //free up space for new bitmaps
        //bitmap.recycle();

        //Now do the same for the grey markertext.
        bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_grey_marker);
        bitmap=bitmapdraw.getBitmap();
        height =(int)( width * 1.6);
        greyMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        //bitmap.recycle();
    }
    public boolean hasLocPermission(){
        if(preferences != null) {
            return preferences.getBoolean("fine", false) && preferences.getBoolean("coarse", false);
        }
        return false;
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
    public static FragmentGoogleMapsHunts newInstance(HuntManager huntManager, CameraManager cameraManager) {
        /*
        This is the constructor ( public 'classname'() constructors dont work, so this is a way to get references
        into the fragment at start-up
         */

        FragmentGoogleMapsHunts f = new FragmentGoogleMapsHunts();
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
        mCameraManager.setLat(mLocationToFocus.latitude);
        mCameraManager.setLon(mLocationToFocus.longitude);
        /*
        Find the markertext in the hashmap being focused on
         */
        if(markers != null) {
            Marker m = markers.get(mHuntManager.getFocusBadge().getID());
            if(m != null)
                m.showInfoWindow();
        }
        System.out.println("Reposition camera from setcameraonlandmark()");
        repositionCamera();
    }
    public void initMap(){
        System.out.println("init map");
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        System.out.println(TAG+" onResume");
        super.onResume();
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
                //initMap();
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
                }
            }
        }
    }

    //This loads the map after getMapAsync() is called on SupportMapFragment
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(TAG, "Map is ready"+googleMap);

        //save reference to the map object
        mMap = googleMap;

        int offset=
                getContext().getResources().getDimensionPixelSize(R.dimen.custom_info_bottom_offset);
        mapWrapperLayout.init(googleMap, offset );
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

        //set up our custom info windows for when user clicks on a markertext
        setCustomInfoWindow();
        if (mShowInfoWindow) {
            mShowInfoWindow = false;
            Marker m = markers.get(mHuntManager.getFocusBadge().getID());
            m.showInfoWindow();
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                Projection projection = mMap.getProjection();
                LatLng markerPosition = marker.getPosition();
                Point markerPoint = projection.toScreenLocation(markerPosition);
                Point targetPoint;
                targetPoint = new Point(markerPoint.x, markerPoint.y - customInfoWindowAdapter.getViewHeight() / 2);

                LatLng targetPosition = projection.fromScreenLocation(targetPoint);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(targetPosition), 300, null);
                saveCameraPosition();
                return true;
            }
        });

    }

    public void setCustomInfoWindow(){
        currentInfoWindowAdapter = customInfoWindowAdapter;
        mMap.setInfoWindowAdapter(customInfoWindowAdapter);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                saveCameraPosition();
                System.out.println("ACCESS PARENT FRAGMENT FROM googlemaps");
                mHuntManager.setFocusHunt(Integer.parseInt(marker.getTitle()));
                //((FragmentBirdsEyeViewContainer) getParentFragment()).setParentFragment(FragmentList.FRAGMENT_LANDMARK_INFO);
                ((ActivityBase)getActivity()).collectionClickedMapView();
            }
        });



    }

    public void saveCameraPosition(){
        if(mMap == null)return;

        /*
        Save the camera lat and lon into camera manager so that when we leave the fragment and return
        the camera will be in the same spot we left it.
         */
        mCameraManager.setLat(mMap.getCameraPosition().target.latitude);
        mCameraManager.setLon(mMap.getCameraPosition().target.longitude);

    }


    public void repositionCamera(){
        System.out.println("Reposition camera");
            if (mLocationToFocus != null) {

                moveCamera(mLocationToFocus, DEFAULT_ZOOM);
                mLocationToFocus = null;
            } else if (mCameraManager.getLat() != 0) {
                Log.e(TAG, "Repositioning camera, lat: " + mCameraManager.getLat() + " lon: " + mCameraManager.getLon());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCameraManager.getLat(), mCameraManager.getLon()), DEFAULT_ZOOM));
            }

    }

    /*
    This method moves the camera.
     */
    private void moveCamera(LatLng latlng, float zoom){
        //If animation is needed, make camera pan over quickly (done when clicking on landmark or centering.
        if(mAnimateCamera)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom), 100, null);
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
    }


    /*
    This method is called when the user clicks the target button to center the camera on
    the user's current location
     */
    private void centerOnPosition(){
        Log.e(TAG, " centerOnPosition ");
        if(hasLocPermission()) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            try {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            if(hasLocPermission()) {
                                Location currentLocation = (Location) task.getResult();

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
    public void updatePermissionLocation(boolean onElseOff){
        if(onElseOff){
            if(mMap != null){
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    mMap.setMyLocationEnabled(true);//marks my location on the map
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            if(mMap != null){
                mMap.setMyLocationEnabled(false);
            }
        }
    }

    /*
        This is called to clear the map and redraw all markers for the selected hunts.
     */
    public void updateFocusHunts(){
        System.out.println("filter: updating focus hunts from maps ");
        System.out.println("filter: updating focus hunts from maps inside first if "+ getContext()+" "+mMap);
        //if (getContext() != null) {
            System.out.println("filter: updating focus hunts from maps inside first if "+ getContext()+" "+mMap);
            //clear the map of all markers
            FragmentOverallHuntTabs f = (FragmentOverallHuntTabs)getParentFragment();
            //if(f == null)
              //  return;

            mMap.clear();
            markers = new HashMap<>();
            System.out.println("filter: updating focus hunts");
            int curFilter = f.getCurFilter();
            if (curFilter == FragmentOverallHunt.FILTER_NEARME) {
                if(hasLocPermission()) {
                    //TODO make this work without gps
                    //make a toast possibly and clear markers?
                    System.out.println("filter: updating focus hunts near me");
                    getUserCityState();
                }
                else{
                    Toast.makeText(getContext(), "Turn on location permissions to see nearby hunts", Toast.LENGTH_SHORT).show();
                }
            } else if (curFilter == FragmentOverallHunt.FILTER_SEARCHED){
                System.out.println("filter: updating focus searched");
                for (Hunt h : mHuntManager.getmSearchHunts()) {
                    setUpMarkerGenerationThreads(h);
                }
            }else if (curFilter == FragmentOverallHunt.FILTER_DOWNLOADED) {
                System.out.println("filter: updating focus downloaded");
                for (Hunt h : mHuntManager.getAllDownloadedUndeletedHunts()) {
                    setUpMarkerGenerationThreads(h);
                }
            }
            //superBadgeImages = new HashMap<>();

            /*for(Hunt h: ((FragmentBirdsEyeViewContainer)getParentFragment()).getSelected_items()){
                System.out.println("markers: starting a hunt");
                System.out.println("markers: starting a marker generation");

                setUpMarkerGenerationThreads(h);
            }*/
            //getUserCityState();
            // for each hunt in the collection of selected hunts, place markertext at each badge's location

        //}

    }
    public void buildBounds(){
        try
        {
            LatLngBounds bounds = builder.build();
            int padding = getResources().getDimensionPixelSize(R.dimen.maps_zoom_offset);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom), 300, null);
            mMap.animateCamera(cu, 500, null);
        }
        catch(IllegalStateException e)
        {
            Log.e("NOPOINTS", "No points have been included.");
        }
        finally
        {
            //do nothing
        }
    }

    /*public void publishMarkers(){
        Set<Integer> keys = table.keySet();
        for(Integer key: keys){
            //MarkerInfo mi = table.get(key);
            img_markerbadge.setImageBitmap(mi.getImg());
            Badge b = mi.getBadge();
            MarkerOptions mo;
            mo = new MarkerOptions().position(b
                    .getLatLng())
                    .title("" + b.getID())
                    .icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view_marker)));
            markers.put(b.getID(), mMap.addMarker(mo));

        }
    }*/
    //updates the items in the spinner list
    public void updateSpinner(){
        //TODO eventually make this get all downloaded hunts, as undownloaded hunts may be sitting in huntmanager
        checkableSpinnerAdapter.updateSpinnerItems();
        checkableSpinnerAdapter.notifyDataSetChanged();
    }


    public Bitmap loadBitmapFromView(View v) {

        if (v.getMeasuredHeight() <= 0) {
            v.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//            if (b != null && !b.isRecycled()) {
//                b.recycle();
//                b = null;
//            }
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        int iconWidth =
                getContext().getResources().getDimensionPixelSize(R.dimen.custom_marker_width);
        int iconHeight =
                getContext().getResources().getDimensionPixelSize(R.dimen.custom_marker_height);
        Bitmap b = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);

        return b;
    }



    private CustomTarget customTarget;
    private CustomTargetFailed customTargetFailed;



    public void setUpMarkerGenerationThreads(Hunt h) {

        System.out.println("hashmap: bitmap checking: "+h.getName());
        HuntMarkerInfo huntMarkerInfo = superBadgeImages.get(h.getID());
        if(huntMarkerInfo == null){
            huntMarkerInfo = new HuntMarkerInfo();
            superBadgeImages.put(h.getID(), huntMarkerInfo);
        }
        Bitmap bitmap = huntMarkerInfo.getBitmap();
        if(bitmap == null){
            System.out.println("hashmap: bitmap null: "+h.getName());
            ImageManager im = new ImageManager();
            customTargetFailed = new CustomTargetFailed(h);
            im.fillSuperBadgeImageIntoMarker(getContext(), h, customTargetFailed);
        }else {
            LatLng huntCoords = findHuntMarkerCoordinates(h);

            MarkerOptions tempMarker = new MarkerOptions().position(huntCoords).title(h.getID()+"").icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view_marker)));
            huntMarkerInfo.setMarkerOptions(tempMarker);
            System.out.println("hashmap: bitmap not null: "+h.getName());
            img_markerbadge.setImageBitmap(bitmap);
            BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view_marker));


            tempMarker.icon(bd);
            markers.put(h.getID(), mMap.addMarker(tempMarker));
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                    System.out.println("marker drag");
                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {

                }
            });
        }

    }
    class CustomTarget implements Target{
        private Hunt h;
        CustomTarget(Hunt h){
            System.out.println("Hunt added to constructor: "+h.getID());
            this.h = h;
        }
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            System.out.println("Hunt added to: "+h.getID());
            //superBadgeImages.put(h.getID(),);
            System.out.println("getting hunt from hashmap: "+h.getName());
            superBadgeImages.get(h.getID()).setBitmap(bitmap);
            setUpMarkerGenerationThreads(h);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            System.out.println("Hunt added to: failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            System.out.println("Hunt added to onprepareload: "+h.getID());

        }
    }


    class CustomTargetFailed implements Target{
        private Badge badge;
        private MarkerOptions mo;
        private Hunt h;
        /*CustomTarget(Badge b, MarkerOptions mo){
            badge = b;
            this.mo = mo;
        }*/
        CustomTargetFailed(Hunt h){
            System.out.println("Hunt added to constructor: "+h.getID());
            this.h = h;
        }
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                /*img_markerbadge.setImageBitmap(bitmap);
                mo.icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view_marker)));
                markers.put(badge.getID(), mMap.addMarker(mo));*/
            System.out.println("Hunt added to: "+h.getID());
            System.out.println("getting hunt from hashmap: "+h.getName());
            superBadgeImages.get(h.getID()).setBitmap(bitmap);
            setUpMarkerGenerationThreads(h);
            //markers.put(h.getID(), mMap.addMarker(superBadgeImages.get(h.getID()).getMarkerOptions()));
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            System.out.println("Hunt added to: failed");
            setUpMarkerGenerationThreads(h);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            System.out.println("Hunt added to onprepareload: "+h.getID());

        }
    }

    public void createDownloadedHuntMarkers()
    {
        downloadedHuntMarkers = new HashMap<>();
        LinkedList<Hunt> downloadedHunts = mHuntManager.getAllHunts();
        LatLng huntCoords;

        for(int i = 0; i < downloadedHunts.size(); i++)
        {
            Hunt tempHunt = downloadedHunts.get(i);
            huntCoords = findHuntMarkerCoordinates(downloadedHunts.get(i));

            MarkerOptions tempMarker = new MarkerOptions().position(huntCoords).title(tempHunt.getID()+"").icon(BitmapDescriptorFactory.fromBitmap(defaultMarker));
            downloadedHuntMarkers.put(tempHunt.getID(), mMap.addMarker(tempMarker));
        }

    }

    public LatLng findHuntMarkerCoordinates(Hunt hunt)
    {
        double huntLat = 0.0;
        double huntLong = 0.0;
        LinkedList<Badge> huntBadges = hunt.getAllBadges();


        for(int i = 0; i < huntBadges.size(); i++)
        {
            huntLat += huntBadges.get(i).getLatitude();
            huntLong += huntBadges.get(i).getLongitude();
        }

        huntLat = huntLat / huntBadges.size();
        huntLong = huntLong / huntBadges.size();

        LatLng markerCoords = new LatLng(huntLat, huntLong);

        return markerCoords;
    }

    public void createNearbyHuntMarkers()
    {
        if(getContext() == null)
            return;
        if(!hasLocPermission()){
            return;
        }
        nearbyHuntMarkers = new HashMap<>();
        LinkedList<Hunt> huntsInManager = mHuntManager.getAllHunts();
        LinkedList<Hunt> huntsInCity = nearbyHunts;
        LatLng huntCoords;


        for(int i = 0; i < huntsInCity.size(); i++)
        {
            Hunt tempHunt = huntsInCity.get(i);
            huntCoords = findHuntMarkerCoordinates(tempHunt);

            //MarkerOptions tempMarker = new MarkerOptions().position(huntCoords).title(tempHunt.getID()+"").icon(BitmapDescriptorFactory.fromBitmap(defaultMarker));
            //nearbyHuntMarkers.put(tempHunt.getID(), mMap.addMarker(tempMarker));
            table = new Hashtable<>();
            //setUpMarkerGenerationThreads(tempHunt, tempMarker);
            if(superBadgeImages.get(tempHunt.getID())==null){
                HuntMarkerInfo hmi = new HuntMarkerInfo();
                superBadgeImages.put(tempHunt.getID(), hmi);

            }
            setUpMarkerGenerationThreads(tempHunt);

            System.out.println("huntname: "+tempHunt.getName());
        }
    }

    public void retrieveNearbyHunts(String city)
    {
        //LinkedList<Hunt> nearbyHunts;
        //String city = getUserCityState();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        System.out.println("city result: "+city);
        JsonArrayRequest request = new JsonArrayRequest("http://206.189.204.95/api/v3/hunts/city/"+city,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        //this will return list of hunts
                        Log.e(TAG, "Response gained");
                        JSONParser jsonparser = new JSONParser(jsonArray);
                        nearbyHunts = jsonparser.getAllHunts();
                        //mHuntManager.setmSearchHunts(hunts);
                        if(nearbyHunts == null){
                            Toast.makeText(getContext(), "No hunts found, please try again...", Toast.LENGTH_SHORT).show();
                            nearbyHunts = new LinkedList<Hunt>();

                        }
                        else {
                            //updateList();

                        }
                        mHuntManager.addAllHunts(nearbyHunts);
                        mHuntManager.setmSearchNearbyHunts(nearbyHunts);
                        createNearbyHuntMarkers();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //searchByState(city);
                    }
                });
        queue.add(request);

    }

    public void getUserCityState()
    {
        if(hasLocPermission())
        {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            try
            {
                Task location = mFusedLocationProviderClient.getLastLocation();
                System.out.println("getusercity: 1");
                location.addOnCompleteListener(new OnCompleteListener()
                {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        System.out.println("getusercity: 2");
                        if (task.isSuccessful())
                        {
                            System.out.println("getusercity: 3");
                            Log.e(TAG, "onComplete from centerOnPosition " + this);
                            Location currentLocation = (Location) task.getResult();
                            userLong = currentLocation.getLatitude();
                            userLat = currentLocation.getLongitude();

                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                            try {
                                System.out.println("getusercity: 4");
                                List<Address> addresses = geocoder.getFromLocation(userLong, userLat, 1);
                                cityName = addresses.get(0).getLocality();
                                retrieveNearbyHunts(cityName);
                                System.out.println("getusercity: 5");
                                Log.d("ADDRESS", cityName);
                            }
                            catch(IOException e)
                            {
                                Log.e("GEO", "Error retrieving user Lat/Long");
                            }
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                            buildBounds();
                        }
                    }
                });
            }
            catch (SecurityException e)
            {
                Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
            }
        }

    }


}

