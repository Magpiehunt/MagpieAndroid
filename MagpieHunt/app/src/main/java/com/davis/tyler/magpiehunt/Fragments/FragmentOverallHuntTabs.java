package com.davis.tyler.magpiehunt.Fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerAdapter;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerOverallHuntsAdapter;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerSearchAdapter;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.Location.CameraManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Spinners.SpinnerHuntFilter;
import com.davis.tyler.magpiehunt.Spinners.SpinnerOverallHuntsFilter;
import com.davis.tyler.magpiehunt.Spinners.SpinnerSearchFilter;

import java.lang.reflect.Field;
import java.util.Set;

public class FragmentOverallHuntTabs extends Fragment implements FragmentHuntsList.OnCollectionSelectedListener{
    private static final String TAG = "OverallHuntTabs";

    public static final int FRAGMENT_GOOGLE_MAPS = 0;
    public static final int FRAGMENT_HUNT_LIST = 1;
    public static final int FRAGMENT_HUNT_SEARCH = 2;

    public static final int FILTER_NEARME = 0;
    public static final int FILTER_DOWNLOADED = 1;
    public static final int FILTER_SEARCHED = 2;
    //private ViewPager mViewPager;
    private FragmentHuntListTab fragmentHuntsList;
    private FragmentGoogleMapsHunts fragmentGoogleMaps;
    private FragmentSearchHunts fragmentSearchHunts;
    private HuntManager mHuntManager;
    private CameraManager mCameraManager;
    //private int curFrag;
    private TabLayout tabBar;
    private Set<Hunt> selected_items;
    private ImageView img_greenarrow;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overall_hunts_tabs, container, false);

        mHuntManager = ((ActivityBase)getActivity()).getData();
        tabBar = view.findViewById(R.id.tabLayout);
        img_greenarrow = view.findViewById(R.id.greenarrow);
        //setupFilter();
        //mViewPager = view.findViewById(R.id.view_container);
        //setupViewPager(mViewPager);
        //mViewPager.setOffscreenPageLimit(2);
        initTabLayout();
        //fragmentGoogleMaps = FragmentGoogleMapsHunts.newInstance(mHuntManager, mCameraManager);
        setFragment(FRAGMENT_GOOGLE_MAPS);
        SpinnerOverallHuntsFilter spinner = (SpinnerOverallHuntsFilter) view.findViewById(R.id.spinner);
        CheckableSpinnerOverallHuntsAdapter adapter = new CheckableSpinnerOverallHuntsAdapter(getContext(), (FragmentOverallHunt)getParentFragment());
        spinner.setAdapter(adapter);
        spinner.setSpinnerSearchEventsListener((SpinnerOverallHuntsFilter.OnSpinnerEventsListener)getParentFragment());
        return view;
    }
    /*public void setupFilter(){

        selected_items = mHuntManager.getSelectedHunts();
        String headerText = "Filter";

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

        }

        checkableSpinnerAdapter = new CheckableSpinnerAdapter(getContext(), headerText, mHuntManager, selected_items);
        spinner.setAdapter(checkableSpinnerAdapter);
        spinner.setSpinnerEventsListener((ActivityBase)getActivity());
    }*/

    public void updatetab(){
        try {
            int focusedTab = 0;
            if (getParentFragment() != null) {

                focusedTab = ((FragmentOverallHunt) getParentFragment()).getCurTab();
                System.out.println("parent tab not null: "+focusedTab);
            }
            TabLayout.Tab tab = tabBar.getTabAt(focusedTab);
            tab.select();
            setFragment(focusedTab);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void initTabLayout(){

        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.colorMagpieGreen);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                switch (tab.getPosition()) {
                    case FRAGMENT_GOOGLE_MAPS:
                        setFragment(FRAGMENT_GOOGLE_MAPS);
                        break;
                    case FRAGMENT_HUNT_LIST:
                        setFragment(FRAGMENT_HUNT_LIST);
                        break;
                    case FRAGMENT_HUNT_SEARCH:
                        setFragment(FRAGMENT_HUNT_SEARCH);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.colorMagpieDarkGray);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        int focusedTab = ((FragmentOverallHunt)getParentFragment()).getCurTab();
        TabLayout.Tab tab = tabBar.getTabAt(focusedTab);
        int tabIconColor = ContextCompat.getColor(getContext(), R.color.colorMagpieGreen);
        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tab.select();
        if(tabBar.getTabAt(FRAGMENT_GOOGLE_MAPS).isSelected()) {

            setFragment(FRAGMENT_GOOGLE_MAPS);
        }
        else if(tabBar.getTabAt(FRAGMENT_HUNT_LIST).isSelected()) {

            setFragment(FRAGMENT_HUNT_LIST);
        }
        else if(tabBar.getTabAt(FRAGMENT_HUNT_SEARCH).isSelected()) {

            setFragment(FRAGMENT_HUNT_SEARCH);
        }
    }

    public static FragmentOverallHuntTabs newInstance(HuntManager huntManager, CameraManager cameraManager) {
        FragmentOverallHuntTabs f = new FragmentOverallHuntTabs();
        Bundle args = new Bundle();
        args.putSerializable("cameramanager", cameraManager);
        //args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        mCameraManager = (CameraManager)args.getSerializable("cameramanager");
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");
    }

    public void setFragment(int i){
        //((FragmentList)getParentFragment()).setCurTab(i);
        ((FragmentOverallHunt)getParentFragment()).setCurTab(i);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(i == FRAGMENT_GOOGLE_MAPS) {
            ((ActivityBase)getActivity()).setBackButtonOnOff(false);
            if(fragmentGoogleMaps == null) {
                fragmentGoogleMaps = FragmentGoogleMapsHunts.newInstance(mHuntManager, mCameraManager);
            }
            //spinnerOnElseOff(true);
            ft.replace(R.id.currentfragment, fragmentGoogleMaps);
        }
        else if(i == FRAGMENT_HUNT_LIST){
            //if(fragmentGoogleMaps != null)
                //fragmentGoogleMaps.saveCameraPosition();
            if(fragmentHuntsList == null){
                fragmentHuntsList = FragmentHuntListTab.newInstance(mHuntManager);
            }
            //spinnerOnElseOff(true);
            ft.replace(R.id.currentfragment, fragmentHuntsList);
        }
        else if(i == FRAGMENT_HUNT_SEARCH){
            //if(fragmentGoogleMaps != null)
                //fragmentGoogleMaps.saveCameraPosition();
            if(fragmentSearchHunts == null){
                fragmentSearchHunts = FragmentSearchHunts.newInstance(mHuntManager);
            }
            //spinnerOnElseOff(false);
            ft.replace(R.id.currentfragment, fragmentSearchHunts);

        }

        System.out.println("COMMITING");
        ft.commit();
        System.out.println("COMMITING");
        try {
            getChildFragmentManager().executePendingTransactions();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("FINISH COMMITING");
        if(i == FRAGMENT_HUNT_LIST) {
            //fragmentHuntsList.updateList();
        }//TODO FINISH SET FRAGMENT
        else if(i == FRAGMENT_GOOGLE_MAPS){
            fragmentGoogleMaps.repositionCamera();
            //fragmentBirdsEyeViewContainer.reloadMap();
        }
    }
    /*private void spinnerOnElseOff(boolean b){
        if(b){
            spinner.setVisibility(View.VISIBLE);
            img_greenarrow.setVisibility(View.VISIBLE);
        }else{
            spinner.setVisibility(View.INVISIBLE);
            img_greenarrow.setVisibility(View.INVISIBLE);
        }
    }*/

    @Override
    public void onResume() {
        System.out.println(TAG+" onResume");
        super.onResume();
    }

    public void setParentFragment(int i){
        System.out.println("ACCESS PARENT FRAGMENT FROM BIRDSEYEVIEWCONTAINER");
        ((FragmentOverallHunt)getParentFragment()).setFragment(i);
        System.out.println("FINISH PARENT FRAGMENT FROM BIRDSEYEVIEWCONTAINER");
    }




    public void updateFocusHunts(){
        System.out.println("filter: updating focus hunts from hunttabs ");
        if(fragmentHuntsList != null)
            fragmentHuntsList.updateHuntsList();
        if(fragmentGoogleMaps != null){
            if(((FragmentOverallHunt)getParentFragment()).getCurTab() == FRAGMENT_GOOGLE_MAPS) {
                //fragmentGoogleMaps.setCustomInfoWindow();
                System.out.println("filter: updating focus hunts from hunttabs in final if");
                fragmentGoogleMaps.updateFocusHunts();
            }
        }
    }

    public void setCameraOnLandmark(Badge badge){
        System.out.println("badge to focus: "+badge.getLatitude());
        //fragmentGoogleMaps.setCameraOnLandmark(badge);
        setTab(FRAGMENT_GOOGLE_MAPS);

    }
    public void setTab(int i){
        int focusedTab = i;
        ((FragmentOverallHunt)getParentFragment()).setCurTab(i);
        TabLayout.Tab tab = tabBar.getTabAt(focusedTab);
        tab.select();
        setFragment(focusedTab);
    }
    /*public void updateSpinner(){
        if(fragmentLandmarkList != null){
            //fragmentLandmarkList.updateSpinner();
        }
        if(fragmentGoogleMaps != null){
            //fragmentGoogleMaps.updateSpinner();
        }
    }*/
    public void updatePermissionLocation(boolean b){
        //if(fragmentGoogleMaps!= null)
            //fragmentGoogleMaps.updatePermissionLocation(b);
    }
    public Set<Hunt> getSelected_items(){
        return selected_items;
    }

    public void onLandmarkSearchSelected(Badge badge){

        mHuntManager.setFocusHunt(badge.getHuntID());
        setTab(FRAGMENT_GOOGLE_MAPS);
        //fragmentGoogleMaps.setCameraOnLandmark(badge);

    }

    public void updateHuntsList(){
        if(fragmentHuntsList != null)
            fragmentHuntsList.updateHuntsList();
        if(fragmentSearchHunts!= null)
            fragmentSearchHunts.updateList();
    }
    public int getCurFilter(){
        FragmentOverallHunt f = ((FragmentOverallHunt)getParentFragment());
        if(f != null){
            return f.getCurFilter();
        }
        return FragmentOverallHunt.FILTER_DOWNLOADED;
    }

    @Override
    public void onCollectionSelected(int id, String name) {

        mHuntManager.setFocusHunt(id);
        ((ActivityBase)getActivity()).collectionClicked();
    }

    @Override
    public void onCollectionDeleted() {
        ((ActivityBase)getActivity()).onCollectionDeleted();
    }
    @Override
    public void onCollectionRestored() {
        ((ActivityBase)getActivity()).onCollectionRestored();
    }
}
