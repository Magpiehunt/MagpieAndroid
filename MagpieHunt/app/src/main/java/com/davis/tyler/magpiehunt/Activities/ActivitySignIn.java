package com.davis.tyler.magpiehunt.Activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.davis.tyler.magpiehunt.Fragments.FragmentSigninInitial;
import com.davis.tyler.magpiehunt.Fragments.FragmentWalkthroughContainer;
import com.davis.tyler.magpiehunt.R;

public class ActivitySignIn extends AppCompatActivity implements FragmentSigninInitial.FragmentSigninInitialListener{
    private FragmentSigninInitial mFragmentSigninInitial;
    private static final String TAG = "Actvity_signin";

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private String[] permissionResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //getLocationPermission();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        permissionResults = new String[]{"camera", "internet", "network", "storage", "fine", "coarse"};
        if(preferences.getBoolean("firstrun", true)){
            System.out.println("permission this is first time run");
            preferences.edit().putBoolean("firstrun", false).apply();
            checkQRPermission();
        }else {
            System.out.println("permission this is not first time run");
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.action_bar_gradient, null));
        mFragmentSigninInitial = new FragmentSigninInitial();
        goToLogin();
    }

    public void goToLogin(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.currentfragment, mFragmentSigninInitial);

        ft.commit();
    }

    @Override
    public void swipedLeftEvent() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.currentfragment, new FragmentWalkthroughContainer());
        ft.addToBackStack(null);
        ft.commit();
    }

    public boolean checkCameraPermission(){
        SharedPreferences preferences =
                getSharedPreferences("settings",
                        android.content.Context.MODE_PRIVATE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            preferences.edit().putBoolean("camera", true).apply();
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, PackageManager.PERMISSION_GRANTED);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                preferences.edit().putBoolean("camera", true).apply();
                return true;
            }
            else{
                preferences.edit().putBoolean("camera", false).apply();
                return false;
            }
        }
    }

    public boolean checkExternalStoragePermission(){
        SharedPreferences preferences =
                getSharedPreferences("settings",
                        android.content.Context.MODE_PRIVATE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            preferences.edit().putBoolean("storage", true).apply();
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PackageManager.PERMISSION_GRANTED);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                preferences.edit().putBoolean("storage", true).apply();
                return true;
            }
            else{
                preferences.edit().putBoolean("storage", false).apply();
                return false;
            }
        }
    }

    public boolean checkLocationPermission(){
        SharedPreferences preferences =
                getSharedPreferences("settings",
                        android.content.Context.MODE_PRIVATE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            preferences.edit().putBoolean("internet", true).apply();
            preferences.edit().putBoolean("network", true).apply();
            preferences.edit().putBoolean("fine", true).apply();
            preferences.edit().putBoolean("coarse", true).apply();
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, PackageManager.PERMISSION_GRANTED);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){

                return true;
            }
            else{
                return false;
            }
        }
    }


    public boolean checkQRPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true;
            }
            else{
                return false;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //SharedPreferences.Editor editor = preferences.edit();

                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            System.out.println("permission "+permissionResults[i]+" denied");
                           preferences.edit().putBoolean(permissionResults[i], false).apply();
                        }
                        else{
                            System.out.println("permission "+permissionResults[i]+" granted");
                            preferences.edit().putBoolean(permissionResults[i], true).apply();
                            System.out.println("permission "+preferences.getBoolean(permissionResults[i],false)+" current");
                        }
                    }

                }
            }
        }
    }
}
