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

import com.davis.tyler.magpiehunt.Fragments.FragmentPrivacyPolicy;
import com.davis.tyler.magpiehunt.Fragments.FragmentSigninInitial;
import com.davis.tyler.magpiehunt.Fragments.FragmentTermsAndConditions;
import com.davis.tyler.magpiehunt.Fragments.FragmentWalkthroughContainer;
import com.davis.tyler.magpiehunt.R;

public class ActivitySignIn extends AppCompatActivity implements FragmentSigninInitial.FragmentSigninInitialListener{
    private FragmentSigninInitial fragmentSigninInitial;
    private static final String TAG = "Actvity_signin";

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final int FRAGMENT_PRIVACY = 0;
    public static final int FRAGMENT_TERMS = 1;
    public static final int FRAGMENT_SIGNIN = 2;

    private FragmentPrivacyPolicy fragmentPrivacyPolicy;
    private FragmentTermsAndConditions fragmentTermsAndConditions;
    private int curFrag;
    private String[] permissionResults;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        permissionResults = new String[]{"camera", "internet", "network", "storage", "fine", "coarse"};
        if (preferences.getBoolean("firstrun", true)) {
            System.out.println("permission this is first time run");
            preferences.edit().putBoolean("firstrun", false).apply();
            checkQRPermission();
        } else {
            System.out.println("permission this is not first time run");
        }
        if (!preferences.getBoolean("privacyAccepted", false)) {
            setFragment(FRAGMENT_PRIVACY);
        } else if (!preferences.getBoolean("termsAccepted", false)) {
            setFragment(FRAGMENT_TERMS);
        } else {
            setFragment(FRAGMENT_SIGNIN);
        }
        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.action_bar_gradient, null));

    }

    public void setPrivacyAccept(){
        preferences.edit().putBoolean("privacyAccepted", true).apply();
    }
    public void setTermsAccept(){
        preferences.edit().putBoolean("termsAccepted", true).apply();
    }

    public void setFragment(int i){
        curFrag = i;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(i == FRAGMENT_PRIVACY) {
            if(fragmentPrivacyPolicy == null) {
                fragmentPrivacyPolicy = FragmentPrivacyPolicy.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentPrivacyPolicy);
        }else if( i == FRAGMENT_SIGNIN){
            if(fragmentSigninInitial == null)
                fragmentSigninInitial = new FragmentSigninInitial();
            ft.replace(R.id.currentfragment, fragmentSigninInitial);
        }
        else if(i == FRAGMENT_TERMS)
        {
            if(fragmentTermsAndConditions == null){
                fragmentTermsAndConditions = FragmentTermsAndConditions.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentTermsAndConditions);
        }

        ft.commit();


    }

    @Override
    public void swipedLeftEvent() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.currentfragment, new FragmentWalkthroughContainer());
        ft.addToBackStack(null);
        ft.commit();
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
