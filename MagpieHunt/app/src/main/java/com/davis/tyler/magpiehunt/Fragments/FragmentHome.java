package com.davis.tyler.magpiehunt.Fragments;

import android.app.Activity;
import android.content.Context;
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
    public static final int FRAGMENT_PRIVACY = 2;
    public static final int FRAGMENT_TERMS = 3;

    private FragmentHuntsList collectionFragment;
    private FragmentSettings fragmentSettings;
    private FragmentTermsAndConditions fragmentTerms;
    private FragmentPrivacyPolicy fragmentPrivacy;
    private HuntManager mHuntManager;
    private int curFrag;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        curFrag = 0;
        setFragment(FRAGMENT_HUNTS_LIST);
        return view;
    }

    public static FragmentHome newInstance() {
        FragmentHome f = new FragmentHome();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }
    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");
    }

    private Activity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        updateActionBar();
    }

    public void updateActionBar(){
        if(context == null) {
            return;
        }
        if(curFrag == FRAGMENT_SETTINGS){
            ((ActivityBase)context).setBackButtonOnOff(true);
            ((ActivityBase)context).getSupportActionBar().setTitle("Settings");
            ((ActivityBase)context).menuSettingsVisibility(false);
        }
        else if(curFrag == FRAGMENT_HUNTS_LIST){


            ((ActivityBase)context).getSupportActionBar().setTitle("My Collections");
            ((ActivityBase)context).menuSettingsVisibility(true);
        }
        else if(curFrag == FRAGMENT_TERMS)
        {
            ((ActivityBase)context).setBackButtonOnOff(true);
            ((ActivityBase)context).getSupportActionBar().setTitle("Terms of Use");
            ((ActivityBase)context).menuSettingsVisibility(false);
        }
        else if(curFrag == FRAGMENT_PRIVACY)
        {
            ((ActivityBase)context).setBackButtonOnOff(true);
            ((ActivityBase)context).getSupportActionBar().setTitle("Privacy Policy");
            ((ActivityBase)context).menuSettingsVisibility(false);
        }
    }

    public void setFragment(int i) {
        if (!isAdded()) return;
        curFrag = i;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(i == FRAGMENT_HUNTS_LIST) {
            if(collectionFragment == null) {
                collectionFragment = FragmentHuntsList.newInstance();
            }
            ft.replace(R.id.currentfragment, collectionFragment);
        }else if( i == FRAGMENT_SETTINGS){
            ((ActivityBase)getActivity()).setBackButtonOnOff(true);
            if(fragmentSettings == null) {
                fragmentSettings = FragmentSettings.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentSettings);
        }
        else if(i == FRAGMENT_PRIVACY)
        {
            ((ActivityBase)getActivity()).setBackButtonOnOff(true);
            if(fragmentPrivacy == null){
                fragmentPrivacy = FragmentPrivacyPolicy.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentPrivacy);
        }
        else if(i == FRAGMENT_TERMS)
        {
            ((ActivityBase)getActivity()).setBackButtonOnOff(true);
            if(fragmentTerms == null){
                fragmentTerms = FragmentTermsAndConditions.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentTerms);
        }
        updateActionBar();
        ft.addToBackStack(null);
        ft.commit();
    }
    @Override
    public void onCollectionSelected(int id, String name) {

        mHuntManager.setFocusHunt(id);
        ((ActivityBase)getActivity()).collectionClicked();
    }

    @Override
    public void onCollectionDeleted() {
        ((ActivityBase)getActivity()).onCollectionDeleted();
    }

    @Override
    public void onCollectionRestored() {
        ((ActivityBase)getActivity()).onCollectionRestored();
    }

    public void onBackPressed(){
        Log.e(TAG, "curfrag: "+curFrag);
        if(curFrag == FRAGMENT_SETTINGS){
            ((ActivityBase)getActivity()).setBackButtonOnOff(false);
            setFragment(FRAGMENT_HUNTS_LIST);
            curFrag = FRAGMENT_HUNTS_LIST;
            updateActionBar();
        }
        else if(curFrag == FRAGMENT_TERMS || curFrag == FRAGMENT_PRIVACY)
        {
            ((ActivityBase)getActivity()).setBackButtonOnOff(true);
            setFragment(FRAGMENT_SETTINGS);
            curFrag = FRAGMENT_SETTINGS;
            updateActionBar();
        }
    }

    public void updateHuntsList(){
        if(collectionFragment != null)
            collectionFragment.updateList();
    }
}
