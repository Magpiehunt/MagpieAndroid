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

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;

public class FragmentPrizes extends Fragment implements FragmentPrizesList.OnHuntSelectedListener{
    private static final String TAG = "Prizes Fragment";
    public static final int FRAGMENT_PRIZES_LIST = 0;
    public static final int FRAGMENT_PRIZES_INFO = 1;
    private HuntManager mHuntManager;
    private FragmentPrizesList fragmentPrizesList;
    private FragmentPrizeInfo fragmentPrizeInfo;
    private int curFrag;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prizes, container, false);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        setFragment(FRAGMENT_PRIZES_LIST);
        return view;
    }

    public static FragmentPrizes newInstance() {
        FragmentPrizes f = new FragmentPrizes();
        return f;
    }

    public void updateActionBar(){
        ActivityBase activityBase = ((ActivityBase) getActivity());
        if(activityBase == null) {
            return;
        }
        activityBase.getSupportActionBar().setTitle("My Prizes");
        activityBase.menuSettingsVisibility(false);
        if(curFrag == FRAGMENT_PRIZES_LIST) {
            activityBase.setBackButtonOnOff(false);
        }
        else if(curFrag == FRAGMENT_PRIZES_INFO){
            activityBase.setBackButtonOnOff(true);
        }

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        updateActionBar();
    }


    public void setFragment(int i) {
        if (!isAdded()) return;
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        curFrag = i;
        if(i == FRAGMENT_PRIZES_LIST) {

            if(fragmentPrizesList == null) {
                fragmentPrizesList = FragmentPrizesList.newInstance();
            }
            ft.replace(R.id.currentfragment, fragmentPrizesList);
        }
        else if(i == FRAGMENT_PRIZES_INFO){

            ft.replace(R.id.currentfragment, FragmentPrizeInfo.newInstance());
        }
        ft.commit();
    }

    public void updateList(){
        if(fragmentPrizesList != null){
            fragmentPrizesList.updateList();
        }
    }

    public void onBackPressed(){
        if(curFrag == FRAGMENT_PRIZES_INFO){
            setFragment(FRAGMENT_PRIZES_LIST);
            curFrag = FRAGMENT_PRIZES_LIST;
        }
    }
    @Override
    public void onHuntSelected(Hunt b) {
        mHuntManager.setFocusAward(b.getID());
        setFragment(FRAGMENT_PRIZES_INFO);
    }
}
