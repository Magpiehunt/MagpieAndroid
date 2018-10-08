package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Location.CameraManager;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;

public class FragmentMap extends Fragment implements IFragmentSwitcherListener{
    private static final String TAG = "Map Fragment";
    public static final int FRAGMENT_GOOGLE_MAPS = 0;
    public static final int FRAGMENT_LANDMARK_INFO = 1;
    public static final int FRAGMENT_QR_READER = 2;
    public static final int FRAGMENT_QUIZ = 3;
    public static final int FRAGMENT_TIMER = 4;
    public static final int FRAGMENT_BADGE_OBTAINED = 5;
    private FragmentGoogleMaps fragmentGoogleMaps;
    private FragmentLandmarkInfo fragmentLandmarkInfo;
    private FragmentTimer fragmentTimer;
    private FragmentQuiz fragmentQuiz;
    private FragmentBadgeObtained fragmentBadgeObtained;
    private HuntManager mHuntManager;
    private CameraManager mCameraManager;
    private int curFrag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        Log.e(TAG, "onCreateView");
        mHuntManager = ((ActivityBase)getActivity()).getData();
        if(savedInstanceState == null) {
            //Log.e(TAG, "MAKING NEW HUNT MANAGER: "+mHuntManager);
            curFrag = 0;
        }
        else{
            Log.e(TAG,"BUNDLE NOT NULL");
            curFrag = savedInstanceState.getInt("currentfragment");
        }
        Log.e(TAG,"oncreate calling setfragment: "+mHuntManager);
        setFragment(FRAGMENT_GOOGLE_MAPS);

        return view;
    }

    public static FragmentMap newInstance(HuntManager huntManager, CameraManager cameraManager) {
        FragmentMap f = new FragmentMap();
        Bundle args = new Bundle();
        //args.putSerializable("huntmanager", huntManager);
        args.putSerializable("cameramanager", cameraManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        Log.e(TAG, "setArguments, args: "+args);
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");
        mCameraManager = (CameraManager)args.getSerializable("cameramanager");
        Log.e(TAG, "setArguments, huntman: "+mHuntManager);


    }
    public void updateActionBar(){
        ActivityBase activityBase = ((ActivityBase) getActivity());
        if(mHuntManager.getSelectedHuntsSize()== 1){
            activityBase.getSupportActionBar().setTitle(mHuntManager.getSingleSelectedHunt().getName());
        }
        else {
            activityBase.getSupportActionBar().setTitle("Badges Near Me");
        }
        activityBase.menuSettingsVisibility(false);
        activityBase.setBackButtonOnOff(false);
        if(curFrag == FRAGMENT_BADGE_OBTAINED){
            activityBase.setBackButtonOnOff(false);
        }

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            Log.e(TAG,"visible");
            if(fragmentGoogleMaps != null)
            fragmentGoogleMaps.repositionCamera();
        }
        else{
            Log.e(TAG,"not visible");
            if(fragmentGoogleMaps != null)
                fragmentGoogleMaps.saveCameraPosition();
        }
    }


    @Override
    public void setFragment(int i) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        curFrag = i;
        Log.e(TAG, "curfrag set to: "+curFrag);
        if(i == FRAGMENT_GOOGLE_MAPS) {
            if(fragmentGoogleMaps == null) {
                fragmentGoogleMaps = FragmentGoogleMaps.newInstance(mHuntManager, mCameraManager);
            }
            ft.replace(R.id.currentfragment, fragmentGoogleMaps);
        }
        else if(i == FRAGMENT_LANDMARK_INFO){

            if(fragmentLandmarkInfo == null){
                fragmentLandmarkInfo = FragmentLandmarkInfo.newInstance(mHuntManager);
            }
            ft.replace(R.id.currentfragment, fragmentLandmarkInfo);
        }
        else if(i == FRAGMENT_QR_READER) {
            ft.replace(R.id.currentfragment, FragmentQR.newInstance(mHuntManager));
        }
        else if(i == FRAGMENT_TIMER){
            if(fragmentTimer == null){
                fragmentTimer = FragmentTimer.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentTimer);
            //fragmentTimer.startTimer();
        }
        else if(i == FRAGMENT_QUIZ){
            if(fragmentQuiz == null){
                fragmentQuiz = FragmentQuiz.newInstance(mHuntManager);
            }
            ft.replace(R.id.currentfragment, fragmentQuiz);

        }
        else if(i == FRAGMENT_BADGE_OBTAINED){
            if(fragmentBadgeObtained == null){
                fragmentBadgeObtained = FragmentBadgeObtained.newInstance(mHuntManager);
            }
            ft.replace(R.id.currentfragment, fragmentBadgeObtained);

        }
        ft.commit();

        if(i == FRAGMENT_TIMER) {
            getChildFragmentManager().executePendingTransactions();
            fragmentTimer.startTimer();
        }
        else if(i == FRAGMENT_GOOGLE_MAPS) {
            getChildFragmentManager().executePendingTransactions();
            fragmentGoogleMaps.updateFocusHunts();
        }
    }

    public void onBackPressed(){
        Log.e(TAG, "curfrag: "+curFrag);
        if(curFrag == FRAGMENT_LANDMARK_INFO){
            ((ActivityBase)getActivity()).setBackButtonOnOff(false);
            setFragment(FRAGMENT_GOOGLE_MAPS);
            curFrag = FRAGMENT_GOOGLE_MAPS;
            fragmentGoogleMaps.repositionCamera();
        }
        else if(curFrag == FRAGMENT_QR_READER){
            setFragment(FRAGMENT_LANDMARK_INFO);
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e(TAG, "onsave");

        super.onSaveInstanceState(outState);
        //outState.putSerializable("huntmanager", mHuntManager);
        outState.putInt("currentfragment", curFrag);
    }


    public void setCameraOnLandmark(Badge badge){
        fragmentGoogleMaps.setCameraOnLandmark(badge);
        setFragment(FRAGMENT_GOOGLE_MAPS);
    }
    public void updateFocusHunts(){
        if(fragmentGoogleMaps != null){
            fragmentGoogleMaps.updateFocusHunts();
        }
    }
    public void updateSpinner(){
        if(fragmentGoogleMaps != null){
            fragmentGoogleMaps.updateSpinner();
        }
    }
    public void updatePermissionLocation(boolean b){
        fragmentGoogleMaps.updatePermissionLocation(b);
    }
}