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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerSearchAdapter;
import com.davis.tyler.magpiehunt.Adapters.SearchCollectionAdapter;
import com.davis.tyler.magpiehunt.CMS.CMSCommunicator;
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
public class FragmentSearchHunts extends Fragment implements View.OnFocusChangeListener{
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
    private EditText mSearchText;
    private List<String> states;
    private ArrayList<String> mEntries;
    private LinkedList<Hunt> hunts;
    //private ImageView test;
    private Target target;

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
                    searchForHunts(mSearchText.getText().toString());
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


        //get data from api
        //TODO make class to make http requests. pass that  class to this fragment
        //method call to query all hunts here that will return a list of hunts to display

        hunts = mHuntManager.getmSearchHunts();
        if(hunts == null)
            hunts = new LinkedList<>();
        mModelAdapter = new SearchCollectionAdapter(hunts, getContext(), mHuntManager, this);
        mRecyclerView.setAdapter(mModelAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCMSCommunicator = new CMSCommunicator();

        SpinnerSearchFilter spinner = (SpinnerSearchFilter) rootView.findViewById(R.id.spinner);
        CheckableSpinnerSearchAdapter adapter = new CheckableSpinnerSearchAdapter(getContext());
        spinner.setAdapter(adapter);
        spinner.setSpinnerSearchEventsListener((ActivityBase)getActivity());


        return rootView;
    }


    private void searchForHunts(String query){
        Log.e(TAG, query);
        if(Pattern.matches("^[\\d]{5}$", query)){
            searchByZip(query);
        }
        /*else if(Pattern.matches("^[a-zA-Z]{2}$", query)){
            Log.e(TAG, "State search result: "+mCMSCommunicator.getHuntsByState(query));
        }
        else if(Pattern.matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$", query)){
            Log.e(TAG, "City search result: "+mCMSCommunicator.getHuntsByCity(query));
        }*/
        else{
            searchByCity(query);

        }
    }
    public void searchByZip(String zip){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest("http://206.189.204.95/api/v3/hunts/zip/"+zip,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        //this will return list of hunts
                        Log.e(TAG, "Response gained");
                        JSONParser jsonparser = new JSONParser(jsonArray);
                        hunts = jsonparser.getAllHunts();
                        mHuntManager.setmSearchHunts(hunts);
                        if(hunts == null){
                            Toast.makeText(getContext(), "No hunts found, please try again...", Toast.LENGTH_SHORT).show();
                            mHuntManager.setmSearchHunts(new LinkedList<Hunt>());
                        }
                        else
                            updateList();
                        //TODO sort list by filter, then update list

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
                        mHuntManager.setmSearchHunts(hunts);
                        if(hunts == null){
                            Toast.makeText(getContext(), "No hunts found, please try again...", Toast.LENGTH_SHORT).show();
                            mHuntManager.setmSearchHunts(new LinkedList<Hunt>());
                        }
                        else
                            updateList();
                        //TODO sort list by filter, then update list

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
                        mHuntManager.setmSearchHunts(hunts);
                        if(hunts == null){
                            Toast.makeText(getContext(), "No hunts found, please try again...", Toast.LENGTH_SHORT).show();
                            mHuntManager.setmSearchHunts(new LinkedList<Hunt>());
                        }
                        else {
                            updateList();

                        }
                        //TODO sort list by filter, then update list

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

    public void getAllHunts(){
        System.out.println("test 1");
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest request = new JsonArrayRequest("http://206.189.204.95/api/v3/hunts/city/seattle",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        //this will return list of hunts
                        Log.e(TAG, "Response gained");
                        JSONParser jsonparser = new JSONParser(jsonArray);
                        hunts = jsonparser.getAllHunts();

                        updateList();
                        //TODO sort list by filter, then update list

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(request);
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
    public void updateList(){
        mModelAdapter.updateList(hunts);
        mModelAdapter.notifyDataSetChanged();
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

    public void onAddHuntListener(){
        ((ActivityBase)getActivity()).onAddHuntEvent();
    }

}
