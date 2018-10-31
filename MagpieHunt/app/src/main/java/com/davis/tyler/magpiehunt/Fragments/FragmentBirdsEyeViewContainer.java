package com.davis.tyler.magpiehunt.Fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Adapters.CheckableSpinnerAdapter;
import com.davis.tyler.magpiehunt.Adapters.SectionsStatePagerAdapter;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.Location.CameraManager;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Spinners.SpinnerHuntFilter;

import java.lang.reflect.Field;
import java.util.Set;

public class FragmentBirdsEyeViewContainer extends Fragment {
    private static final String TAG = "BirdsEyeViewCont";

    public static final int FRAGMENT_GOOGLE_MAPS = 0;
    public static final int FRAGMENT_LANDMARK_LIST = 1;
    public static final int FRAGMENT_LANDMARK_SEARCH = 2;

    //private ViewPager mViewPager;
    private FragmentLandmarkList fragmentLandmarkList;
    private FragmentGoogleMaps fragmentGoogleMaps;
    private FragmentSearchLandmarks fragmentSearchLandmarks;
    private HuntManager mHuntManager;
    private CameraManager mCameraManager;
    //private int curFrag;
    private TabLayout tabBar;
    private Set<Hunt> selected_items;
    private SpinnerHuntFilter spinner;
    private CheckableSpinnerAdapter checkableSpinnerAdapter;
    private ImageView img_greenarrow;
    private boolean moveCameraOntoHunt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_birds_eye_view_container, container, false);

        mHuntManager = ((ActivityBase)getActivity()).getData();

        tabBar = view.findViewById(R.id.tabLayout);
        spinner = (SpinnerHuntFilter) view.findViewById(R.id.spinner);
        img_greenarrow = view.findViewById(R.id.greenarrow);
        setupFilter();
        //mViewPager = view.findViewById(R.id.view_container);
        //setupViewPager(mViewPager);
        //mViewPager.setOffscreenPageLimit(2);
        initTabLayout();
        return view;
    }
    public void setupFilter(){

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
    }

    public void updatetab(){
       try {
           int focusedTab = 0;
           if (getParentFragment() != null) {

               focusedTab = ((FragmentList) getParentFragment()).getCurTab();
               System.out.println("parent tab not null: "+focusedTab);
           }
           TabLayout.Tab tab = tabBar.getTabAt(focusedTab);
           tab.select();
           setFragment(focusedTab);
       }catch(Exception e){
           e.printStackTrace();
       }
    }

    public void notifyLocationChanged(){
        if(((FragmentList) getParentFragment()).getCurTab() == FRAGMENT_LANDMARK_LIST)
            fragmentLandmarkList.updateList();
    }

    public void initTabLayout(){

        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.colorMagpieGreen);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                System.out.println("Tab switching to: "+tab.getPosition());
                switch (tab.getPosition()) {
                    case FRAGMENT_GOOGLE_MAPS:
                        setFragment(FRAGMENT_GOOGLE_MAPS);
                        //fragment = LandmarkContainerFragment.newInstance();//GoogleMapFragment.newInstance();
                        break;
                    case FRAGMENT_LANDMARK_LIST:
                        setFragment(FRAGMENT_LANDMARK_LIST);
                        break;
                    case FRAGMENT_LANDMARK_SEARCH:
                        setFragment(FRAGMENT_LANDMARK_SEARCH);
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
        int focusedTab = ((FragmentList)getParentFragment()).getCurTab();
        TabLayout.Tab tab = tabBar.getTabAt(focusedTab);
        int tabIconColor = ContextCompat.getColor(getContext(), R.color.colorMagpieGreen);
        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tab.select();
        if(tabBar.getTabAt(FRAGMENT_GOOGLE_MAPS).isSelected()) {

            setFragment(FRAGMENT_GOOGLE_MAPS);
        }
        else if(tabBar.getTabAt(FRAGMENT_LANDMARK_LIST).isSelected()) {

            setFragment(FRAGMENT_LANDMARK_LIST);
        }
        else if(tabBar.getTabAt(FRAGMENT_LANDMARK_LIST).isSelected()) {

            setFragment(FRAGMENT_LANDMARK_LIST);
        }
    }

    public static FragmentBirdsEyeViewContainer newInstance(HuntManager huntManager, CameraManager cameraManager) {
        FragmentBirdsEyeViewContainer f = new FragmentBirdsEyeViewContainer();
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
       ((FragmentList)getParentFragment()).setCurTab(i);
       if (!isAdded()) return;
       FragmentTransaction ft = getChildFragmentManager().beginTransaction();
       if(i == FRAGMENT_GOOGLE_MAPS) {
           ((ActivityBase)getActivity()).setBackButtonOnOff(false);
           if(fragmentGoogleMaps == null) {
               fragmentGoogleMaps = FragmentGoogleMaps.newInstance(mHuntManager, mCameraManager);
           }
           spinnerOnElseOff(true);
           ft.replace(R.id.currentfragment, fragmentGoogleMaps);
       }
       else if(i == FRAGMENT_LANDMARK_LIST){
           if(fragmentGoogleMaps != null)
               fragmentGoogleMaps.saveCameraPosition();
           if(fragmentLandmarkList == null){
               fragmentLandmarkList = FragmentLandmarkList.newInstance(mHuntManager);
           }
           spinnerOnElseOff(true);
           ft.replace(R.id.currentfragment, fragmentLandmarkList);
       }
       else if(i == FRAGMENT_LANDMARK_SEARCH){
           if(fragmentGoogleMaps != null)
               fragmentGoogleMaps.saveCameraPosition();
           if(fragmentSearchLandmarks == null){
               fragmentSearchLandmarks = FragmentSearchLandmarks.newInstance();
           }
           spinnerOnElseOff(false);
           ft.replace(R.id.currentfragment, fragmentSearchLandmarks);

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
       if(i == FRAGMENT_LANDMARK_LIST) {
           fragmentLandmarkList.updateFocusHunts();
       }//TODO FINISH SET FRAGMENT
       else if(i == FRAGMENT_GOOGLE_MAPS){
           //fragmentGoogleMaps.repositionCamera();
           updateMapBirdsEye();
           fragmentGoogleMaps.setCustomInfoWindow();
           if(moveCameraOntoHunt) {
               System.out.println("movetohunt: from setfragment");
               fragmentGoogleMaps.moveCameraOntoHunt();
               moveCameraOntoHunt = false;
           }
           //fragmentBirdsEyeViewContainer.reloadMap();
       }
   }
   private void spinnerOnElseOff(boolean b){
        if(b){
            spinner.setVisibility(View.VISIBLE);
            img_greenarrow.setVisibility(View.VISIBLE);
        }else{
            spinner.setVisibility(View.INVISIBLE);
            img_greenarrow.setVisibility(View.INVISIBLE);
        }
   }

    @Override
    public void onResume() {
        System.out.println(TAG+" onResume");
        super.onResume();
    }

    public void setParentFragment(int i){
        System.out.println("ACCESS PARENT FRAGMENT FROM BIRDSEYEVIEWCONTAINER");
        ((FragmentList)getParentFragment()).setFragment(i);
        System.out.println("FINISH PARENT FRAGMENT FROM BIRDSEYEVIEWCONTAINER");
    }


    public void updateMapBirdsEye(){
       if(fragmentLandmarkList!= null) {
           fragmentLandmarkList.updateFocusHunts();
           //fragmentLandmarkList.updateSpinner();
       }
    }

    public void updateFocusHunts(){
        if(fragmentLandmarkList != null)
            fragmentLandmarkList.updateFocusHunts();
        if(fragmentGoogleMaps != null){

            if(((FragmentList)getParentFragment()).getCurTab() == FRAGMENT_GOOGLE_MAPS) {
                fragmentGoogleMaps.setCustomInfoWindow();
                if(moveCameraOntoHunt)
                    fragmentGoogleMaps.moveCameraOntoHunt();
                fragmentGoogleMaps.updateFocusHunts();
            }
        }
    }

    public void setCameraOnLandmark(Badge badge){
       System.out.println("badge to focus: "+badge.getLatitude());
        fragmentGoogleMaps.setCameraOnLandmark(badge);
        setTab(FRAGMENT_GOOGLE_MAPS);

    }
    public void setTab(int i){
        int focusedTab = i;
        if (getParentFragment() != null) {

            ((FragmentList) getParentFragment()).setCurTab(i);
        }
        TabLayout.Tab tab = tabBar.getTabAt(focusedTab);
        tab.select();
        setFragment(focusedTab);
    }
    public void updateSpinner(){
        checkableSpinnerAdapter.updateSpinnerItems();
        checkableSpinnerAdapter.notifyDataSetChanged();
    }
    public void updatePermissionLocation(boolean b){
        if(fragmentGoogleMaps!= null)
            fragmentGoogleMaps.updatePermissionLocation(b);
    }
    public Set<Hunt> getSelected_items(){
       return selected_items;
    }

    public void moveCameraToHunt(){
        /*if(((FragmentList)getParentFragment()).getCurTab() == FRAGMENT_GOOGLE_MAPS){
            System.out.println("movetohunt: from if method");
            fragmentGoogleMaps.moveCameraOntoHunt();
        }
        else {
            System.out.println("movetohunt: setting boolean true");*/
            moveCameraOntoHunt = true;
            fragmentGoogleMaps.setMoveCameraOnHunt();

    }

    public void moveCameraToHuntSpinnerClose(){
        if(((FragmentList)getParentFragment()).getCurTab() == FRAGMENT_GOOGLE_MAPS){
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
    public void setHuntCompleteNotificationMaps(Hunt h){
        fragmentGoogleMaps.setHuntCompleteNotificationMaps(h);
    }
    public void setHuntCompleteNotificationList(Hunt h){
        fragmentLandmarkList.setHuntCompleteNotificationList(h);
    }
}
