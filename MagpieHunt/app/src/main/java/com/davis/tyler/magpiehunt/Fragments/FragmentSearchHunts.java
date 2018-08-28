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


import com.davis.tyler.magpiehunt.Adapters.SearchCollectionAdapter;
import com.davis.tyler.magpiehunt.CMS.CMSCommunicator;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;

import java.util.LinkedList;


/**
 *
 * This class displays all Collections available on the CMS (during testing the CMS was running locally)
 * RecyclerView for this fragment is SearchCollectionAdapter
 */
public class FragmentSearchHunts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "SearchCollectionsFrag";


    protected RecyclerView mRecyclerView;
    protected SearchCollectionAdapter mModelAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected Context context;
    private HuntManager mHuntManager;
    private CMSCommunicator mCMSCommunicator;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentSearchHunts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentSearchHunts.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSearchHunts newInstance() {
        FragmentSearchHunts fragment = new FragmentSearchHunts();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        this.context = this.getActivity();
        mCMSCommunicator = new CMSCommunicator();

    }
    public static FragmentSearchHunts newInstance(HuntManager huntManager) {
        FragmentSearchHunts f = new FragmentSearchHunts();
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

        View rootView = inflater.inflate(R.layout.fragment_search_collections, container, false);


        //init landmark_list_green
        mRecyclerView = rootView.findViewById(R.id.searchView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        setRecyclerViewLayoutManager();

        //get data from api
        //TODO make class to make http requests. pass that  class to this fragment
        //method call to query all hunts here that will return a list of hunts to display
        LinkedList<Hunt> hunts = mHuntManager.getAllUnCompletedHunts();
        mModelAdapter = new SearchCollectionAdapter(hunts, getContext());
        mRecyclerView.setAdapter(mModelAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



        return rootView;
    }




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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
