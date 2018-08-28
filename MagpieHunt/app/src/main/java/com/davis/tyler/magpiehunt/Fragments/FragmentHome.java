package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.LocationTracker;
import com.davis.tyler.magpiehunt.QRFragment;
import com.davis.tyler.magpiehunt.R;

public class FragmentHome extends Fragment {
    private static final String TAG = "Home Fragment";
    public static final int FRAGMENT_LANDMARK_LIST = 0;
    public static final int FRAGMENT_LANDMARK_INFO = 1;
    public static final int FRAGMENT_QR_READER= 2;
    private FragmentLandmarkList mLandmarkListFragment;
    private FragmentLandmarkInfo mFragmentLandmarkInfo;
    private HuntManager mHuntManager;
    private int curFrag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.e(TAG, "onCreateView");
        curFrag = 0;
        setFragment(FRAGMENT_LANDMARK_LIST);

        return view;
    }

    public void notifyLocationChanged(){
        mLandmarkListFragment.updateList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            Log.e(TAG,"visible");
        }
        else{
            Log.e(TAG,"not visible");
        }
    }

    public static FragmentHome newInstance(HuntManager huntManager) {
        FragmentHome f = new FragmentHome();
        Bundle args = new Bundle();
        args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        mHuntManager = (HuntManager)args.getSerializable("huntmanager");
    }

    public void onBackPressed(){
        //todo implement backpress in map fragment
        Log.e(TAG, "curfrag: "+curFrag);
        if(curFrag == FRAGMENT_LANDMARK_INFO){
            setFragment(FRAGMENT_LANDMARK_LIST);
        }
        else if(curFrag == FRAGMENT_QR_READER){
            setFragment(FRAGMENT_LANDMARK_INFO);
        }
    }

    public void setFragment(int i) {
        curFrag = i;
        Log.e(TAG, "curfrag set to: "+curFrag);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(i == FRAGMENT_LANDMARK_LIST) {
            Log.e(TAG, "Switching to list");
            if(mLandmarkListFragment == null) {
                //collectionFragment = FragmentGoogleMaps.newInstance(mHuntManager);
                mLandmarkListFragment = FragmentLandmarkList.newInstance(mHuntManager);
            }
            mLandmarkListFragment.onUpdateTextView();
            ft.replace(R.id.currentfragment, mLandmarkListFragment);
        }
        else if(i == FRAGMENT_LANDMARK_INFO){
            Log.e(TAG, "Switching to info");
            if(mFragmentLandmarkInfo == null){
                mFragmentLandmarkInfo = FragmentLandmarkInfo.newInstance(mHuntManager);
            }
            ft.replace(R.id.currentfragment, mFragmentLandmarkInfo);
        }
        else if(i == FRAGMENT_QR_READER)
            ft.replace(R.id.currentfragment, QRFragment.newInstance(mHuntManager));
        ft.commit();
    }

    public void updateFocusHunts(){
        if(mLandmarkListFragment != null)
            mLandmarkListFragment.updateFocusHunts();
    }
}
