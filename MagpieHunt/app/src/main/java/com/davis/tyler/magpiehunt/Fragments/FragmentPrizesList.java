package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
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

import com.davis.tyler.magpiehunt.Adapters.PrizesAdapter;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;

import java.util.LinkedList;

public class FragmentPrizesList extends Fragment {

    private static final String TAG = "FragmentPrizesList";

    protected RecyclerView mRecyclerView;
    protected PrizesAdapter mModelAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private FragmentPrizesList.OnHuntSelectedListener mListener;
    private HuntManager mHuntManager;
    private LinkedList<Hunt> prize_items;



    // TODO: Rename and change types and number of parameters
    public static FragmentPrizesList newInstance(HuntManager huntManager) {
        FragmentPrizesList f = new FragmentPrizesList();
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
        View rootView = inflater.inflate(R.layout.fragment_prizes_list, container, false);

        prize_items = mHuntManager.getAllCompletedHunts();



        mRecyclerView = rootView.findViewById(R.id.prizesRecyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        setRecyclerViewLayoutManager();

        mModelAdapter = new PrizesAdapter(prize_items, this.getActivity(), this, mListener);

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

        mListener = (FragmentPrizes)getParentFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void updateList(){
        prize_items = mHuntManager.getAllCompletedHunts();
        mModelAdapter.updateList(prize_items);
        mModelAdapter.notifyDataSetChanged();
    }



    //TODO implement this before release, just for testing
    public interface OnHuntSelectedListener {
        // TODO: Update argument type and name
        void onHuntSelected(Hunt b);
    }
}
