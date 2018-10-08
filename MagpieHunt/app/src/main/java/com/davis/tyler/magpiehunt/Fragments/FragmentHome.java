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
import com.davis.tyler.magpiehunt.R;

public class FragmentHome extends Fragment implements FragmentHuntsList.OnCollectionSelectedListener {
    private static final String TAG = "Home Fragment";
    public static final int FRAGMENT_HUNTS_LIST = 0;
    public static final int FRAGMENT_SETTINGS = 1;
    private FragmentHuntsList collectionFragment;
    private FragmentSettings fragmentSettings;
    private HuntManager mHuntManager;
    private int curFrag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        curFrag = 0;
        setFragment(FRAGMENT_HUNTS_LIST);
        return view;
    }

    public static FragmentHome newInstance(HuntManager huntManager) {
        FragmentHome f = new FragmentHome();
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
        if(curFrag == FRAGMENT_SETTINGS){
            ((ActivityBase) getActivity()).setBackButtonOnOff(true);
            ((ActivityBase) getActivity()).getSupportActionBar().setTitle("Settings");
            ((ActivityBase) getActivity()).menuSettingsVisibility(false);
        }
        else if(curFrag == FRAGMENT_HUNTS_LIST){


            ((ActivityBase) getActivity()).getSupportActionBar().setTitle("My Collections");
            ((ActivityBase) getActivity()).menuSettingsVisibility(true);
        }
    }

    public void setFragment(int i) {
        curFrag = i;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(i == FRAGMENT_HUNTS_LIST) {
            if(collectionFragment == null) {
                collectionFragment = FragmentHuntsList.newInstance(mHuntManager);
            }
            ft.replace(R.id.currentfragment, collectionFragment);
        }else if( i == FRAGMENT_SETTINGS){
            ((ActivityBase)getActivity()).setBackButtonOnOff(true);
            if(fragmentSettings == null) {
                fragmentSettings = FragmentSettings.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentSettings);
        }
        updateActionBar();
        ft.addToBackStack(null);
        ft.commit();
    }
    @Override
    public void onCollectionSelected(int id, String name) {

        mHuntManager.setFocusHunt(id);
        ((ActivityBase)getActivity()).changePage(ActivityBase.FRAGMENT_LIST);
        ((ActivityBase)getActivity()).updateList();

    }

    public void onBackPressed(){
        Log.e(TAG, "curfrag: "+curFrag);
        if(curFrag == FRAGMENT_SETTINGS){
            ((ActivityBase)getActivity()).setBackButtonOnOff(false);
            setFragment(FRAGMENT_HUNTS_LIST);
            curFrag = FRAGMENT_HUNTS_LIST;
            updateActionBar();
        }
    }
    public void updateHuntsList(){
        if(collectionFragment != null)
            collectionFragment.updateList();
    }
}
