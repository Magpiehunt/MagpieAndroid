package com.davis.tyler.magpiehunt.Location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by evan g on 3/13/2018.
 * This class is pretty generic so that you can use it within any fragment/activity you want to check the location of the device.
 * For examples on how it could be implemented, see the GoogleMapsFragment
 */

/*
    Edited by Tyler Davis 8/16/2018
    Added functionality to update listviews to stay updated on distances when user location changes
    Changed small things to give distances in Miles and rounded up to 2 decimal places.
 */

//after initialization you can call hasLocPermission in order to check if permissions were accepted or not
//that way your fragment/activity can handle the results appropriatly
public class LocationTracker implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final String TAG = "LocationTracker";
    private Activity a;
    private Context context;
    //private GPSTracker gpsTracker;
    //private static double validDistanceInMiles = 0.008;//change this later to whatever the valid distance you decide on then
    private static double validDistanceInMiles = 20;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private HuntManager mHuntManager;
    private Location currLoc;
    private LatLng coords;
    private SharedPreferences preferences;

    public LocationTracker(Activity a, Context cxt, HuntManager hm){//use null for gMap if not using this feature
        this.a = a;
        context = cxt;
        //this.gMap = gMap;
        mHuntManager = hm;
        preferences = PreferenceManager.getDefaultSharedPreferences(cxt);
        if(preferences.getBoolean("fine", false) && preferences.getBoolean("coarse", false)){

            System.out.println("permission, location approved");
            checkLocationPermission();
        }
        //checkLocationPermission();
    }

    public boolean isValidDistance(Location to){
        if(distanceToPoint(to) <= validDistanceInMiles){
            return true;
        }
        return false;
    }

    public double distanceToPoint(Location loc){
        return currLoc.distanceTo(loc)/(1609.344);
    }

    /*
    Check if we have permission to use gps tracking, then when our location changes, order badges
    by distance, and then notify activity that location has changed so list views can update.
     */
    public boolean checkLocationPermission() {
        if(PermissionChecker.checkCallingOrSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                PermissionChecker.checkCallingOrSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            System.out.println("distance permission granted");
            mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    System.out.println("updating: onlocation changed");
                    if(hasLocPermission()) {
                        System.out.println("updating: has location permissions");
                        currLoc = location;
                        coords = new LatLng(location.getLatitude(), location.getLongitude());

                        updateDistances();
                        if(location.hasBearing()) {
                            ((ActivityBase) a).updateLocation(location);
                        }
                    }
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {
                   /* if(gMap != null && coords != null)
                    {
                        gMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 20));
                    }*/
                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            Criteria criteria = new Criteria();
            criteria.setBearingRequired(true);
            criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
            //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, mLocationListener);
            //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2, mLocationListener);
            if(hasLocPermission()) {
                currLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                coords = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());
                updateDistances();
            }
            return true;
        }
        else{
            ActivityCompat.requestPermissions(a, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //return hasPermission;
        }
        return hasLocPermission();
    }


    public boolean hasLocPermission(){
        preferences = PreferenceManager.getDefaultSharedPreferences(a.getApplicationContext());

        System.out.println("permission map: "+preferences.getBoolean("fine", false)+" "+ preferences.getBoolean("coarse", false));
        return (preferences.getBoolean("fine", false)
                && preferences.getBoolean("coarse", false)
                && isLocationEnabled(a.getApplicationContext()));
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

    public Location getCurrLoc(){
        return currLoc;
    }

    /*public void setgMap(GoogleMap map){
        gMap = map;
    }*/

    public void updateDistances(){
        System.out.println("updating: updating distances");
        if(hasLocPermission()) {
            Location l = new Location("");


            LinkedList<Badge> badges = mHuntManager.getAllBadges();
            for (Badge b : badges) {
                b.setLocation(l);
                Log.e(TAG, "updating: distance to: " + distanceToPoint(l));
                b.setDistance(distanceToPoint(l));
            }
            ((ActivityBase) a).notifyLocationChanged();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    preferences.edit().putBoolean("fine", true).apply();
                    preferences.edit().putBoolean("coarse", true).apply();
                }
                else{
                    System.out.println("distance permission denied from tracker");
                    Toast.makeText(context, "Location Permissions are required to use this functionality", Toast.LENGTH_SHORT).show();
                    preferences.edit().putBoolean("fine", false).apply();
                    preferences.edit().putBoolean("coarse", false).apply();
                }
                return;
        }
    }
    public void updatePermissionLocation(boolean onElseOff){
        if(onElseOff){
            checkLocationPermission();
        }
        else{
            if(mLocationManager != null)
                mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
