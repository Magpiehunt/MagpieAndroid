package com.davis.tyler.magpiehunt.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;

public class FragmentSearch extends Fragment implements FragmentHuntsList.OnCollectionSelectedListener{
    private static final String TAG = "Search Fragment";
    public static final int FRAGMENT_COLLECTIONS = 0;
    public static final int FRAGMENT_SEARCH = 1;
    private FragmentHuntsList collectionFragment;
    private HuntManager mHuntManager;
    private FragmentSearchHunts searchCollectionsFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Log.e(TAG, "ONCREATE");
        setFragment(FRAGMENT_COLLECTIONS);
        return view;
    }

    public static FragmentSearch newInstance(HuntManager huntManager) {
        FragmentSearch f = new FragmentSearch();
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
    @Override
    public void onCollectionSelected(int id, String name) {

        mHuntManager.setFocusHunt(id);
        ((ActivityBase)getActivity()).changePage(ActivityBase.FRAGMENT_MAP);
        ((ActivityBase)getActivity()).updateList();

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

    public void setFragment(int i) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(i == FRAGMENT_COLLECTIONS) {
            if(collectionFragment == null) {
                collectionFragment = FragmentHuntsList.newInstance(mHuntManager);
            }
            ft.replace(R.id.currentfragment, collectionFragment);
        }
        else if(i == FRAGMENT_SEARCH) {
            if(searchCollectionsFragment == null) {
                searchCollectionsFragment = FragmentSearchHunts.newInstance(mHuntManager);
            }
            ft.replace(R.id.currentfragment, searchCollectionsFragment);
        }
        ft.addToBackStack(null);
        ft.commit();
    }
}
