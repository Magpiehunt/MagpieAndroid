package com.davis.tyler.magpiehunt.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Dialogs.DialogAddHunt;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.Location.CameraManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Spinners.SpinnerOverallHuntsFilter;

public class FragmentOverallHunt extends Fragment implements SpinnerOverallHuntsFilter.OnSpinnerEventsListener, FragmentLandmarkInfo.onClickListener, FragmentLandmarkList.OnLandmarkSelectedListener {
    private static final String TAG = "Overall Fragment";
    public static final int FRAGMENT_OVERALL_HUNT_TABS = 0;
    public static final int FRAGMENT_LANDMARK_INFO = 1;
    public static final int FRAGMENT_QR_READER= 2;
    public static final int FRAGMENT_QUIZ = 3;
    public static final int FRAGMENT_TIMER = 4;
    public static final int FRAGMENT_BADGE_OBTAINED = 5;

    public static final int FILTER_NEARME = 0;
    public static final int FILTER_DOWNLOADED = 1;
    public static final int FILTER_SEARCHED = 2;
    private HuntManager mHuntManager;
    private FragmentOverallHuntTabs fragmentOverallHuntTabs;
    private FragmentLandmarkInfo mFragmentLandmarkInfo;
    private FragmentQuiz fragmentQuiz;
    private FragmentTimer fragmentTimer;
    private FragmentBadgeObtained fragmentBadgeObtained;
    private int curFrag;
    private CameraManager cameraManager;
    private int curTab;
    private boolean isVisible;
    private int curFilter;
    private boolean searchHuntsElseLandmarks;
    private boolean modeHuntsElseLandmarks;
    private DialogAddHunt dialogAddHunt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overall_hunt, container, false);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        Log.e(TAG, "ONCREATE");
        cameraManager = new CameraManager();
        curFrag = 0;
        curFilter = FILTER_DOWNLOADED;
        modeHuntsElseLandmarks = true;
        searchHuntsElseLandmarks = true;
        setFragment(FRAGMENT_OVERALL_HUNT_TABS);
        dialogAddHunt = new DialogAddHunt(getActivity());
        dialogAddHunt.setCancelable(false);

        return view;
    }

    public static FragmentOverallHunt newInstance() {
        FragmentOverallHunt f = new FragmentOverallHunt();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
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
        if (!isAdded()) return;
        curFrag = i;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(i == FRAGMENT_OVERALL_HUNT_TABS) {
            ((ActivityBase)getActivity()).setBackButtonOnOff(false);
            if(fragmentOverallHuntTabs == null) {
                fragmentOverallHuntTabs = FragmentOverallHuntTabs.newInstance(cameraManager);
            }

            ft.replace(R.id.currentfragment, fragmentOverallHuntTabs);
        }
        else if(i == FRAGMENT_LANDMARK_INFO){
            if(mFragmentLandmarkInfo == null){
                mFragmentLandmarkInfo = FragmentLandmarkInfo.newInstance();
            }
            ft.replace(R.id.currentfragment, mFragmentLandmarkInfo);
        }
        else if(i == FRAGMENT_QR_READER) {
            ft.replace(R.id.currentfragment, FragmentQR.newInstance());
        }
        else if(i == FRAGMENT_TIMER){
            if(fragmentTimer == null){
                fragmentTimer = FragmentTimer.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentTimer);

        }
        else if(i == FRAGMENT_QUIZ){
            if(fragmentQuiz == null){
                fragmentQuiz = FragmentQuiz.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentQuiz);

        }
        else if(i == FRAGMENT_BADGE_OBTAINED){
            if(fragmentBadgeObtained == null){
                fragmentBadgeObtained = FragmentBadgeObtained.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentBadgeObtained);

        }
        ft.commit();
        try {
            if(i != FRAGMENT_LANDMARK_INFO) {

                getChildFragmentManager().executePendingTransactions();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        if(i == FRAGMENT_TIMER) {
            fragmentTimer.startTimer();
        }
        else if(i == FRAGMENT_BADGE_OBTAINED){
            fragmentBadgeObtained.updateInfo(mHuntManager.getFocusBadge());
        }
        else if(i == FRAGMENT_QUIZ){
            fragmentQuiz.updateFragment();
        }
        else if(i == FRAGMENT_OVERALL_HUNT_TABS){
            fragmentOverallHuntTabs.updatetab();

            //fragmentBirdsEyeViewContainer.reloadMap();
        }
    }
    public void updateActionBar(){
        if(isVisible) {
            ActivityBase activityBase = ((ActivityBase) getActivity());
            if (mHuntManager.getSelectedHuntsSize() == 1 && !modeHuntsElseLandmarks) {
                activityBase.getSupportActionBar().setTitle(mHuntManager.getSingleSelectedHunt().getName());
            } else {
                if(curTab == FragmentOverallHuntTabs.FRAGMENT_GOOGLE_MAPS) {
                    if(modeHuntsElseLandmarks) {
                        activityBase.getSupportActionBar().setTitle("Map");
                    }else{
                        ((ActivityBase)getActivity()).setHuntTitle();
                    }

                }
                else if(curTab == FragmentOverallHuntTabs.FRAGMENT_LIST)
                    activityBase.getSupportActionBar().setTitle("Badges");
            }
            activityBase.menuSettingsVisibility(false);
            activityBase.setBackButtonOnOff(false);
            if (curFrag == FRAGMENT_LANDMARK_INFO)
                activityBase.setBackButtonOnOff(true);
            else if (curFrag == FRAGMENT_QR_READER) {
                activityBase.setBackButtonOnOff(true);
            } else if (curFrag == FRAGMENT_QUIZ) {
                activityBase.setBackButtonOnOff(true);
            }
            if(curFrag == FRAGMENT_OVERALL_HUNT_TABS && curTab == FragmentOverallHuntTabs.FRAGMENT_HUNT_SEARCH){
                if(searchHuntsElseLandmarks) {
                    activityBase.getSupportActionBar().setTitle("Search Collections");
                }else{
                    activityBase.getSupportActionBar().setTitle("Search Badges");
                }
            }
        }
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

    public void updateFocusHunts(){
        if(fragmentOverallHuntTabs != null)
            fragmentOverallHuntTabs.updateFocusHunts();
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
    public void moveCameraToHuntSpinnerClose(){
        fragmentOverallHuntTabs.moveCameraToHuntSpinnerClose();
    }
    @Override
    public void onSpinnerSearchClosed() {
        fragmentOverallHuntTabs.updateFocusHunts();
    }

    @Override
    public void onLandmarkSelected(Badge b) {

        Hunt h = mHuntManager.getHuntByID(b.getHuntID());
        if(!h.getIsDownloaded() || h.getIsDeleted()){
            dialogAddHunt.setHunt(h);
            dialogAddHunt.show();
        }
        else {
            mHuntManager.setFocusBadge(b.getID());
            setFragment(FRAGMENT_LANDMARK_INFO);
        }
    }
    public void moveCameraToHunt(){
        if(fragmentOverallHuntTabs != null){
            fragmentOverallHuntTabs.moveCameraToHunt();
        }
    }

    @Override
    public void onMapClicked(Badge badge) {
        setFragment(FRAGMENT_OVERALL_HUNT_TABS);
        fragmentOverallHuntTabs.setCameraOnLandmark(badge);
    }
    public void onBackPressed(){
        if(curFrag == FRAGMENT_LANDMARK_INFO){
            ((ActivityBase)getActivity()).setBackButtonOnOff(false);
            setFragment(FRAGMENT_OVERALL_HUNT_TABS);
            //fragmentBirdsEyeViewContainer.reloadMap();
        }
        else if(curFrag == FRAGMENT_QR_READER){
            setFragment(FRAGMENT_LANDMARK_INFO);
        }
        else if(curFrag == FRAGMENT_QUIZ){
            setFragment(FRAGMENT_LANDMARK_INFO);
        }
    }
    @Override
    public void onCollectMoveCloser(Badge badge) {
        //if user isnt close enough, switch to map fragment, and set camera to target landmark
        cameraManager.setMoveCloser(true);
        //mViewPager.setCurrentItem(FRAGMENT_MAP);
        //setFragment(FRAGMENT_BIRDS_EYE);

        fragmentOverallHuntTabs.setTab(FragmentOverallHuntTabs.FRAGMENT_GOOGLE_MAPS);
        fragmentOverallHuntTabs.setCameraOnLandmark(badge);
        //mNavigationView.setSelectedItemId(FRAGMENT_MAP);

    }
    public void updateSpinner(){
        if(fragmentOverallHuntTabs != null){
            fragmentOverallHuntTabs.updateSpinner();
        }
    }
    public void notifyLocationChanged(){
        if(curFrag == FRAGMENT_OVERALL_HUNT_TABS)
            fragmentOverallHuntTabs.notifyLocationChanged();
    }
    public void setHuntCompleteNotification(Hunt h){
        if(curTab == FragmentOverallHuntTabs.FRAGMENT_GOOGLE_MAPS){
            fragmentOverallHuntTabs.setHuntCompleteNotificationMaps(h);
        }else if( curTab == FragmentOverallHuntTabs.FRAGMENT_LIST){
            fragmentOverallHuntTabs.setHuntCompleteNotificationList(h);
        }
    }

    public boolean getSearchHuntsElseLandmarks(){return searchHuntsElseLandmarks;}
    public void setSearchHuntsElseLandmarks(boolean b){
        searchHuntsElseLandmarks = b;
        updateActionBar();
    }
    public boolean getModeHuntsElseLandmarks(){return modeHuntsElseLandmarks;}
    public void setModeHuntsElseLandmarks(boolean b){
        modeHuntsElseLandmarks = b;
        updateActionBar();
        fragmentOverallHuntTabs.updateSpinnerVisibility();
        fragmentOverallHuntTabs.setCustomInfoWindow();

    }
    public void updateLocation(Location location){
        if(curFrag == FRAGMENT_OVERALL_HUNT_TABS && curTab == FragmentOverallHuntTabs.FRAGMENT_GOOGLE_MAPS)
            fragmentOverallHuntTabs.updateLocation(location);
    }
}
