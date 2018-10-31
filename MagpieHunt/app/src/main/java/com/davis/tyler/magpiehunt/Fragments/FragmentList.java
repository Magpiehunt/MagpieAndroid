package com.davis.tyler.magpiehunt.Fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

public class FragmentList extends Fragment implements FragmentLandmarkInfo.onClickListener, FragmentLandmarkList.OnLandmarkSelectedListener{
    private static final String TAG = "LIST Fragment";
    public static final int FRAGMENT_GOOGLE_MAPS = 0;
    public static final int FRAGMENT_LANDMARK_LIST = 1;
    public static final int FRAGMENT_LANDMARK_INFO = 2;
    public static final int FRAGMENT_QR_READER= 3;
    public static final int FRAGMENT_QUIZ = 4;
    public static final int FRAGMENT_TIMER = 5;
    public static final int FRAGMENT_BADGE_OBTAINED = 6;
    public static final int FRAGMENT_BIRDS_EYE = 7;

    private FragmentLandmarkInfo mFragmentLandmarkInfo;
    private FragmentQuiz fragmentQuiz;
    private FragmentTimer fragmentTimer;
    private FragmentBadgeObtained fragmentBadgeObtained;
    private FragmentBirdsEyeViewContainer fragmentBirdsEyeViewContainer;
    private HuntManager mHuntManager;
    private CameraManager mCameraManager;
    private int curFrag;
    private int curTab;
    private boolean isVisible;
    private DialogAddHunt dialogAddHunt;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        curTab = 0;
        mHuntManager = ((ActivityBase)getActivity()).getData();

        Log.e(TAG, "onCreateView");
        setFragment(FRAGMENT_BIRDS_EYE);

        dialogAddHunt = new DialogAddHunt(getActivity());
        dialogAddHunt.setCancelable(false);

        return view;
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


    public void notifyLocationChanged(){
        if(curFrag == FRAGMENT_BIRDS_EYE)
            fragmentBirdsEyeViewContainer.notifyLocationChanged();
    }

    public static FragmentList newInstance(HuntManager huntManager, CameraManager cameraManager) {
        FragmentList f = new FragmentList();
        Bundle args = new Bundle();
        args.putSerializable("cameramanager", cameraManager);
        //args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        mCameraManager = (CameraManager)args.getSerializable("cameramanager");
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");
    }

    public void onBackPressed(){
        //todo implement backpress in map fragment
        if(curFrag == FRAGMENT_LANDMARK_INFO){
            ((ActivityBase)getActivity()).setBackButtonOnOff(false);
            setFragment(FRAGMENT_BIRDS_EYE);
            //fragmentBirdsEyeViewContainer.reloadMap();
        }
        else if(curFrag == FRAGMENT_QR_READER){
            setFragment(FRAGMENT_LANDMARK_INFO);
        }
        else if(curFrag == FRAGMENT_QUIZ){
            setFragment(FRAGMENT_LANDMARK_INFO);
        }
    }

    public void updateActionBar(){
        if(isVisible) {
            ActivityBase activityBase = ((ActivityBase) getActivity());
            if (mHuntManager.getSelectedHuntsSize() == 1) {
                activityBase.getSupportActionBar().setTitle(mHuntManager.getSingleSelectedHunt().getName());
            } else {
                if(curTab == FragmentBirdsEyeViewContainer.FRAGMENT_GOOGLE_MAPS)
                    activityBase.getSupportActionBar().setTitle("Birds Eye View");
                else if(curTab == FragmentBirdsEyeViewContainer.FRAGMENT_LANDMARK_LIST)
                    activityBase.getSupportActionBar().setTitle("Birds Eye View");
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
            if(curFrag == FRAGMENT_BIRDS_EYE && curTab == FragmentBirdsEyeViewContainer.FRAGMENT_LANDMARK_SEARCH){
                activityBase.getSupportActionBar().setTitle("Search Badges");
            }
        }
    }

    public void setFragment(int i) {
        System.out.println("CHANGING FRAGMENT");
        curFrag = i;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(i == FRAGMENT_BIRDS_EYE) {
            ((ActivityBase)getActivity()).setBackButtonOnOff(false);
            Log.e(TAG, "landmark list being switched to");
            if(fragmentBirdsEyeViewContainer == null) {
                fragmentBirdsEyeViewContainer = FragmentBirdsEyeViewContainer.newInstance(mHuntManager, mCameraManager);
            }

            ft.replace(R.id.currentfragment, fragmentBirdsEyeViewContainer);
        }
        else if(i == FRAGMENT_LANDMARK_INFO){
            if(mFragmentLandmarkInfo == null){
                mFragmentLandmarkInfo = FragmentLandmarkInfo.newInstance(mHuntManager);
            }
            ft.replace(R.id.currentfragment, mFragmentLandmarkInfo);
        }
        else if(i == FRAGMENT_QR_READER) {
            ft.replace(R.id.currentfragment, FragmentQR.newInstance(mHuntManager));
        }
        else if(i == FRAGMENT_TIMER){
            if(fragmentTimer == null){
                fragmentTimer = FragmentTimer.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentTimer);

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
        System.out.println("COMMITING");
        ft.commit();
        System.out.println("COMMITING");
        try {
            if(i != FRAGMENT_LANDMARK_INFO)
                getChildFragmentManager().executePendingTransactions();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("FINISH COMMITING");
        if(i == FRAGMENT_TIMER) {
            fragmentTimer.startTimer();
        }
        else if(i == FRAGMENT_BADGE_OBTAINED){
            fragmentBadgeObtained.updateInfo(mHuntManager.getFocusBadge());
        }
        else if(i == FRAGMENT_QUIZ){
            fragmentQuiz.updateFragment();
        }
        else if(i == FRAGMENT_BIRDS_EYE){
            fragmentBirdsEyeViewContainer.updatetab();

            //fragmentBirdsEyeViewContainer.reloadMap();
        }
    }

    public void moveCameraToHunt(){
        if(fragmentBirdsEyeViewContainer != null){
            fragmentBirdsEyeViewContainer.moveCameraToHunt();
        }
    }
    public void updateFocusHunts(){
        if(fragmentBirdsEyeViewContainer != null){
            fragmentBirdsEyeViewContainer.updateFocusHunts();
        }
    }
    public void updateSpinner(){
        if(fragmentBirdsEyeViewContainer != null){
            fragmentBirdsEyeViewContainer.updateSpinner();
        }
    }

    public void updatePermissionLocation(boolean b){
        if(fragmentBirdsEyeViewContainer!= null)
            fragmentBirdsEyeViewContainer.updatePermissionLocation(b);
    }

    public void setHuntCompleteNotification(Hunt h){
        if(curTab == FragmentBirdsEyeViewContainer.FRAGMENT_GOOGLE_MAPS){
            fragmentBirdsEyeViewContainer.setHuntCompleteNotificationMaps(h);
        }else if( curTab == FragmentBirdsEyeViewContainer.FRAGMENT_LANDMARK_LIST){
            fragmentBirdsEyeViewContainer.setHuntCompleteNotificationList(h);
        }
    }
    @Override
    public void onMapClicked(Badge badge) {
        setFragment(FRAGMENT_BIRDS_EYE);
        fragmentBirdsEyeViewContainer.setCameraOnLandmark(badge);
    }

    @Override
    public void onCollectMoveCloser(Badge badge) {
        //if user isnt close enough, switch to map fragment, and set camera to target landmark
        mCameraManager.setMoveCloser(true);
        //mViewPager.setCurrentItem(FRAGMENT_MAP);
        setFragment(FRAGMENT_BIRDS_EYE);

        fragmentBirdsEyeViewContainer.setTab(FragmentBirdsEyeViewContainer.FRAGMENT_GOOGLE_MAPS);
        fragmentBirdsEyeViewContainer.setCameraOnLandmark(badge);
        //mNavigationView.setSelectedItemId(FRAGMENT_MAP);

    }
    public void moveCameraToHuntSpinnerClose(){
        fragmentBirdsEyeViewContainer.moveCameraToHuntSpinnerClose();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    public void setCurTab(int i){
        System.out.println("setting tab "+i);
        curTab = i;
        updateActionBar();
    }
    public int getCurTab(){
        System.out.println("getting tab "+curTab);
        return curTab;
    }

    public void changeTab(int i){
        fragmentBirdsEyeViewContainer.setTab(i);
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
            setFragment(FragmentList.FRAGMENT_LANDMARK_INFO);
        }
    }
}
