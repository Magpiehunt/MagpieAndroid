package com.davis.tyler.magpiehunt.Fragments;

import android.Manifest;
import android.content.Context;
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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import com.davis.tyler.magpiehunt.Adapters.CustomInfoWindowAdapter;
import com.davis.tyler.magpiehunt.Adapters.CustomInfoWindowHuntAdapter;
import com.davis.tyler.magpiehunt.CMS.JSONParser;
import com.davis.tyler.magpiehunt.Dialogs.DialogHuntCompleted;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FragmentGoogleMapsHunts extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String TAG = "Fragment_Maps_tab";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;

    private HashMap<Integer, Marker> downloadedHuntMarkers; //key: huntID
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
    private ImageView img_markerbackground;
    private Drawable draw_markergrey;
    private Drawable draw_markercolor;
    private HashMap<Integer, Bitmap> superBadgeImagesLandmarks;

    private ImageView btn_toggle;


    private CameraManager mCameraManager;
    private HashMap<Integer, Marker> markers; //key: badgeid
    private DialogHuntCompleted dialogHuntCompleted;
    private SharedPreferences preferences;
    private  LatLngBounds.Builder builder;
    private CustomInfoWindowHuntAdapter customInfoWindowHuntAdapter;
    private CustomInfoWindowAdapter customInfoWindowAdapter;
    private GoogleMap.InfoWindowAdapter currentInfoWindowAdapter, custominfo;

    private CustomTargetFailedLandmark customTargetFailedLandmark;
    private CustomTargetLandmark customTargetLandmark;
    private boolean moveCameraOnHunt;

    private ViewGroup infoWindow;
    private Button btn_LetsWingit;
    private ImageView wingitLandmarkImg;
    private RelativeLayout wingitContainer;
    private TextView wingitTitle;
    private TextView wingitMiles;
    private TextView wingitHrs;
    private CustomTargetWingit wingitTarget;
    private OnInfoWindowElementTouchListener infoButtonListener;
    private MapWrapperLayout mapWrapperLayout;

    private ImageView btn_zoom_in;
    private ImageView btn_zoom_out;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_googlemaps_hunts, container, false);

        mHuntManager = ((ActivityBase)getActivity()).getData();
        mapWrapperLayout = view.findViewById(R.id.mapwrapper);
        btn_zoom_in = view.findViewById(R.id.btn_zoom_in);
        btn_zoom_out = view.findViewById(R.id.btn_zoom_out);

        btn_zoom_in.setOnClickListener(this);
        btn_zoom_out.setOnClickListener(this);
        initLetsWingIt();
        initMarkerView();

        initSuperBadgeImages();
        initSuperBadgeImagesLandmark();
        customInfoWindowHuntAdapter = new CustomInfoWindowHuntAdapter(getContext(), mHuntManager);
        customInfoWindowAdapter = new CustomInfoWindowAdapter(getContext(), mHuntManager);

        currentInfoWindowAdapter = custominfo;
        dialogHuntCompleted = new DialogHuntCompleted(getActivity());
        btn_toggle = view.findViewById(R.id.btn_toggle);
        btn_toggle.setOnClickListener(this);

        btn_center_camera = (ImageView)view.findViewById(R.id.ic_gps);

        btn_center_camera.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        if(hasLocPermission()){
            getLocationPermission();
        }
        initMap();
        return view;
    }

    public void initSuperBadgeImages(){
        superBadgeImages = new HashMap<>();
    }
    public void initSuperBadgeImagesLandmark(){
        superBadgeImagesLandmarks = new HashMap<>();
        LinkedList<Hunt> hll = mHuntManager.getAllDownloadedHunts();
        for(Hunt h: hll){
            ImageManager im = new ImageManager();
            customTargetLandmark = new CustomTargetLandmark(h);
            im.fillSuperBadgeImageIntoMarker(getContext(), h, customTargetLandmark);
        }
    }
    public void initMarkerView(){
        view_marker = (ViewGroup)getLayoutInflater().inflate(R.layout.markerimg, null);
        img_markerbadge = view_marker.findViewById(R.id.img_badge);
        img_markerbackground = view_marker.findViewById(R.id.img_markerbackground);
        draw_markercolor = getResources().getDrawable(R.drawable.badgepin2);
        draw_markergrey = getResources().getDrawable(R.drawable.defaultmarkergrey);
    }
    public void initLetsWingIt(){
        infoWindow = (ViewGroup)getLayoutInflater().inflate(R.layout.custom_info_start_hunt, null);
        wingitLandmarkImg = infoWindow.findViewById(R.id.landmarkImage);
        wingitContainer = infoWindow.findViewById(R.id.custom_info_container);
        wingitMiles = infoWindow.findViewById(R.id.landmarkMiles);
        wingitHrs = infoWindow.findViewById(R.id.landmarkTime);
        wingitTitle = infoWindow.findViewById(R.id.badgeName);
        btn_LetsWingit = infoWindow.findViewById(R.id.btn_wingit);
        custominfo = new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                // Setting up the infoWindow with current's markertext info
                int title = Integer.parseInt(marker.getTitle());
                final Badge badge = mHuntManager.getBadgeByID(title);

                wingitTitle.setText(badge.getName());
                String url = "http://206.189.204.95/landmark/image/"+badge.getLandmarkImage();

                wingitTarget = new CustomTargetWingit(marker);

                wingitLandmarkImg.setImageDrawable(null);
                Picasso.get().load(url).placeholder(getContext().getResources().getDrawable(R.drawable.background_color_white)).into(wingitTarget);


                wingitMiles.setText(""+mHuntManager.round(badge.getDistance(), 2));
                wingitHrs.setText(""+badge.getHours());
                infoButtonListener.setMarker(marker);

                // We must call this to set the current markertext and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        };

        this.infoButtonListener = new OnInfoWindowElementTouchListener(btn_LetsWingit)
        {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                //Toast.makeText(getActivity(), markertext.getTitle() + "'s button clicked!", Toast.LENGTH_SHORT).show();

                //TODO put the round method in hunt manager, and make the parameter of getdistance include precision
                Badge b = mHuntManager.getBadgeByID(Integer.parseInt(marker.getTitle()));
                Hunt h = mHuntManager.getHuntByID(b.getHuntID());
                ((ActivityBase)getActivity()).onAddHuntEvent(h);
            }
        };
        btn_LetsWingit.setOnTouchListener(infoButtonListener);
    }

    public boolean hasLocPermission(){
        //preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        System.out.println("permission map: "+preferences.getBoolean("fine", false)+" "+ preferences.getBoolean("coarse", false));
        return (preferences.getBoolean("fine", false)
                && preferences.getBoolean("coarse", false)
                && isLocationEnabled(getContext()));
    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    public static FragmentGoogleMapsHunts newInstance(CameraManager cameraManager) {
        /*
        This is the constructor ( public 'classname'() constructors dont work, so this is a way to get references
        into the fragment at start-up
         */

        FragmentGoogleMapsHunts f = new FragmentGoogleMapsHunts();
        Bundle args = new Bundle();
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

    public void initMap(){
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map2);
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
        repositionCamera();
    }
    public void setMoveCameraOnHunt(){
        moveCameraOnHunt = true;
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
            Badge b = mHuntManager.getFocusBadge();
            Marker m = markers.get(b.getID());
            if(m == null){
                populateMapWithHunt(b.getHuntID(), b);
            }
            else {
                m.showInfoWindow();
            }
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                Projection projection = mMap.getProjection();
                LatLng markerPosition = marker.getPosition();
                Point markerPoint = projection.toScreenLocation(markerPosition);
                Point targetPoint;
                if(!(currentInfoWindowAdapter instanceof CustomInfoWindowAdapter)
                        && !(currentInfoWindowAdapter instanceof CustomInfoWindowHuntAdapter)) {
                    targetPoint = new Point(markerPoint.x, markerPoint.y - wingitContainer.getHeight() / 2);
                    System.out.println("setting background white");
                    //wingitLandmarkImg.setImageDrawable(null);
                }else
                    targetPoint = new Point(markerPoint.x, markerPoint.y - customInfoWindowAdapter.getViewHeight() / 2);

                LatLng targetPosition = projection.fromScreenLocation(targetPoint);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(targetPosition), 300, null);
                saveCameraPosition();
                return true;
            }
        });
    }

    public void populateMapWithHunt(int huntid, Badge b){
        setModeHuntElseLandmark(false);
        mHuntManager.setFocusHunt(huntid);
        updateFocusHunts();
        markers.get(b.getID()).showInfoWindow();
    }
    public void setCustomInfoWindow(){
        if(mHuntManager != null) {
            if(getModeHuntElseLandmark()){
                currentInfoWindowAdapter = customInfoWindowHuntAdapter;
                mMap.setInfoWindowAdapter(customInfoWindowHuntAdapter);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        saveCameraPosition();
                        mHuntManager.setFocusHunt(Integer.parseInt(marker.getTitle()));
                        //((FragmentBirdsEyeViewContainer) getParentFragment()).setParentFragment(FragmentList.FRAGMENT_LANDMARK_INFO);
                        ((ActivityBase)getActivity()).collectionClickedMapView();
                    }
                });
            }
            else {
                if (mHuntManager.getSelectedHuntsSize() == 1) {
                    Hunt h = mHuntManager.getSingleSelectedHunt();
                    if (!h.getIsDownloaded() || h.getIsDeleted()) {
                        currentInfoWindowAdapter = custominfo;
                        mMap.setInfoWindowAdapter(custominfo);
                        mMap.setOnInfoWindowClickListener(null);
                    } else {
                        currentInfoWindowAdapter = customInfoWindowAdapter;
                        mMap.setInfoWindowAdapter(customInfoWindowAdapter);
                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {

                                saveCameraPosition();
                                mHuntManager.setFocusBadge(Integer.parseInt(marker.getTitle()));
                                //System.out.println("ACCESS PARENT FRAGMENT FROM googlemaps");
                                ((FragmentOverallHuntTabs) getParentFragment()).setParentFragment(FragmentOverallHunt.FRAGMENT_LANDMARK_INFO);

                            }
                        });
                    }
                } else {
                    currentInfoWindowAdapter = customInfoWindowAdapter;
                    mMap.setInfoWindowAdapter(customInfoWindowAdapter);
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {

                            saveCameraPosition();
                            mHuntManager.setFocusBadge(Integer.parseInt(marker.getTitle()));
                            ((FragmentOverallHuntTabs) getParentFragment()).setParentFragment(FragmentOverallHunt.FRAGMENT_LANDMARK_INFO);

                        }
                    });

                }
            }
        }
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
            if (mLocationToFocus != null) {

                moveCamera(mLocationToFocus, DEFAULT_ZOOM);
                mLocationToFocus = null;
            } else if (mCameraManager.getLat() != 0) {
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

    public void moveCameraOntoHunt(){
        if(getModeHuntElseLandmark())
            return;
        if(mHuntManager.getSelectedHuntsSize() == 1)
        {
            builder = new LatLngBounds.Builder();

            int i = 0;
            Iterator it = markers.entrySet().iterator();
            while (it.hasNext())
            {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                Marker marker = (Marker)pair.getValue();
                builder.include(marker.getPosition());
                i++;
            }
            if(hasLocPermission())
            {

                try {
                    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

                    Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful())
                            {
                                Location currentLocation = (Location) task.getResult();
                                LatLng position =  new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                builder.include(position);
                                buildBounds();
                                // emthod(builder, position);
                            } else {
                                Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                                buildBounds();
                            }


                        }
                    });
                } catch (SecurityException e) {
                    Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
                }catch (Exception e) {
                    Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
                }
            }
            else
            {
                buildBounds();
            }

        }
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
    public void updateFocusHuntMode(){
        FragmentOverallHuntTabs f = (FragmentOverallHuntTabs)getParentFragment();
        img_markerbackground.setImageDrawable(draw_markergrey);

        int curFilter = f.getCurFilter();
        if (curFilter == FragmentOverallHunt.FILTER_NEARME) {
            if(hasLocPermission()) {
                getUserCityState();
            }
            else{
                Toast.makeText(getContext(), "Turn on location permissions to see nearby hunts", Toast.LENGTH_SHORT).show();
            }
        } else if (curFilter == FragmentOverallHunt.FILTER_SEARCHED){
            for (Hunt h : mHuntManager.getmSearchHunts()) {
                setUpMarkerGenerationThreads(h);
            }
        }else if (curFilter == FragmentOverallHunt.FILTER_DOWNLOADED) {
            for (Hunt h : mHuntManager.getAllDownloadedUndeletedHunts()) {
                setUpMarkerGenerationThreads(h);
            }
        }
    }
    public void updateFocusHunts(){
        if(mMap == null)
            return;

        mMap.clear();
        markers = new HashMap<>();
        if(getModeHuntElseLandmark())
            updateFocusHuntMode();
        else
            updateFocusLandmarkMode();

    }
    public void updateFocusLandmarkMode() {
        //if (mMap != null && getContext() != null) {
            //table = new Hashtable<>();
            for (Hunt h : ((FragmentOverallHuntTabs) getParentFragment()).getSelected_items()) {

                setUpMarkerGenerationThreadsLandmarks(h);
            }
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
        int iconWidth = 20;
        int iconHeight = 20;
        if(getContext() != null){
            iconWidth =
                    getContext().getResources().getDimensionPixelSize(R.dimen.custom_marker_width);
            iconHeight =
                    getContext().getResources().getDimensionPixelSize(R.dimen.custom_marker_height);
        }
        Bitmap b = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);

        return b;
    }



    private CustomTarget customTarget;
    private CustomTargetFailed customTargetFailed;

    public void setUpMarkerGenerationThreadsLandmarks(Hunt h) {
        LinkedList<Badge> badges = h.getAllBadges();
        Bitmap bitmap = superBadgeImagesLandmarks.get(h.getID());
        if(bitmap == null){
            ImageManager im = new ImageManager();
            customTargetFailedLandmark = new CustomTargetFailedLandmark(h);
            im.fillSuperBadgeImageIntoMarker(getContext(), h, customTargetFailedLandmark);
        }else {
            img_markerbadge.setImageBitmap(bitmap);

            for (Badge b : badges) {
                if(b.getIsCompleted())
                    img_markerbackground.setImageDrawable(draw_markercolor);
                else
                    img_markerbackground.setImageDrawable(draw_markergrey);
                BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view_marker));
                MarkerOptions mo = new MarkerOptions().position(b
                        .getLatLng())
                        .title("" + b.getID())
                        .icon(bd);
                markers.put(b.getID(), mMap.addMarker(mo));
            }
            if(moveCameraOnHunt) {
                moveCameraOntoHunt();
                moveCameraOnHunt = false;
            }
        }

    }

    public void setUpMarkerGenerationThreads(Hunt h) {
        if(!getModeHuntElseLandmark()){
            return;
        }
        HuntMarkerInfo huntMarkerInfo = superBadgeImages.get(h.getID());
        if(huntMarkerInfo == null){
            huntMarkerInfo = new HuntMarkerInfo();
            superBadgeImages.put(h.getID(), huntMarkerInfo);
        }
        Bitmap bitmap = huntMarkerInfo.getBitmap();
        if(bitmap == null){
            ImageManager im = new ImageManager();
            customTargetFailed = new CustomTargetFailed(h);
            im.fillSuperBadgeImageIntoMarker(getContext(), h, customTargetFailed);
        }else {
            LatLng huntCoords = findHuntMarkerCoordinates(h);

            MarkerOptions tempMarker = new MarkerOptions().position(huntCoords).title(h.getID()+"").icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view_marker)));
            huntMarkerInfo.setMarkerOptions(tempMarker);
            img_markerbadge.setImageBitmap(bitmap);
            Hunt h1 = mHuntManager.getHuntByID(h.getID());
            if(h1 != null){
                if(h1.getIsCompleted())
                    img_markerbackground.setImageDrawable(draw_markercolor);
                else
                    img_markerbackground.setImageDrawable(draw_markergrey);
            }
            else
                img_markerbackground.setImageDrawable(draw_markergrey);

            BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view_marker));


            tempMarker.icon(bd);
            markers.put(h.getID(), mMap.addMarker(tempMarker));

        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_zoom_in:
                mMap.animateCamera(CameraUpdateFactory.zoomIn(), 100, null);
                break;
            case R.id.btn_zoom_out:
                mMap.animateCamera(CameraUpdateFactory.zoomOut(), 100, null);
                break;
            case R.id.ic_gps:
                mAnimateCamera = true;
                centerOnPosition();
                break;
            case R.id.btn_toggle:
                setModeHuntElseLandmark(!getModeHuntElseLandmark());
                updateFocusHunts();
                moveCameraOntoHunt();
                break;
        }
    }

    class CustomTarget implements Target{
        private Hunt h;
        CustomTarget(Hunt h){
            this.h = h;
        }
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            superBadgeImages.get(h.getID()).setBitmap(bitmap);
            setUpMarkerGenerationThreads(h);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    class CustomTargetFailedLandmark implements Target{
        private Hunt h;
        CustomTargetFailedLandmark(Hunt h){
            this.h = h;
        }
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            superBadgeImagesLandmarks.put(h.getID(),bitmap);
            setUpMarkerGenerationThreadsLandmarks(h);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
    class CustomTargetFailed implements Target{
        private Hunt h;
        CustomTargetFailed(Hunt h){
            this.h = h;
        }
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            superBadgeImages.get(h.getID()).setBitmap(bitmap);
            setUpMarkerGenerationThreads(h);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            setUpMarkerGenerationThreads(h);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

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
        LinkedList<Hunt> huntsInCity = nearbyHunts;


        for(int i = 0; i < huntsInCity.size(); i++)
        {
            Hunt tempHunt = huntsInCity.get(i);

            if(superBadgeImages.get(tempHunt.getID())==null){
                HuntMarkerInfo hmi = new HuntMarkerInfo();
                superBadgeImages.put(tempHunt.getID(), hmi);

            }
            setUpMarkerGenerationThreads(tempHunt);

        }
    }

    public void retrieveNearbyHunts(String city)
    {
        //LinkedList<Hunt> nearbyHunts;
        //String city = getUserCityState();
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
        if(getContext() == null)
            return;
        if(hasLocPermission())
        {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            try
            {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener()
                {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.e(TAG, "onComplete from centerOnPosition " + this);
                            Location currentLocation = (Location) task.getResult();
                            userLong = currentLocation.getLatitude();
                            userLat = currentLocation.getLongitude();
                            try {
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                                List<Address> addresses = geocoder.getFromLocation(userLong, userLat, 1);
                                cityName = addresses.get(0).getLocality();
                                retrieveNearbyHunts(cityName);
                            }
                            catch(Exception e)
                            {
                                Log.e("GEO", "Error retrieving user Lat/Long");
                            }
                        }
                        else
                        {
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

    class CustomTargetLandmark implements Target{
        private Hunt h;
        CustomTargetLandmark(Hunt h){
            this.h = h;
        }
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            superBadgeImagesLandmarks.put(h.getID(),bitmap);

        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

    class CustomTargetWingit implements Target{
        private Marker marker;
        CustomTargetWingit(Marker m){
            marker = m;
        }
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            wingitLandmarkImg.setImageBitmap(bitmap);
            if(marker != null && marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
                marker.showInfoWindow();
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }

    }
    public void setHuntCompleteNotificationMaps(Hunt h){
        dialogHuntCompleted.setHunt(h);
        dialogHuntCompleted.setCancelable(false);
        dialogHuntCompleted.show();
    }
    private void updateCameraBearing(GoogleMap mMap, Location location) {
        if ( mMap == null) return;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder(mMap.getCameraPosition())
                    .target(latLng)
                    .bearing(location.getBearing())
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);


    }

    public void updateLocation(Location location) {

        // ... process location

        updateCameraBearing(mMap, location);
    }

    private boolean getModeHuntElseLandmark(){return ((FragmentOverallHuntTabs)getParentFragment()).getModeHuntsElseLandmarks();}
    private void setModeHuntElseLandmark(boolean b){((FragmentOverallHuntTabs)getParentFragment()).setModeHuntsElseLandmarks(b);}
}

