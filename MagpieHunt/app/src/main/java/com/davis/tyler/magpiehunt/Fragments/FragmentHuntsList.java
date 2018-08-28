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
import android.widget.Button;


import com.davis.tyler.magpiehunt.Adapters.CollectionAdapter;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment displays all collections currently saved in the Room database in a recyclerview
 * RecyclerView adapter for this fragment is CollectionAdapter
 */
public class FragmentHuntsList extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentHuntsList";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected RecyclerView mRecyclerView;
    protected CollectionAdapter mModelAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<Hunt> mDataset;
    private Button addCollectionBtn;
    private HuntManager mHuntManager;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnCollectionSelectedListener mListener;

    public FragmentHuntsList() {
        // Required empty public constructor
    }


    //    // TODO: Rename and change types and number of parameters
    public static FragmentHuntsList newInstance() {
        FragmentHuntsList fragment = new FragmentHuntsList();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_my_collections, container, false);
        rootView.setTag(TAG);


        this.addCollectionBtn = rootView.findViewById(R.id.button_addCollection_collection);
        addCollectionBtn.setOnClickListener(FragmentHuntsList.this);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        mModelAdapter = new CollectionAdapter(mDataset, FragmentHuntsList.TAG, this.getActivity(), FragmentHuntsList.this, this.mListener);
        // Set the adapter for RecyclerView.
        mRecyclerView.setAdapter(mModelAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;

    }
    public static FragmentHuntsList newInstance(HuntManager huntManager) {
        FragmentHuntsList f = new FragmentHuntsList();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //TODO: restore state of fragment
        }//end if
    }//end

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //TODO: saved state of fragment here
    }//end

    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
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

            mListener = (OnCollectionSelectedListener) getParentFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_addCollection_collection:
                ((FragmentSearch)getParentFragment()).setFragment(FragmentSearch.FRAGMENT_SEARCH);
                break;


            default:
                break;
        }//end switch
    }//end onClick


    // TODO: Replace the test data within this with data from room DB
    private void initializeData() {
        mDataset = new ArrayList<>();


        mDataset.addAll(mHuntManager.getAllUnCompletedHunts());
        Hunt testCollection = new Hunt();
        testCollection.setName("Test Walk Talk");
        testCollection.setAbbreviation("TWT");
        testCollection.setDescription("hello this is testing the description display for the collection fragments");
        mDataset.add(testCollection);

        testCollection = new Hunt();
        testCollection.setName("Walk Test Mag");
        testCollection.setAbbreviation("WTM");
        mDataset.add(testCollection);

        testCollection = new Hunt();
        testCollection.setName("Card Walk Test");
        testCollection.setAbbreviation("CWT");
        mDataset.add(testCollection);

        testCollection = new Hunt();
        testCollection.setName("Walk onthe Wildside");
        testCollection.setAbbreviation("WOW");
        mDataset.add(testCollection);

        testCollection = new Hunt();
        testCollection.setName("No Skipping Allowed");
        testCollection.setAbbreviation("NSA");
        mDataset.add(testCollection);

        testCollection = new Hunt();
        testCollection.setName("Cards Are Easy");
        testCollection.setAbbreviation("CAE");
        mDataset.add(testCollection);

        testCollection = new Hunt();
        testCollection.setName("Walk With Me");
        testCollection.setAbbreviation("WWM");
        mDataset.add(testCollection);

        testCollection = new Hunt();
        testCollection.setName("The Last Walk");
        testCollection.setAbbreviation("TLW");
        mDataset.add(testCollection);

    }//end

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    //TODO implement this before release, just for testing
    public interface OnCollectionSelectedListener {
        void onCollectionSelected(int cid, String name);
    }
}
