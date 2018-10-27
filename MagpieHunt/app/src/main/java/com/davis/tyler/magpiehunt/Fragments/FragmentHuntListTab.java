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
import com.davis.tyler.magpiehunt.Dialogs.DialogDeleteHunt;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.IOnHuntDeleteResponse;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.RecyclerItemHelpers.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FragmentHuntListTab extends Fragment implements View.OnClickListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, IOnHuntDeleteResponse {

    private static final String TAG = "FragmentHuntstab";
    protected RecyclerView mRecyclerView;
    protected CollectionAdapter mModelAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected LinkedList<Hunt> mDataset;
    private Button addCollectionBtn;
    private HuntManager mHuntManager;
    private LinearLayout linearLayout;
    private FragmentHuntsList.OnCollectionSelectedListener mListener;
    private DialogDeleteHunt dialogDeleteHunt;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mHuntManager = ((ActivityBase)getActivity()).getData();
        View rootView = inflater.inflate(R.layout.fragment_hunts_list_tab, container, false);
        rootView.setTag(TAG);
        initializeData();
        linearLayout = rootView.findViewById(R.id.container);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        setRecyclerViewLayoutManager();

        mModelAdapter = new CollectionAdapter(mDataset, FragmentHuntListTab.TAG, this.getActivity(), FragmentHuntListTab.this, this.mListener);
        // Set the adapter for RecyclerView.
        mRecyclerView.setAdapter(mModelAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dialogDeleteHunt = new DialogDeleteHunt(getActivity());
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        return rootView;

    }
    public static FragmentHuntListTab newInstance(HuntManager huntManager) {
        FragmentHuntListTab f = new FragmentHuntListTab();
        Bundle args = new Bundle();
        //args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        Log.e(TAG, "setArguments, args: "+args);
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");
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
        Log.e(TAG, "onsave");

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

        mListener = (FragmentHuntsList.OnCollectionSelectedListener) getParentFragment();
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
                ((ActivityBase)getActivity()).changePage(ActivityBase.FRAGMENT_OVERALL_HUNTS);
                break;

            default:
                break;
        }//end switch
    }//end onClick


    // TODO: Replace the test data within this with data from room DB
    private void initializeData() {
        mDataset = mHuntManager.getAllDownloadedUndeletedHunts();

    }//end


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CollectionAdapter.CollectionHolder) {

            final Hunt deletedItem = mDataset.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            /*deletedItem.setmIsDeleted(true);
            // remove the item from recycler view
            mHuntManager.deleteHuntByID(deletedItem.getID());
            mModelAdapter.removeItem(viewHolder.getAdapterPosition());*/

            dialogDeleteHunt.setHunt(deletedItem,this, viewHolder);
            dialogDeleteHunt.show();
        }
    }

    public void updateHuntsList(){
        mDataset = mHuntManager.getAllDownloadedUndeletedHunts();
        mModelAdapter.updateList(mDataset);

        mModelAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDelete(Hunt deletedItem, RecyclerView.ViewHolder viewHolder) {

        deletedItem.setmIsDeleted(true);
        // remove the item from recycler view
        //mHuntManager.deleteHuntByID(deletedItem.getID());
        mModelAdapter.removeItem(viewHolder.getAdapterPosition());
        mModelAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRestore() {
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
