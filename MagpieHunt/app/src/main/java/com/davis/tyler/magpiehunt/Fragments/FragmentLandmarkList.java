package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;


import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerAdapter;
import com.davis.tyler.magpiehunt.Adapters.LandmarkAdapter;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.SpinnerHuntFilter;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * This fragment displays a list (RecyclerView) of landmarks in the selected collection. Landmarks are retrieved
 * from the Room database using Collection CID
 * Nested fragment inside of FragmentLandmarkListContainer
 * RecyclerView adapter for this fragment is LandmarkAdapter
 */
public class FragmentLandmarkList extends Fragment {

    private static final String TAG = "FragmentLandmarkList";

    protected RecyclerView mRecyclerView;
    protected LandmarkAdapter mModelAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private OnLandmarkSelectedListener mListener;
    private HuntManager mHuntManager;
    private List<Hunt> spinner_items;
    private Set<Hunt> selected_items;
    private List<Badge> landmarks;
    private TextView txt_select_hunts;



    // TODO: Rename and change types and number of parameters
    public static FragmentLandmarkList newInstance(HuntManager huntManager) {
        FragmentLandmarkList f = new FragmentLandmarkList();
        Bundle args = new Bundle();
        args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        Log.e(TAG, "setArguments, args: "+args);
        mHuntManager = (HuntManager)args.getSerializable("huntmanager");
        Log.e(TAG, "setArguments, huntman: "+mHuntManager);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "OnCreateView");
        View rootView = inflater.inflate(R.layout.fragment_landmark_list, container, false);

        //replace param with cid of collection from bundle
        selected_items = mHuntManager.getSelectedHunts();
        spinner_items = mHuntManager.getAllHunts();


        String headerText = "Filter";
        SpinnerHuntFilter spinner = (SpinnerHuntFilter) rootView.findViewById(R.id.spinner);
        //Set the max height of the dropdown spinner:
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(600);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        CheckableSpinnerAdapter adapter = new CheckableSpinnerAdapter(getContext(), headerText, spinner_items, selected_items);
        spinner.setAdapter(adapter);
        spinner.setSpinnerEventsListener((ActivityBase)getActivity());

        landmarks = mHuntManager.getSortedBadges();

        mRecyclerView = rootView.findViewById(R.id.landmarksRecyclerView);
        txt_select_hunts = (TextView) rootView.findViewById(R.id.txt_no_hunt_selected);
        mLayoutManager = new LinearLayoutManager(getActivity());
        setRecyclerViewLayoutManager();

        mModelAdapter = new LandmarkAdapter(landmarks, FragmentLandmarkList.TAG, this.getActivity(), FragmentLandmarkList.this, mListener);

        // Set the adapter for RecyclerView.
        mRecyclerView.setAdapter(mModelAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        RecyclerView.LayoutManager mana = mRecyclerView.getLayoutManager();
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }//end if

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }//end

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //TODO implement this before release, just for testing
        if (context instanceof OnLandmarkSelectedListener) {
            mListener = (OnLandmarkSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLandmarkSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void updateList(){

        mModelAdapter.notifyDataSetChanged();
    }

    public void onUpdateTextView(){
        if(txt_select_hunts != null) {
            if (selected_items.isEmpty()) {
                txt_select_hunts.setVisibility(View.VISIBLE);
            } else {
                txt_select_hunts.setVisibility(View.INVISIBLE);
            }
        }
    }
    public void updateFocusHunts(){

        landmarks.clear();
        for(Hunt h: mHuntManager.getSelectedHunts()){
            landmarks.addAll(h.getAllBadges());
        }
        if(mHuntManager.getSelectedHuntsSize() ==0){
            txt_select_hunts.setVisibility(View.VISIBLE);
        }
        else{
            txt_select_hunts.setVisibility(View.INVISIBLE);
        }
        updateList();
    }
    //TODO implement this before release, just for testing
    public interface OnLandmarkSelectedListener {
        // TODO: Update argument type and name
        void onLandmarkSelected(Badge b);
    }
}
