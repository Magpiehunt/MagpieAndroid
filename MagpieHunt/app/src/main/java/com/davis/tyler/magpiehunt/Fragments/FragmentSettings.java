package com.davis.tyler.magpiehunt.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Activities.ActivitySignIn;
import com.davis.tyler.magpiehunt.Adapters.CollectionAdapter;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.RecyclerItemHelpers.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

public class FragmentSettings extends Fragment implements View.OnClickListener{
    private static final String TAG = "FragmentSettings";
    private final int CAMERA_PERMISSION_CODE = 0;
    private final int LOCATION_PERMISSION_CODE = 1;
    private final int FILE_PERMISSION_CODE = 2;
    private Button btn_signout;
    private RelativeLayout btn_privacy_policy;
    private RelativeLayout btn_terms_and_conditions;
    private CheckBox cb_camera, cb_location, cb_push_notifications;
    private SharedPreferences preferences;

    public static FragmentSettings newInstance() {
        FragmentSettings fragment = new FragmentSettings();
        Bundle args = new Bundle();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        btn_privacy_policy = rootView.findViewById(R.id.privacy_policy);
        btn_terms_and_conditions = rootView.findViewById(R.id.terms_and_conditions);
        btn_signout = rootView.findViewById(R.id.btn_signout);
        cb_camera = rootView.findViewById(R.id.cb_camera);
        cb_location = rootView.findViewById(R.id.cb_gps);
        initialize_cb_state();

        cb_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setLocationPermissionOnElseOff(isChecked);
                ((ActivityBase)getActivity()).updatePermissionLocation(isChecked);
            }
        });
        cb_camera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setCameraPermissionOnElseOff(isChecked);
                //((ActivityBase)getActivity()).updatePermissionCamera(isChecked);
            }
        });

        btn_privacy_policy.setOnClickListener(this);
        btn_terms_and_conditions.setOnClickListener(this);
        btn_signout.setOnClickListener(this);

        //TODO set check boxes based on whats in shared preferences

        return rootView;

    }

    private void initialize_cb_state(){
        if(preferences.getBoolean("fine", false) && preferences.getBoolean("coarse", false))
            cb_location.setChecked(true);
        if(preferences.getBoolean("camera", false))
            cb_camera.setChecked(true);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //TODO: restore state of fragment
        }//end if
    }//end

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.e(TAG, "onsave");

        super.onSaveInstanceState(savedInstanceState);

        //TODO: saved state of fragment here
    }//end


    @Override
    public void onClick(View v) {
        FragmentHome fh = null;
        switch (v.getId()) {
            case R.id.btn_signout:
                startActivity(new Intent(getActivity().getApplicationContext(), ActivitySignIn.class));
                getActivity().finish();
                //TODO do signout function here
                break;
            case R.id.terms_and_conditions:
                fh = (FragmentHome)getParentFragment();
                fh.setFragment(3);
                break;
            case R.id.privacy_policy:
                fh = (FragmentHome)getParentFragment();
                fh.setFragment(2);
                break;


            default:
                break;
        }//end switch
    }//end onClick

    private void setCameraPermissionOnElseOff(boolean b){
        if(!b){
            preferences.edit().putBoolean("camera", false).apply();
        }
        else{

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                preferences.edit().putBoolean("camera", true).apply();
            }else {
                requestPermissions(new String[]{Manifest.permission.CAMERA
                }, CAMERA_PERMISSION_CODE);
            }
        }
    }
    private void setFilePermissionOnElseOff(boolean b){
        if(!b){
            preferences.edit().putBoolean("storage", false).apply();
        }
        else{
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, FILE_PERMISSION_CODE);
        }
    }
    private void setLocationPermissionOnElseOff(boolean b){
        System.out.println( "permission toggle location: "+b );
        if(!b){
            preferences.edit().putBoolean("fine", false).apply();
            preferences.edit().putBoolean("coarse", false).apply();
        }
        else{

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                preferences.edit().putBoolean("fine", true).apply();
                preferences.edit().putBoolean("coarse", true).apply();
                if(!isLocationEnabled(getContext())){
                    Toast.makeText(getContext(), "Make sure location is turned on in phone settings as well", Toast.LENGTH_LONG).show();
                }

            }else {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_PERMISSION_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("permission retoggle off camera");
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {
                System.out.println("permission retoggle off camera");
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            preferences.edit().putBoolean("camera", false).apply();
                            cb_camera.setChecked(false);
                            System.out.println("permission retoggle off");
                        }
                        else{
                            preferences.edit().putBoolean("camera", true).apply();
                        }
                    }

                }
            }
            break;
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    preferences.edit().putBoolean("fine", false).commit();
                    preferences.edit().putBoolean("coarse", false).commit();
                    cb_location.setChecked(false);
                }else {
                    System.out.println("permission toggled to location: " + true);
                    preferences.edit().putBoolean("fine", true).commit();
                    preferences.edit().putBoolean("coarse", true).commit();
                    if(!isLocationEnabled(getContext())){
                        Toast.makeText(getContext(), "Make sure location is turned on in phone settings as well", Toast.LENGTH_LONG).show();
                    }
                    ((ActivityBase)getActivity()).updatePermissionLocation(true);
                }

                break;

            }
            case FILE_PERMISSION_CODE: {
                if (grantResults.length > 0) {
                    boolean permission_granted = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permission_granted = false;
                        }
                    }

                    preferences.edit().putBoolean("storage", permission_granted).apply();
                    //todo toggle checkbox now

                }
            }
            break;

        }
        System.out.println("permission retoggle off camera");
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


}
