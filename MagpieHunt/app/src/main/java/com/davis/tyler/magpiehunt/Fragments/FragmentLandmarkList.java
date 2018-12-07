package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerAdapter;
import com.davis.tyler.magpiehunt.Adapters.LandmarkAdapter;
import com.davis.tyler.magpiehunt.Dialogs.DialogAddHunt;
import com.davis.tyler.magpiehunt.Hunts.Award;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.Listeners.OnSwipeTouchListener;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Spinners.SpinnerHuntFilter;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
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
    private List<Badge> landmarks;
    private RelativeLayout badgeCompleted;
    private ImageView superBadge;
    private DialogAddHunt dialogAddHunt;


    public static FragmentLandmarkList newInstance() {
        FragmentLandmarkList f = new FragmentLandmarkList();
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.e(TAG, "OnCreateView"+this);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        View rootView = inflater.inflate(R.layout.fragment_landmark_list, container, false);

        badgeCompleted = rootView.findViewById(R.id.badge_completed);
        badgeCompleted.setVisibility(View.GONE);
        setPagerSwipe(true);
        superBadge = rootView.findViewById(R.id.img_super_badge);

        badgeCompleted.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft() {
                setPagerSwipe(true);
                ((ActivityBase)getActivity()).swipedToPrize();
            }

            public void onSwipeRight() {
                setPagerSwipe(true);
                ((ActivityBase)getActivity()).swipedToPrize();
            }
        });


        landmarks = mHuntManager.getFocusedSortedBadges();

        mRecyclerView = rootView.findViewById(R.id.landmarksRecyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        setRecyclerViewLayoutManager();

        mModelAdapter = new LandmarkAdapter(landmarks,this.getActivity(), mListener);

        // Set the adapter for RecyclerView.
        mRecyclerView.setAdapter(mModelAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        dialogAddHunt = new DialogAddHunt(getActivity());
        dialogAddHunt.setCancelable(false);

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
        if(landmarks != null) {
            landmarks.clear();
            landmarks.addAll(mHuntManager.getFocusedSortedBadges());
            mModelAdapter.notifyDataSetChanged();
        }
    }

    public void updateFocusHunts(){
        if (landmarks != null) {

            landmarks.clear();
            landmarks.addAll(mHuntManager.getFocusedSortedBadges());
            updateList();
        }
        if(mHuntManager != null) {
            updateBadgeDisplay();
            updateList();
        }

    }

    public void updateBadgeDisplay(){
        //whenever a badge is completed this method is called
        //if there is a single focused hunt, alert the user their super badge is complete
        if(mHuntManager.getFocusAward() != null){
            Award a = mHuntManager.getFocusAward();

            if(a.getIsNew()) {
                Picasso.get().load("http://206.189.204.95/superbadge/image/"+a.getSuperBadgeIcon())
                        .fit()
                        .centerCrop()
                        .into(superBadge);
                badgeCompleted.setVisibility(View.VISIBLE);
                setPagerSwipe(false);
            }
            else {
                badgeCompleted.setVisibility(View.GONE);
                setPagerSwipe(true);
            }
        }
        else{
            badgeCompleted.setVisibility(View.GONE);
            setPagerSwipe(true);
        }
    }
    public void setHuntCompleteNotificationList(Hunt h)
    {
        if(h.getIsCompleted() && h.getAward().getIsNew()) {
            Picasso.get().load("http://206.189.204.95/superbadge/image/"+h.getAward().getSuperBadgeIcon())
                    .fit()
                    .centerCrop()
                    .into(superBadge);
            badgeCompleted.setVisibility(View.VISIBLE);
            setPagerSwipe(false);
            mHuntManager.setFocusAward(h.getID());
        }
        else {
            badgeCompleted.setVisibility(View.GONE);
            setPagerSwipe(true);
        }
    }

    public void setPagerSwipe(boolean b){
        ActivityBase activityBase = (ActivityBase)getActivity();
        if(activityBase != null)
            activityBase.setPagerSwipe(b);
    }

    public interface OnLandmarkSelectedListener {
        void onLandmarkSelected(Badge b);
    }

}
