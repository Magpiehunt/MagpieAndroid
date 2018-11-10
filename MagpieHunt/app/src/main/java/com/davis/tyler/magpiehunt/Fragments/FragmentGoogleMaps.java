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

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerAdapter;
import com.davis.tyler.magpiehunt.Adapters.CustomInfoWindowAdapter;
import com.davis.tyler.magpiehunt.Dialogs.DialogHuntCompleted;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.Location.CameraManager;
import com.davis.tyler.magpiehunt.Dialogs.DialogMoveCloser;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.MapWrapperLayout;
import com.davis.tyler.magpiehunt.MarkerInfo;
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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;

public class FragmentGoogleMaps extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Fragment_Google_Maps";
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 19;


    private ViewGroup infoWindow;
    private Button btn_LetsWingit;
    private ImageView wingitLandmarkImg;
    private RelativeLayout wingitContainer;
    private TextView wingitTitle;
    private TextView wingitMiles;
    private TextView wingitHrs;
    private OnInfoWindowElementTouchListener infoButtonListener;
    private MapWrapperLayout mapWrapperLayout;



    private CyclicBarrier cyclicBarrier;
    private Hashtable<Integer, MarkerInfo> table;
    private LinkedList<Badge> badges;

    private HashMap<Integer, Bitmap> superBadgeImages;

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



    private RelativeLayout slidableview;
    private Button btn_slideIn, btn_slideOut;

    private CameraManager mCameraManager;
    private HashMap<Integer, Marker> markers; //key: badgeid
    private boolean slidableViewUp;
    private DialogMoveCloser customDialog;
    private DialogHuntCompleted dialogHuntCompleted;
    private Set<Hunt> selected_items;
    private SpinnerHuntFilter spinner;
    private CheckableSpinnerAdapter checkableSpinnerAdapter;

    private int width, height;
    private Bitmap defaultMarker;
    private Bitmap greyMarker;
    private SharedPreferences preferences;
    private  LatLngBounds.Builder builder;
    private CustomInfoWindowAdapter customInfoWindowAdapter;
    private GoogleMap.InfoWindowAdapter currentInfoWindowAdapter, custominfo;
    private boolean moveCameraOnHunt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_maps, container, false);
        mapWrapperLayout = view.findViewById(R.id.mapwrapper);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        customDialog =new DialogMoveCloser(getActivity());
        dialogHuntCompleted = new DialogHuntCompleted(getActivity());
        initLetsWingIt();
        initMarkerView();
        initSuperBadgeImages();
        customInfoWindowAdapter = new CustomInfoWindowAdapter(getContext(), mHuntManager);
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
        LinkedList<Hunt> hll = mHuntManager.getAllDownloadedHunts();
        for(Hunt h: hll){
            System.out.println("Hunt added to map: "+h);
            ImageManager im = new ImageManager();
            customTarget = new CustomTarget(h);
            im.fillSuperBadgeImageIntoMarker(getContext(), h, customTarget);
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


                wingitMiles.setText(""+round(badge.getDistance(), 2));
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

                //TODO put the round method in hunt manager, and make the parameter of getdistance include precision
                Badge b = mHuntManager.getBadgeByID(Integer.parseInt(marker.getTitle()));
                Hunt h = mHuntManager.getHuntByID(b.getHuntID());
                ((ActivityBase)getActivity()).onAddHuntEvent(h);
            }
        };
        btn_LetsWingit.setOnTouchListener(infoButtonListener);
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
            catch (Exception e){
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


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
        Log.e(TAG, "Map is ready");

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
                    if(!(currentInfoWindowAdapter instanceof CustomInfoWindowAdapter)) {
                        targetPoint = new Point(markerPoint.x, markerPoint.y - wingitContainer.getHeight() / 2);
                    }else
                        targetPoint = new Point(markerPoint.x, markerPoint.y - customInfoWindowAdapter.getViewHeight() / 2);

                    LatLng targetPosition = projection.fromScreenLocation(targetPoint);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(targetPosition), 300, null);
                    saveCameraPosition();
                    return true;
                }
            });

    }

    public void setCustomInfoWindow(){
        if(mHuntManager != null) {

            if(mHuntManager.getSelectedHuntsSize() == 1){
                Hunt h = mHuntManager.getSingleSelectedHunt();
                if (!h.getIsDownloaded() || h.getIsDeleted()) {
                    currentInfoWindowAdapter = custominfo;
                    mMap.setInfoWindowAdapter(custominfo);
                /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker markertext) {
                        Projection projection = mMap.getProjection();
                        LatLng markerPosition = markertext.getPosition();
                        Point markerPoint = projection.toScreenLocation(markerPosition);
                        Point targetPoint = new Point(markerPoint.x, markerPoint.y - customStartHuntWindowAdapter.getViewHeight() / 2);
                        LatLng targetPosition = projection.fromScreenLocation(targetPoint);
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(targetPosition), 100, null);
                        saveCameraPosition();
                    }
                });*/
                    mMap.setOnInfoWindowClickListener(null);
                }
                else{
                    currentInfoWindowAdapter = customInfoWindowAdapter;
                    mMap.setInfoWindowAdapter(customInfoWindowAdapter);
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {

                            saveCameraPosition();
                            mHuntManager.setFocusBadge(Integer.parseInt(marker.getTitle()));
                            System.out.println("ACCESS PARENT FRAGMENT FROM googlemaps");
                            ((FragmentBirdsEyeViewContainer) getParentFragment()).setParentFragment(FragmentList.FRAGMENT_LANDMARK_INFO);

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
                        ((FragmentBirdsEyeViewContainer) getParentFragment()).setParentFragment(FragmentList.FRAGMENT_LANDMARK_INFO);

                    }
                });

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
            mCameraManager.setZoom(mMap.getCameraPosition().zoom);

    }


    public void repositionCamera(){
        System.out.println("Reposition camera");
        if(hasLocPermission()) {
            if (mCameraManager.shouldMoveCloser()) {
                System.out.println("inside should move closer if");
                moveCamera(mLocationToFocus, DEFAULT_ZOOM);

                customDialog.setBadge(mHuntManager.getFocusBadge(), getContext());
                customDialog.show();
                if (markers != null) {
                    System.out.println("inside should move closer if markertext map not null");
                    Marker m = markers.get(mHuntManager.getFocusBadge().getID());
                    if (m != null) {
                        System.out.println("inside should move closer if markertext not null");
                        m.showInfoWindow();
                    }
                }
            } else if (mLocationToFocus != null) {

                moveCamera(mLocationToFocus, DEFAULT_ZOOM);
                mLocationToFocus = null;
            } else if (mCameraManager.getLat() != 0) {
                Log.e(TAG, "Repositioning camera, lat: " + mCameraManager.getLat() + " lon: " + mCameraManager.getLon());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCameraManager.getLat(), mCameraManager.getLon()), mCameraManager.getZoom()));
            }
        }
        else{
            if (mLocationToFocus != null) {

                moveCamera(mLocationToFocus, DEFAULT_ZOOM);
                mLocationToFocus = null;
            } else if (mCameraManager.getLat() != 0) {
                Log.e(TAG, "Repositioning camera, lat: " + mCameraManager.getLat() + " lon: " + mCameraManager.getLon());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCameraManager.getLat(), mCameraManager.getLon()), mCameraManager.getZoom()));
            }
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

    public void setMoveCameraOnHunt(){
        moveCameraOnHunt = true;
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
        Toast.makeText(getContext(), "Failed to load map", Toast.LENGTH_SHORT).show();
    }

    /*
        This is called to clear the map and redraw all markers for the selected hunts.
     */
    public void updateFocusHunts(){
            if (mMap != null && getContext() != null) {
                //clear the map of all markers
                mMap.clear();
                markers = new HashMap<>();
                table = new Hashtable<>();
                for(Hunt h: ((FragmentBirdsEyeViewContainer)getParentFragment()).getSelected_items()){
                    System.out.println("markers: starting a hunt");
                    System.out.println("markers: starting a marker generation");

                    setUpMarkerGenerationThreads(h);
                }
                //setCustomInfoWindow();
                //setUpMarkerGenerationThreads(badges.size(), badges);

                // for each hunt in the collection of selected hunts, place markertext at each badge's location
                /*for (Hunt h : ((FragmentBirdsEyeViewContainer)getParentFragment()).getSelected_items()) {
                    LinkedList<Badge> ll = h.getAllBadges();
                    Log.e(TAG, "badge list size: " + ll.size());
                    for (Badge b : ll) {
                        Log.e(TAG, "adding markertext");
                        MarkerOptions temp;

                        //if badge isn't completed place the corresponding hunt color as the markertext color
                        if (!b.getIsCompleted()) {
                            //If a hunt is being focused, give it the default gradient markers
                            if (mHuntManager.getSelectedHuntsSize() == 1 && !mHuntManager.getSingleSelectedHunt().getIsDownloaded()) {
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
                            if(mHuntManager.getSelectedHuntsSize() == 1){
                                temp = new MarkerOptions().position(b
                                        .getLatLng())
                                        .title("" + b.getID())
                                        .icon(BitmapDescriptorFactory.fromBitmap(defaultMarker));
                            }
                            else {
                                temp = new MarkerOptions().position(b
                                        .getLatLng())
                                        .title("" + b.getID())
                                        .icon(BitmapDescriptorFactory.defaultMarker(getHue(h.getID())));
                            }

                        }
                        markers.put(b.getID(), mMap.addMarker(temp));
                    }
                }*/

                //if (mHuntManager.getSelectedHuntsSize() == 1) all markers in the markers hashmap are what needs to be all on screen
            }



    }
    public void moveCameraOntoHunt(){
        System.out.println("movetohunt: moving to hunt");
        if(mHuntManager.getSelectedHuntsSize() == 1)
        {
            builder = new LatLngBounds.Builder();
            Log.d("MARKER", markers.size() + "");

            int i = 0;
            Iterator it = markers.entrySet().iterator();
            while (it.hasNext())
            {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                Marker marker = (Marker)pair.getValue();
                Log.d("MARKER", "Iteration: " + i);
                Log.d("POINT", marker.getPosition() + "");
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
                                Log.e(TAG, "onComplete from centerOnPosition " + this);
                                Location currentLocation = (Location) task.getResult();
                                LatLng position =  new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                builder.include(position);
                                buildBounds();
                                // emthod(builder, position);
                            } else {
                                Log.d(TAG, "onComplete: current location is null");
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
    public void publishMarkers(){
        Set<Integer> keys = table.keySet();
        for(Integer key: keys){
            MarkerInfo mi = table.get(key);
            img_markerbadge.setImageBitmap(mi.getImg());
            Badge b = mi.getBadge();
            MarkerOptions mo;
            mo = new MarkerOptions().position(b
                    .getLatLng())
                    .title("" + b.getID())
                    .icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view_marker)));
            markers.put(b.getID(), mMap.addMarker(mo));

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

        // Previous code


        /*public void setUpMarkerGenerationThreads(int numWorkers, LinkedList<Badge> badges) {
            this.badges = badges;
            cyclicBarrier = new CyclicBarrier(numWorkers, new AggregatorThread());

            for(Badge b: badges){
                Hunt h = mHuntManager.getHuntByID(b.getHuntID());
                Thread worker = new Thread(new WorkerThread(b, h));
                System.out.println("markers: starting a worker thread");
                worker.start();

            }

        }*/
        private CustomTarget customTarget;
        private CustomTargetFailed customTargetFailed;

        /*public void setUpMarkerGenerationThreads(LinkedList<Badge> badges) {

            for(Badge b: badges){
                Hunt h = mHuntManager.getHuntByID(b.getHuntID());
                ImageManager im = new ImageManager();
                MarkerOptions mo = new MarkerOptions().position(b
                        .getLatLng())
                        .title("" + b.getID());
                customTarget = new CustomTarget(b,mo);
                im.fillSuperBadgeImageIntoMarker(getContext(), h, customTarget);

            }

        }*/

    public void setUpMarkerGenerationThreads(Hunt h) {
        LinkedList<Badge> badges = h.getAllBadges();
        Bitmap bitmap = superBadgeImages.get(h.getID());
        if(bitmap == null){
            ImageManager im = new ImageManager();
            customTargetFailed = new CustomTargetFailed(h);
            im.fillSuperBadgeImageIntoMarker(getContext(), h, customTargetFailed);
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
        class CustomTarget implements Target{
            private Badge badge;
            private MarkerOptions mo;
            private Hunt h;
            /*CustomTarget(Badge b, MarkerOptions mo){
                badge = b;
                this.mo = mo;
            }*/
            CustomTarget(Hunt h){
                System.out.println("Hunt added to constructor: "+h.getID());
                this.h = h;
            }
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                /*img_markerbadge.setImageBitmap(bitmap);
                mo.icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(view_marker)));
                markers.put(badge.getID(), mMap.addMarker(mo));*/
                System.out.println("Hunt added to: "+h.getID());
                superBadgeImages.put(h.getID(),bitmap);

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
            superBadgeImages.put(h.getID(),bitmap);
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

    public void setHuntCompleteNotificationMaps(Hunt h){
        //mHuntManager.setFocusAward(h.getID());
        dialogHuntCompleted.setHunt(h);
        dialogHuntCompleted.setCancelable(false);
        dialogHuntCompleted.show();
    }
}
