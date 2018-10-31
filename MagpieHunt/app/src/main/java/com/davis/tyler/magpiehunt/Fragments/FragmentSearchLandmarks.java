package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerSearchAdapter;
import com.davis.tyler.magpiehunt.Adapters.LandmarkSearchAdapter;
import com.davis.tyler.magpiehunt.Adapters.SearchCollectionAdapter;
import com.davis.tyler.magpiehunt.CMS.JSONParser;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Spinners.SpinnerSearchFilter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;

import java.util.LinkedList;
import java.util.regex.Pattern;

public class FragmentSearchLandmarks extends Fragment implements View.OnFocusChangeListener{
    private static final String TAG = "SearchLandmarksFrag";
    protected RecyclerView mRecyclerView;
    protected LandmarkSearchAdapter mModelAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected Context context;
    private HuntManager mHuntManager;
    private EditText mSearchText;
    private LinkedList<Badge> badges;
    private Target target;

    /**
     * @return A new instance of fragment FragmentSearchHunts.
     */
    public static FragmentSearchLandmarks newInstance() {
        FragmentSearchLandmarks f = new FragmentSearchLandmarks();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mHuntManager = ((ActivityBase)getActivity()).getData();
        View rootView = inflater.inflate(R.layout.fragment_search_landmarks, container, false);
        this.context = this.getActivity();
        //test = rootView.findViewById(R.id.imageView2);
        //init landmark_list_green
        mRecyclerView = rootView.findViewById(R.id.searchView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSearchText = rootView.findViewById(R.id.searchText);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute our method for searching when they hit enter instead of making new line
                    //getAllHunts();

                    searchForBadges(mSearchText.getText().toString());
                    hideSoftKeyboard();
                }
                return false;
            }
        });

        mSearchText.setOnFocusChangeListener(this);
        setRecyclerViewLayoutManager();

        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                System.out.println("icon received: "+bitmap);
                //test.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                System.out.println("icon failed: ");
                e.printStackTrace();
            }


            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        //method call to query all hunts here that will return a list of hunts to display

        badges = mHuntManager.getmSearchBadges();
        if(badges == null)
            badges = new LinkedList<>();
        mModelAdapter = new LandmarkSearchAdapter(badges, getContext(),((FragmentBirdsEyeViewContainer)getParentFragment()));
        mRecyclerView.setAdapter(mModelAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    private void searchForBadges(String query){


        LinkedList<Badge> res = mHuntManager.getAllBadgesByKeyword(query);
        if(res.size() == 0)
            Toast.makeText(context, "No results found", Toast.LENGTH_LONG).show();
        mModelAdapter.updateList(res);
        mModelAdapter.notifyDataSetChanged();


    }



    public void getBadgeImage(String filename){
        System.out.println("getting icon: "+filename);
        String imageUrl = "http://206.189.204.95/badge/icon/"+filename;
        someMethod(filename);

    }


    private void someMethod(String filename) {
        Picasso.get().load("http://206.189.204.95/badge/icon/"+filename).into(target);
    }
    public void setImage(Bitmap bitmap){
        //test.setImageBitmap(bitmap);
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

    @Override
    public void onFocusChange(View view, boolean hasFocus) {

        final InputMethodManager inputManager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && !hasFocus) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
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



    public void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void onAddHuntListener(Hunt h){
        ((ActivityBase)getActivity()).onAddHuntEvent(h);
    }

}
