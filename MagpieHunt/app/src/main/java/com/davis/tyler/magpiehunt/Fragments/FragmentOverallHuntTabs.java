package com.davis.tyler.magpiehunt.Fragments;

import android.graphics.PorterDuff;
import android.location.Location;
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
    public static final int FRAGMENT_LIST = 1;
    public static final int FRAGMENT_HUNT_SEARCH = 2;

    //private ViewPager mViewPager;
    private FragmentLandmarkList fragmentLandmarkList;
    private FragmentGoogleMapsHunts fragmentGoogleMaps;
    private FragmentSearchHunts fragmentSearchHunts;
    private HuntManager mHuntManager;
    private CameraManager mCameraManager;

    //private int curFrag;
    private TabLayout tabBar;
    private Set<Hunt> selected_items;
    private ImageView img_greenarrow;

    private CheckableSpinnerOverallHuntsAdapter adapterHunts;
    private SpinnerOverallHuntsFilter spinnerHunts;
    private SpinnerHuntFilter spinnerLandmarks;
    private CheckableSpinnerAdapter checkableSpinnerAdapter;
    private boolean moveCameraOntoHunt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overall_hunts_tabs, container, false);

        mHuntManager = ((ActivityBase)getActivity()).getData();
        tabBar = view.findViewById(R.id.tabLayout);
        img_greenarrow = view.findViewById(R.id.greenarrow);
        moveCameraOntoHunt = false;
        initTabLayout();
        setFragment(((FragmentOverallHunt)getParentFragment()).getCurTab());
        spinnerHunts = (SpinnerOverallHuntsFilter) view.findViewById(R.id.spinnerhunts);
        adapterHunts = new CheckableSpinnerOverallHuntsAdapter(getContext(), (FragmentOverallHunt)getParentFragment());
        spinnerHunts.setAdapter(adapterHunts);
        spinnerHunts.setSpinnerSearchEventsListener((SpinnerOverallHuntsFilter.OnSpinnerEventsListener)getParentFragment());
        spinnerLandmarks = (SpinnerHuntFilter) view.findViewById(R.id.spinnerlandmarks);
        setupFilter();
        updateSpinnerVisibility();
        return view;
    }

    public void setupFilter(){

        selected_items = mHuntManager.getSelectedHunts();
        //Set the max height of the dropdown spinner:
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinnerLandmarks);

            // Set popupWindow height to 500px
            popupWindow.setHeight(600);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {

        }

        checkableSpinnerAdapter = new CheckableSpinnerAdapter(getContext(), mHuntManager, selected_items);
        spinnerLandmarks.setAdapter(checkableSpinnerAdapter);
        spinnerLandmarks.setSpinnerEventsListener((ActivityBase)getActivity());
    }


    public void updatetab(){
        try {
            int focusedTab = 0;
            if (getParentFragment() != null) {

                focusedTab = ((FragmentOverallHunt) getParentFragment()).getCurTab();
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
                    case FRAGMENT_LIST:
                        setFragment(FRAGMENT_LIST);
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
        else if(tabBar.getTabAt(FRAGMENT_LIST).isSelected()) {

            setFragment(FRAGMENT_LIST);
        }
        else if(tabBar.getTabAt(FRAGMENT_HUNT_SEARCH).isSelected()) {

            setFragment(FRAGMENT_HUNT_SEARCH);
        }
    }

    public static FragmentOverallHuntTabs newInstance(CameraManager cameraManager) {
        FragmentOverallHuntTabs f = new FragmentOverallHuntTabs();
        Bundle args = new Bundle();
        args.putSerializable("cameramanager", cameraManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        mCameraManager = (CameraManager)args.getSerializable("cameramanager");
    }

    public void setFragment(int i){
        //((FragmentList)getParentFragment()).setCurTab(i);
        ((FragmentOverallHunt)getParentFragment()).setCurTab(i);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(i == FRAGMENT_GOOGLE_MAPS) {
            ((ActivityBase)getActivity()).setBackButtonOnOff(false);
            if(fragmentGoogleMaps == null) {
                fragmentGoogleMaps = FragmentGoogleMapsHunts.newInstance(mCameraManager);
            }
            updateSpinnerVisibility();
            ft.replace(R.id.currentfragment, fragmentGoogleMaps);
        }
        else if(i == FRAGMENT_LIST){
            if(fragmentLandmarkList == null){
                fragmentLandmarkList = FragmentLandmarkList.newInstance();
            }
            updateSpinnerVisibility();
            ft.replace(R.id.currentfragment, fragmentLandmarkList);
        }
        else if(i == FRAGMENT_HUNT_SEARCH){
            if(fragmentSearchHunts == null){
                fragmentSearchHunts = FragmentSearchHunts.newInstance();
            }
            updateSpinnerVisibility();
            ft.replace(R.id.currentfragment, fragmentSearchHunts);

        }

        ft.commit();
        try {
            getChildFragmentManager().executePendingTransactions();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(i == FRAGMENT_LIST) {
            updateFocusHunts();
        }
        else if(i == FRAGMENT_GOOGLE_MAPS){
            updateFocusHunts();

            if(moveCameraOntoHunt) {
                fragmentGoogleMaps.moveCameraOntoHunt();
                moveCameraOntoHunt = false;
            }
        }
    }

    public void updateSpinnerVisibility(){
        int tab = ((FragmentOverallHunt)getParentFragment()).getCurTab();
        if(tab == FRAGMENT_GOOGLE_MAPS){
            if(getModeHuntsElseLandmarks()) {
                toggleHuntModeOnElseOff(true);
            }
            else{
                toggleHuntModeOnElseOff(false);
            }
        }
        else if(tab == FRAGMENT_HUNT_SEARCH){
            spinnerHunts.setVisibility(View.GONE);
            spinnerLandmarks.setVisibility(View.GONE);
            img_greenarrow.setVisibility(View.GONE);
        }
        else if(tab == FRAGMENT_LIST){
            toggleHuntModeOnElseOff(false);
        }
    }

    private void toggleHuntModeOnElseOff(boolean onElseOff){
        if(spinnerHunts == null || spinnerLandmarks == null)
            return;
        img_greenarrow.setVisibility(View.VISIBLE);
        if(onElseOff){
            spinnerHunts.setVisibility(View.VISIBLE);
            spinnerLandmarks.setVisibility(View.GONE);
        }else{
            spinnerHunts.setVisibility(View.GONE);
            spinnerLandmarks.setVisibility(View.VISIBLE);
        }
    }


    public void setParentFragment(int i){
        ((FragmentOverallHunt)getParentFragment()).setFragment(i);
    }




    public void updateFocusHunts(){
        if(fragmentLandmarkList != null) {
            fragmentLandmarkList.updateList();
            fragmentLandmarkList.updateFocusHunts();
        }
        if(fragmentGoogleMaps != null){
            if(((FragmentOverallHunt)getParentFragment()).getCurTab() == FRAGMENT_GOOGLE_MAPS) {
                fragmentGoogleMaps.setCustomInfoWindow();
                fragmentGoogleMaps.updateFocusHunts();
            }
        }
    }


    public void setTab(int i){
        int focusedTab = i;
        ((FragmentOverallHunt)getParentFragment()).setCurTab(i);
        TabLayout.Tab tab = tabBar.getTabAt(focusedTab);
        tab.select();
        setFragment(focusedTab);
    }
    public void updatePermissionLocation(boolean b){
        if(fragmentGoogleMaps!= null)
            fragmentGoogleMaps.updatePermissionLocation(b);
    }
    public Set<Hunt> getSelected_items(){
        return selected_items;
    }
    public void setCameraOnLandmark(Badge badge){
        fragmentGoogleMaps.setCameraOnLandmark(badge);
        setTab(FRAGMENT_GOOGLE_MAPS);

    }

    public void setCustomInfoWindow(){
        if(fragmentGoogleMaps != null)
            fragmentGoogleMaps.setCustomInfoWindow();
    }
    public void updateHuntsList(){
        if(fragmentLandmarkList != null)
            fragmentLandmarkList.updateList();
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

    public boolean getSearchHuntsElseLandmarks(){return ((FragmentOverallHunt)getParentFragment()).getSearchHuntsElseLandmarks();}
    public void setSearchHuntsElseLandmarks(boolean b){((FragmentOverallHunt)getParentFragment()).setSearchHuntsElseLandmarks(b);}
    public boolean getModeHuntsElseLandmarks(){return ((FragmentOverallHunt)getParentFragment()).getModeHuntsElseLandmarks();}
    public void setModeHuntsElseLandmarks(boolean b){
        ((FragmentOverallHunt)getParentFragment()).setModeHuntsElseLandmarks(b);

    }
    public void moveCameraToHuntSpinnerClose(){
        if(((FragmentOverallHunt)getParentFragment()).getCurTab() == FRAGMENT_GOOGLE_MAPS){
            System.out.println("movetohunt: from if method");
            fragmentGoogleMaps.moveCameraOntoHunt();
        }else{
            fragmentGoogleMaps.setMoveCameraOnHunt();
        }
    }
    public void onLandmarkSearchSelected(Badge badge){
        mHuntManager.setFocusHunt(badge.getHuntID());
        mHuntManager.setFocusBadge(badge.getID());
        setTab(FRAGMENT_GOOGLE_MAPS);
        fragmentGoogleMaps.updateFocusHunts();
        fragmentGoogleMaps.setCameraOnLandmark(badge);

    }
    public void moveCameraToHunt(){
        if(!getModeHuntsElseLandmarks()) {
            moveCameraOntoHunt = true;
            fragmentGoogleMaps.setMoveCameraOnHunt();
        }

    }
    public void notifyLocationChanged(){
        if(((FragmentOverallHunt) getParentFragment()).getCurTab() == FRAGMENT_LIST)
            fragmentLandmarkList.updateList();
    }
    public void updateSpinner(){
        checkableSpinnerAdapter.updateSpinnerItems();
        checkableSpinnerAdapter.notifyDataSetChanged();
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
    public void setHuntCompleteNotificationMaps(Hunt h){
        fragmentGoogleMaps.setHuntCompleteNotificationMaps(h);
    }
    public void setHuntCompleteNotificationList(Hunt h){
        fragmentLandmarkList.setHuntCompleteNotificationList(h);
    }


    public void updateLocation(Location location){
        fragmentGoogleMaps.updateLocation(location);
    }
}
