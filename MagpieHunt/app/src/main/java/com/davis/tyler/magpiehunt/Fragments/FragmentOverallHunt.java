package com.davis.tyler.magpiehunt.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.Location.CameraManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Spinners.SpinnerOverallHuntsFilter;

public class FragmentOverallHunt extends Fragment implements SpinnerOverallHuntsFilter.OnSpinnerEventsListener {
    private static final String TAG = "Search Fragment";
    public static final int FRAGMENT_OVERALL_HUNT_TABS = 0;

    public static final int FILTER_NEARME = 0;
    public static final int FILTER_DOWNLOADED = 1;
    public static final int FILTER_SEARCHED = 2;
    private HuntManager mHuntManager;
    private FragmentOverallHuntTabs fragmentOverallHuntTabs;
    private int curFrag;
    private CameraManager cameraManager;
    private int curTab;
    private boolean isVisible;
    private int curFilter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overall_hunt, container, false);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        Log.e(TAG, "ONCREATE");
        cameraManager = new CameraManager();
        curFrag = 0;
        curFilter = FILTER_DOWNLOADED;
        setFragment(FRAGMENT_OVERALL_HUNT_TABS);

        return view;
    }

    public static FragmentOverallHunt newInstance(HuntManager huntManager) {
        FragmentOverallHunt f = new FragmentOverallHunt();
        Bundle args = new Bundle();
        //args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");
    }

    public void updateActionBar(){
        if(isVisible) {
            ActivityBase activityBase = ((ActivityBase) getActivity());

            if (curTab == FragmentOverallHuntTabs.FRAGMENT_GOOGLE_MAPS) {
                activityBase.getSupportActionBar().setTitle("Map View");
            } else if (curTab == FragmentOverallHuntTabs.FRAGMENT_HUNT_SEARCH) {
                activityBase.getSupportActionBar().setTitle("Find Collections");
            } else if (curTab == FragmentOverallHuntTabs.FRAGMENT_HUNT_LIST) {
                activityBase.getSupportActionBar().setTitle("My Collections");
            }
            activityBase.menuSettingsVisibility(false);
            activityBase.setBackButtonOnOff(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            Log.e(TAG,"visible");
            isVisible = true;
        }
        else{
            Log.e(TAG,"not visible");
            isVisible = false;
        }
    }

    public void updatePermissionLocation(boolean b){
        if(fragmentOverallHuntTabs!= null)
            fragmentOverallHuntTabs.updatePermissionLocation(b);
    }
    public void setFragment(int i) {
        curFrag = i;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(i == FRAGMENT_OVERALL_HUNT_TABS) {
            if(fragmentOverallHuntTabs == null) {
                fragmentOverallHuntTabs = FragmentOverallHuntTabs.newInstance(mHuntManager, cameraManager);
            }
            ft.replace(R.id.currentfragment, fragmentOverallHuntTabs);
        }
        ft.commit();
        try {
                getChildFragmentManager().executePendingTransactions();
        }catch(Exception e){
            e.printStackTrace();
        }
            fragmentOverallHuntTabs.updatetab();

    }
    public void hideSoftKeyboard(){
        //if(fragmentOverallHuntTabs != null)
            //fragmentOverallHuntTabs.hideSoftKeyboard();
    }

    public int getCurTab(){return curTab;}
    public void setCurTab(int i){
        curTab = i;
        updateActionBar();
    }
    public void changeTab(int i){
        fragmentOverallHuntTabs.setTab(i);
    }
    public void updateHuntsList(){
        if(fragmentOverallHuntTabs != null)
            fragmentOverallHuntTabs.updateHuntsList();
    }

    public void setFilter(int i ){
        curFilter = i;
    }
    public int getCurFilter(){return curFilter;}
    public void filterUpdate(int i){
        curFilter = i;
    }

    @Override
    public void onSpinnerSearchOpened() {

    }

    @Override
    public void onSpinnerSearchClosed() {
        fragmentOverallHuntTabs.updateFocusHunts();
    }
}
