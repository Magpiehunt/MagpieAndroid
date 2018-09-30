package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CollectionAdapter;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.RecyclerItemHelpers.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This fragment displays all collections currently saved in the Room database in a recyclerview
 * RecyclerView adapter for this fragment is CollectionAdapter
 */
public class FragmentHuntsList extends Fragment implements View.OnClickListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

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
    private LinearLayout linearLayout;

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

        linearLayout = rootView.findViewById(R.id.container);

        this.addCollectionBtn = rootView.findViewById(R.id.button_addCollection_collection);
        addCollectionBtn.setOnClickListener(FragmentHuntsList.this);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        mModelAdapter = new CollectionAdapter(mDataset, FragmentHuntsList.TAG, this.getActivity(), FragmentHuntsList.this, this.mListener);
        // Set the adapter for RecyclerView.
        mRecyclerView.setAdapter(mModelAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
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
                ((ActivityBase)getActivity()).changePage(ActivityBase.FRAGMENT_SEARCH);
                break;


            default:
                break;
        }//end switch
    }//end onClick


    // TODO: Replace the test data within this with data from room DB
    private void initializeData() {
        mDataset = new ArrayList<>();


        mDataset.addAll(mHuntManager.getAllHunts());
        /*Hunt testCollection = new Hunt();
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
        mDataset.add(testCollection);*/

    }//end


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CollectionAdapter.CollectionHolder) {
            // get the removed item name to display it in snack bar
            String name = mDataset.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final Hunt deletedItem = mDataset.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            deletedItem.setmIsDeleted(true);
            // remove the item from recycler view
            mModelAdapter.removeItem(viewHolder.getAdapterPosition());
            mHuntManager.deleteHuntByID(deletedItem.getID());
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(linearLayout, name + " removed from collection!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    deletedItem.setmIsDeleted(false);
                    mModelAdapter.restoreItem(deletedItem, deletedIndex);
                    mHuntManager.addHunt(deletedItem);
                    mModelAdapter.notifyDataSetChanged();
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            mModelAdapter.notifyDataSetChanged();
        }
    }

    public void updateList(){
        mModelAdapter.updateList(mHuntManager.getAllHunts());
        mDataset = mHuntManager.getAllHunts();
        mModelAdapter.notifyDataSetChanged();
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
    //TODO implement this before release, just for testing
    public interface OnCollectionSelectedListener {
        void onCollectionSelected(int cid, String name);
    }


}
