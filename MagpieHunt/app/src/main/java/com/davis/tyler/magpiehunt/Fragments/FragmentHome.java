package com.davis.tyler.magpiehunt.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;

public class FragmentHome extends Fragment {
    private static final String TAG = "Home Fragment";
    public static final int FRAGMENT_LANDMARK_LIST = 0;
    public static final int FRAGMENT_LANDMARK_INFO = 1;
    public static final int FRAGMENT_QR_READER= 2;
    public static final int FRAGMENT_QUIZ = 3;
    public static final int FRAGMENT_TIMER = 4;
    public static final int FRAGMENT_BADGE_OBTAINED = 5;
    private FragmentLandmarkList mLandmarkListFragment;
    private FragmentLandmarkInfo mFragmentLandmarkInfo;
    private FragmentQuiz fragmentQuiz;
    private FragmentTimer fragmentTimer;
    private FragmentBadgeObtained fragmentBadgeObtained;
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
                Log.e(TAG, "landmarklist null");
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
        ft.commit();
        getChildFragmentManager().executePendingTransactions();
        if(i == FRAGMENT_TIMER) {
            fragmentTimer.startTimer();
            Log.e(TAG, "timer started");
        }
        else if(i == FRAGMENT_BADGE_OBTAINED){
            fragmentBadgeObtained.updateInfo(mHuntManager.getFocusBadge());
        }
    }

    public void updateFocusHunts(){
        if(mLandmarkListFragment != null)
            mLandmarkListFragment.updateFocusHunts();
    }
    public void updateSpinner(){
        if(mLandmarkListFragment != null){
            mLandmarkListFragment.updateSpinner();
        }
    }
}
