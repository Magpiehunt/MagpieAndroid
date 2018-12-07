package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerSearchAdapter;
import com.davis.tyler.magpiehunt.Adapters.LandmarkSearchAdapter;
import com.davis.tyler.magpiehunt.Adapters.SearchCollectionAdapter;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.CMS.JSONParser;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Spinners.SpinnerSearchFilter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


/**
 *
 * This class displays all Collections available on the CMS (during testing the CMS was running locally)
 * RecyclerView for this fragment is SearchCollectionAdapter
 */
public class FragmentSearchHunts extends Fragment implements View.OnFocusChangeListener, View.OnClickListener{
    private static final String TAG = "SearchCollectionsFrag";
    protected RecyclerView mRecyclerView;
    protected SearchCollectionAdapter mModelAdapter;
    protected LandmarkSearchAdapter mModelAdapterLandmark;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected Context context;
    private HuntManager mHuntManager;
    private EditText mSearchText;
    private LinkedList<Hunt> hunts;
    private LinkedList<Badge> badges;
    private SharedPreferences preferences;
    private RelativeLayout spinnerContainer;
    private ImageView btn_Toggle;


    /**
     * @return A new instance of fragment FragmentSearchHunts.
     */
    public static FragmentSearchHunts newInstance() {
        FragmentSearchHunts f = new FragmentSearchHunts();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("oncreate searchhunts");
        mHuntManager = ((ActivityBase)getActivity()).getData();
        View rootView = inflater.inflate(R.layout.fragment_search_collections, container, false);
        this.context = this.getActivity();
        //test = rootView.findViewById(R.id.imageView2);
        //init landmark_list_green
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mRecyclerView = rootView.findViewById(R.id.searchView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        btn_Toggle = rootView.findViewById(R.id.btn_toggle);
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
                    if(((FragmentOverallHuntTabs)getParentFragment()).getSearchHuntsElseLandmarks()){
                        searchForHunts(mSearchText.getText().toString());
                    }else {
                        searchForBadges(mSearchText.getText().toString());
                    }
                    hideSoftKeyboard();
                }
                return false;
            }
        });
        spinnerContainer = rootView.findViewById(R.id.spinnercontainer);
        mSearchText.setOnFocusChangeListener(this);
        btn_Toggle.setOnClickListener(this);
        setRecyclerViewLayoutManager();


        hunts = mHuntManager.getmSearchHunts();
        if(hunts == null)
            hunts = new LinkedList<>();
        mModelAdapter = new SearchCollectionAdapter(hunts, getContext(), mHuntManager, this);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        SpinnerSearchFilter spinner = (SpinnerSearchFilter) rootView.findViewById(R.id.spinner);
        CheckableSpinnerSearchAdapter adapter = new CheckableSpinnerSearchAdapter(getContext(), this);
        spinner.setAdapter(adapter);
        spinner.setSpinnerSearchEventsListener((ActivityBase)getActivity());

        badges = mHuntManager.getmSearchBadges();
        if(badges == null)
            badges = new LinkedList<>();
        mModelAdapterLandmark = new LandmarkSearchAdapter(badges, getContext(),((FragmentOverallHuntTabs)getParentFragment()));

        if(((FragmentOverallHuntTabs)getParentFragment()).getSearchHuntsElseLandmarks()){
            mRecyclerView.setAdapter(mModelAdapter);
        }else {
            mRecyclerView.setAdapter(mModelAdapterLandmark);
        }


        return rootView;
    }

    public void setAdapterHuntsOnElseOff(boolean onElseOff){
        ((FragmentOverallHuntTabs)getParentFragment()).setSearchHuntsElseLandmarks(onElseOff);
        if(onElseOff){
            mRecyclerView.setAdapter(mModelAdapter);
            spinnerContainer.setVisibility(View.VISIBLE);
            mSearchText.setHint("Enter ZIP, City or State");

        }
        else{
            mRecyclerView.setAdapter(mModelAdapterLandmark);
            spinnerContainer.setVisibility(View.GONE);
            mSearchText.setHint("Enter Keyword");
        }
    }

    public void sortWalkingDistance(){
        mHuntManager.getSortedHuntsByDistance(hunts);
        mModelAdapter.notifyDataSetChanged();
    }
    public void sortClosest(){
        if(hasLocPermission()) {
            mHuntManager.getSortedHuntsByClosestBadge(hunts, ((ActivityBase) getActivity()).getmLocationTracker());
            mModelAdapter.notifyDataSetChanged();
        }else{
            sortWalkingDistance();
            Toast.makeText(getContext(), "Turn on location permission to sort by closest", Toast.LENGTH_SHORT).show();
        }
    }
    public void sortNumberBadges(){
        mHuntManager.getSortedHuntsByNumberOfBadges(hunts);
        mModelAdapter.notifyDataSetChanged();
    }
    private void searchForHunts(String query){
        Log.e(TAG, query);
        if(Pattern.matches("^[\\d]{5}$", query)){
            searchByZip(query);
        }
        else{
            searchByCity(query);

        }
    }
    public void searchByZip(String zip){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest("http://206.189.204.95/api/v3/hunts/zipcode/"+zip,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        //this will return list of hunts
                        Log.e(TAG, "Response gained");
                        JSONParser jsonparser = new JSONParser(jsonArray);
                        hunts = jsonparser.getAllHunts();
                        for(Hunt h: hunts){
                            if(mHuntManager.getHuntByID(h.getID()) == null){
                                mHuntManager.addHunt(h);
                            }

                        }
                        mHuntManager.setmSearchHunts(hunts);
                        if(hunts == null){
                            Toast.makeText(getContext(), "No hunts found, please try again...", Toast.LENGTH_SHORT).show();
                            mHuntManager.setmSearchHunts(new LinkedList<Hunt>());
                        }
                        else
                            updateList();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), "No hunts found, please try again...", Toast.LENGTH_SHORT).show();

                    }
                });
        queue.add(request);
    }
    public void searchByState(String state){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest("http://206.189.204.95/api/v3/hunts/state/"+state,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        //this will return list of hunts
                        Log.e(TAG, "Response gained");
                        JSONParser jsonparser = new JSONParser(jsonArray);
                        hunts = jsonparser.getAllHunts();
                        for(Hunt h: hunts){
                            if(mHuntManager.getHuntByID(h.getID()) == null){
                                mHuntManager.addHunt(h);
                            }

                        }

                        mHuntManager.setmSearchHunts(hunts);
                        if(hunts == null){
                            Toast.makeText(getContext(), "No hunts found, please try again...", Toast.LENGTH_SHORT).show();
                            mHuntManager.setmSearchHunts(new LinkedList<Hunt>());
                        }
                        else
                            updateList();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), "No hunts found, please try again...", Toast.LENGTH_SHORT).show();

                    }
                });

        queue.add(request);
    }
    public void searchByCity(final String city){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest("http://206.189.204.95/api/v3/hunts/city/"+city,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        //this will return list of hunts
                        Log.e(TAG, "Response gained");
                        JSONParser jsonparser = new JSONParser(jsonArray);
                        hunts = jsonparser.getAllHunts();
                        for(Hunt h: hunts){
                            if(mHuntManager.getHuntByID(h.getID()) == null){
                                mHuntManager.addHunt(h);
                            }

                        }
                        mHuntManager.setmSearchHunts(hunts);
                        if(hunts == null){
                            Toast.makeText(getContext(), "No hunts found, please try again...", Toast.LENGTH_SHORT).show();
                            mHuntManager.setmSearchHunts(new LinkedList<Hunt>());
                        }
                        else {
                            updateList();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        searchByState(city);
                    }
                });
        queue.add(request);
    }


    public void setImage(Bitmap bitmap){
        //test.setImageBitmap(bitmap);
    }
    public void updateList(){
        boolean b = ((FragmentOverallHuntTabs)getParentFragment()).getSearchHuntsElseLandmarks();
        if(b) {
            mModelAdapter.updateList(hunts);
            mModelAdapter.notifyDataSetChanged();
        }
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
    private void searchForBadges(String query){


        LinkedList<Badge> res = mHuntManager.getAllBadgesByKeyword(query);
        if(res.size() == 0)
            Toast.makeText(context, "No results found", Toast.LENGTH_LONG).show();
        mModelAdapterLandmark.updateList(res);
        mModelAdapterLandmark.notifyDataSetChanged();


    }
    public boolean hasLocPermission(){
        if(preferences != null) {
            return preferences.getBoolean("fine", false) && preferences.getBoolean("coarse", false);
        }
        return false;
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

    public void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void onAddHuntListener(Hunt h){
        ((ActivityBase)getActivity()).onAddHuntEvent(h);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_toggle){
            boolean b = ((FragmentOverallHuntTabs)getParentFragment()).getSearchHuntsElseLandmarks();
            setAdapterHuntsOnElseOff(!b);
        }
    }
}
