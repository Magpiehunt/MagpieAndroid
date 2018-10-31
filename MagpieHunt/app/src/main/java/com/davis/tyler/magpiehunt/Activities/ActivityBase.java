package com.davis.tyler.magpiehunt.Activities;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.CMS.DownloadImage;
import com.davis.tyler.magpiehunt.FileSystemManager;
import com.davis.tyler.magpiehunt.Fragments.FragmentBirdsEyeViewContainer;
import com.davis.tyler.magpiehunt.Fragments.FragmentDataHunts;
import com.davis.tyler.magpiehunt.Fragments.FragmentOverallHunt;
import com.davis.tyler.magpiehunt.Fragments.FragmentOverallHuntTabs;
import com.davis.tyler.magpiehunt.Hunts.Award;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Location.CameraManager;
import com.davis.tyler.magpiehunt.Fragments.FragmentLandmarkList;
import com.davis.tyler.magpiehunt.Fragments.FragmentHome;
import com.davis.tyler.magpiehunt.Fragments.FragmentList;
import com.davis.tyler.magpiehunt.Fragments.FragmentLandmarkInfo;
import com.davis.tyler.magpiehunt.Fragments.FragmentPrizes;
import com.davis.tyler.magpiehunt.Fragments.FragmentSearch;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.Location.LocationTracker;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Adapters.SectionsStatePagerAdapter;
import com.davis.tyler.magpiehunt.Spinners.SpinnerHuntFilter;
import com.davis.tyler.magpiehunt.Spinners.SpinnerSearchFilter;
import com.davis.tyler.magpiehunt.Views.ViewPagerToggleSwipe;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.LinkedList;

public class ActivityBase extends AppCompatActivity implements
        FragmentLandmarkList.OnLandmarkSelectedListener, FragmentLandmarkInfo.onClickListener,
        SpinnerHuntFilter.OnSpinnerEventsListener, SpinnerSearchFilter.OnSpinnerSearchEventsListener{
    public static final String TAG = "Activity_Base";

    //these must be in order they appear on navigation bar
    public static final int FRAGMENT_MAP = 0;
    public static final int FRAGMENT_OVERALL_HUNTS = 0;
    public static final int FRAGMENT_HOME = 1;
    public static final int FRAGMENT_LIST = 2;
    public static final int FRAGMENT_PRIZES = 3;
    private BottomNavigationView mNavigationView;
    private ViewPagerToggleSwipe mViewPager;
    //private FragmentMap fragmentMap;
    //private FragmentSearch fragmentSearch;
    private FragmentOverallHunt fragmentOverallHunt;
    private FragmentHome fragmentHome;
    private FragmentList fragmentList;
    private FragmentPrizes fragmentPrizes;
    private FragmentDataHunts fragmentData;
    private HuntManager mHuntManager;
    private LocationTracker mLocationTracker;
    private CameraManager mCameraManager;
    private MenuItem menu_settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_base);
        if(mHuntManager == null)
            mHuntManager = new HuntManager(this);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentData = (FragmentDataHunts) fragmentManager.findFragmentByTag("data");

        // create the fragment and data the first time
        if (fragmentData == null) {
            // add the fragment
            fragmentData = new FragmentDataHunts();
            fragmentManager.beginTransaction().add(fragmentData, "data").commit();
            // load the data from the web
            fragmentData.setData(mHuntManager);
        }
        mCameraManager = new CameraManager();
        FileSystemManager fm = new FileSystemManager();
        try {
            //fm.addTestHunt(this);
            mHuntManager.addAllHunts(fm.getHuntsFromFile(this));
            mHuntManager.setAllHuntsFocused();
        }catch(IOException e)
        {
            System.out.println("check: IOException"+e.getMessage());
        }
        catch(ParseException e)
        {
            System.out.println("check: ParseException"+e.getMessage());
        }
        /*
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.example_badge);
        Bitmap test = null;
        try {
            fm.saveImageToInternalStorage(this, bitmap, "test1.png");
            test = fm.loadImageFromStorage(this, "test1.png");
            System.out.println("Hopefully this works!: "+test);
            fm.printImagesDirectory(this);
        }
        catch(Exception e)
        {
            System.out.println("error: "+e.getMessage()+e.getStackTrace());
        }*/
        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.action_bar_gradient, null));

        mViewPager = findViewById(R.id.currentfragment);
        mNavigationView = findViewById(R.id.menu);
        mNavigationView.setSelectedItemId(R.id.menu_home);
        mLocationTracker = new LocationTracker(this, this, mHuntManager);

        setupViewPager(mViewPager);
        //TODO this makes everything heckin fast!!! comment this out if it proves to be an issue later
        mViewPager.setOffscreenPageLimit(4);
        getSupportActionBar().setTitle("My Collections");

        setBackButtonOnOff(false);
        setupFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        menu_settings = menu.findItem(R.id.action_settings);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "item id: "+item.getItemId());

        //TODO: figure out why its this random number 16908332

        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                switchToSettingsPage();
                return true;

            case 16908332:
                onBackPressed();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void menuSettingsVisibility(boolean b){
        if(menu_settings != null)
        menu_settings.setVisible(b);
    }

    private void setupFragments() {
        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        setBackButtonOnOff(false);
                        switch (item.getItemId()) {

                            /*case R.id.menu_map:
                                mViewPager.setCurrentItem(FRAGMENT_MAP);
                                fragmentMap.setFragment(FragmentMap.FRAGMENT_GOOGLE_MAPS);
                                break;
                                */
                            case R.id.menu_list:

                                //fragmentList.setFragment(FragmentList.FRAGMENT_LANDMARK_LIST);

                                mViewPager.setCurrentItem(FRAGMENT_LIST);
                                updateListFragment();
                                break;
                            case R.id.menu_home:
                                mViewPager.setCurrentItem(FRAGMENT_HOME);


                                break;
                            case R.id.menu_map:
                                mViewPager.setCurrentItem(FRAGMENT_OVERALL_HUNTS);
                                updateHuntsList();
                                break;
                            case R.id.menu_prizes:
                                if(fragmentPrizes != null){
                                    fragmentPrizes.updateList();
                                }
                                mViewPager.setCurrentItem(FRAGMENT_PRIZES);
                                //fragment = PrizesFragment.newInstance();
                                break;
                        }
                        return true;
                    }
                });

    }


    @Override
    public void onBackPressed() {
        Log.e(TAG, "onback press");
        // If the fragment exists and has the current focus
        /*if ( fragmentMap != null && mViewPager.getCurrentItem() == FRAGMENT_MAP){
            // Get the fragment fragment manager - and pop the backstack
            fragmentMap.onBackPressed();
        }
        else */if(fragmentList != null && mViewPager.getCurrentItem() == FRAGMENT_LIST){
            fragmentList.onBackPressed();
        }
        /*else if(fragmentSearch != null && mViewPager.getCurrentItem() == FRAGMENT_OVERALL_HUNTS){
            fragmentSearch.onBackPressed();
        }*/
        else if(fragmentPrizes != null && mViewPager.getCurrentItem() == FRAGMENT_PRIZES){
            fragmentPrizes.onBackPressed();
        }
        else if(fragmentHome != null && mViewPager.getCurrentItem() == FRAGMENT_HOME){
            fragmentHome.onBackPressed();
        }
        else{
            // Let super handle the back press
            super.onBackPressed();
        }
    }

    public void changePage(int page){
        setBackButtonOnOff(false);
        fragmentOverallHunt.hideSoftKeyboard();
        //if a page is changed due to left/right swipes, update navigation menu, and actionbar title
        switch(page){
            /*case FRAGMENT_MAP:

                mNavigationView.setSelectedItemId(R.id.menu_map);

                if(fragmentMap != null){
                    updateMap();
                    fragmentMap.updateActionBar();
                    fragmentMap.setFragment(FragmentMap.FRAGMENT_GOOGLE_MAPS);
                }


                break;*/
            case FRAGMENT_LIST:
                mNavigationView.setSelectedItemId(R.id.menu_list);

                if(fragmentList != null){
                    updateListFragment();
                    fragmentList.updateActionBar();
                    //fragmentList.setFragment(FragmentList.FRAGMENT_LANDMARK_LIST);
                    fragmentList.updateFocusHunts();
                }

                break;
            case FRAGMENT_HOME:

                mNavigationView.setSelectedItemId(R.id.menu_home);
                fragmentHome.updateActionBar();
                break;
            case FRAGMENT_OVERALL_HUNTS:
                mNavigationView.setSelectedItemId(R.id.menu_map);
                fragmentOverallHunt.updateActionBar();
                updateHuntsList();
                break;
            case FRAGMENT_PRIZES:
                if(fragmentPrizes != null){
                    fragmentPrizes.updateList();
                    fragmentPrizes.updateActionBar();
                }
                mNavigationView.setSelectedItemId(R.id.menu_prizes);
                break;

        }
    }





    public void switchToSettingsPage(){
        changePage(FRAGMENT_HOME);
        fragmentHome.setFragment(FragmentHome.FRAGMENT_SETTINGS);
    }
    public void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        //YOU NEED TO HAVE THIS IN THE SAME ORDER AS NAV MENU
        //fragmentMap = FragmentMap.newInstance(mHuntManager, mCameraManager);
        //fragmentSearch = FragmentSearch.newInstance(mHuntManager);
        fragmentOverallHunt = FragmentOverallHunt.newInstance(mHuntManager);
        fragmentList = FragmentList.newInstance(mHuntManager, mCameraManager);
        fragmentPrizes = FragmentPrizes.newInstance(mHuntManager);
        fragmentHome = FragmentHome.newInstance(mHuntManager);

        //adapter.addFragment(fragmentMap, "MapFragment");
        //adapter.addFragment(fragmentSearch, "SearchFragment");
        adapter.addFragment(fragmentOverallHunt, "OverallHuntFragment");
        adapter.addFragment(fragmentHome, "HomeFragment");
        adapter.addFragment(fragmentList, "ListFragment");

        adapter.addFragment(fragmentPrizes, "PrizesFragment");
        viewPager.setAdapter(adapter);

        mViewPager.setCurrentItem(FRAGMENT_HOME);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fragmentOverallHunt.hideSoftKeyboard();
                //note: for children fragments, change support action bar title where fragmentTransaction is called;
                switch(position){
                    /*case FRAGMENT_MAP:

                        mNavigationView.setSelectedItemId(R.id.menu_map);
                        if(fragmentMap != null){
                            updateMap();
                            fragmentMap.updateActionBar();
                        }
                        break;*/
                    case FRAGMENT_LIST:

                        mNavigationView.setSelectedItemId(R.id.menu_list);
                        if(fragmentList != null){
                            updateListFragment();
                            updateMapBirdsEye();
                            fragmentList.updateActionBar();
                        }
                        break;
                    case FRAGMENT_HOME:

                        mNavigationView.setSelectedItemId(R.id.menu_home);
                        fragmentHome.updateActionBar();
                        break;
                    case FRAGMENT_OVERALL_HUNTS:
                        mNavigationView.setSelectedItemId(R.id.menu_map);
                        fragmentOverallHunt.updateActionBar();
                        updateHuntsList();
                        break;
                    case FRAGMENT_PRIZES:
                        if(fragmentPrizes != null){
                            fragmentPrizes.updateList();
                            fragmentPrizes.updateActionBar();
                        }
                        mNavigationView.setSelectedItemId(R.id.menu_prizes);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setHuntTitle(){
        if(mHuntManager.getSelectedHuntsSize()== 1){
            getSupportActionBar().setTitle(mHuntManager.getSingleSelectedHunt().getName());
        }
        else {
            getSupportActionBar().setTitle("Birds Eye View");
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onsave");
        super.onSaveInstanceState(outState);

    }


    @Override
    public void onLandmarkSelected(Badge b) {
        /*mHuntManager.setFocusBadge(b.getID());
        fragmentList.setFragment(FragmentList.FRAGMENT_LANDMARK_INFO);*/
        fragmentList.onLandmarkSelected(b);
    }

    @Override
    public void onMapClicked(Badge badge) {
        /*mViewPager.setCurrentItem(FRAGMENT_MAP);
        mNavigationView.setSelectedItemId(FRAGMENT_MAP);
        fragmentMap.setCameraOnLandmark(badge);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onpause");
        addHuntsToFileSystem();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onstop");
        addHuntsToFileSystem();
    }

    @Override
    public void onCollectMoveCloser(Badge badge) {
        //if user isnt close enough, switch to map fragment, and set camera to target landmark
        mCameraManager.setMoveCloser(true);
        mViewPager.setCurrentItem(FRAGMENT_MAP);
        mNavigationView.setSelectedItemId(FRAGMENT_MAP);
        //fragmentMap.setCameraOnLandmark(badge);
    }

    public boolean isCloseEnough(Badge badge){
        //check if user location is within valid distance to the landmark
        Location loc = new Location("");
        loc.setLatitude(badge.getLatitude());
        loc.setLongitude(badge.getLongitude());
        return mLocationTracker.isValidDistance(loc);
    }

    public void notifyLocationChanged(){
        if(fragmentList != null)
            fragmentList.notifyLocationChanged();

    }


    @Override
    public void onSpinnerOpened() {

    }

    @Override
    public void onSpinnerClosed() {
        //when spinner closes, update focus hunts, markers shown, and landmarks listed
        /*if(fragmentMap != null)
            fragmentMap.updateFocusHunts();*/
        if(fragmentList != null)
            fragmentList.updateFocusHunts();
        fragmentList.moveCameraToHuntSpinnerClose();
        setHuntTitle();

    }

    public void updateList(){
        //fragmentMap.updateFocusHunts();
        fragmentList.updateFocusHunts();
    }
    public void updateSpinner(){
        //fragmentMap.updateSpinner();
        fragmentList.updateSpinner();
    }

    public void updateListFragment(){
        System.out.println("updating list fragment");
        fragmentList.updateFocusHunts();
        fragmentList.updateSpinner();
    }

    public void updateHuntsList(){
        if(fragmentHome != null){
            fragmentHome.updateHuntsList();
        }
    }
    public void updateMap(){
        /*fragmentMap.updateFocusHunts();
        fragmentMap.updateSpinner();*/
    }
    public void updateMapBirdsEye(){
        fragmentList.updateFocusHunts();
        //fragmentList.updateSpinner();
    }
    @Override
    public void onSpinnerSearchOpened() {

    }

    @Override
    public void onSpinnerSearchClosed() {

    }

    public void onAddHuntEvent(Hunt h){
        Hunt hunt = mHuntManager.getHuntByID(h.getID());
        if(hunt != null){
            h = hunt;
        }else{
            mHuntManager.addHunt(h);
        }
        h.setmIsDeleted(false);
        h.setIsFocused(true);
        h.setmIsDownloaded(true);


        mHuntManager.getSelectedHunts().add(h);
        mHuntManager.removeUndownloadedSelectedHunts();
        addHuntsToFileSystem();
        saveImagesToFileSystem(h);
        if(mLocationTracker.hasLocPermission()) {
            mLocationTracker.updateDistances();

        }
        updateSpinner();
        updateList();
        fragmentHome.updateHuntsList();
        Toast.makeText(this, "Successfully added hunt", Toast.LENGTH_SHORT).show();
    }



    private void saveImagesToFileSystem(Hunt h){
        LinkedList<Badge> badges = h.getAllBadges();

        //FileSystemManager fm = new FileSystemManager();
        for(Badge b: badges){
            String src = "http://206.189.204.95/badge/icon/"+b.getIcon();
            try {
                URL url = new URL(src);
                new DownloadImage(this, b.getBadgeImageFileName()).execute(url );
            }catch (Exception e){
                e.printStackTrace();
            }
            src = "http://206.189.204.95/landmark/image/"+b.getLandmarkImage();
            try {
                URL url = new URL(src);
                new DownloadImage(this, b.getLandmarkImageFileName()).execute(url );
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        Award award = h.getAward();
        String src = "http://206.189.204.95/superbadge/image/"+award.getSuperBadgeIcon();
        try {
            URL url = new URL(src);
            new DownloadImage(this, award.getSuperBadgeImageFileName()).execute(url );
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void swipedToPrize(){
        changePage(FRAGMENT_PRIZES);
        //mHuntManager.setFocusAward(mHuntManager.getSingleSelectedHunt().getID());
        fragmentPrizes.setFragment(FragmentPrizes.FRAGMENT_PRIZES_INFO);

    }

    public void setNavigationViewVisibility(int viewVisibility){
        mNavigationView.setVisibility(viewVisibility);
    }

    private void addHuntsToFileSystem(){
        FileSystemManager fm = new FileSystemManager();
        try {
            fm.addHuntList(this, mHuntManager.getAllDownloadedHunts());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setBackButtonOnOff(boolean b){
        getSupportActionBar().setDisplayHomeAsUpEnabled(b);

    }
    public HuntManager getData(){
        return mHuntManager;
    }

    public LocationTracker getmLocationTracker(){return mLocationTracker;}
    public void setPagerSwipe(boolean b){mViewPager.setPagingEnabled(b);}
    public void updatePermissionLocation(boolean b){
        mLocationTracker.updatePermissionLocation(b);
        fragmentList.updatePermissionLocation(b);
        fragmentOverallHunt.updatePermissionLocation(b);
    }
    public void updateMapForUndownloadedHunt(){
        mLocationTracker.updateDistances();
        fragmentList.updateFocusHunts();
        changePage(FRAGMENT_LIST);
        fragmentList.setFragment(FragmentList.FRAGMENT_GOOGLE_MAPS);
        fragmentList.updateSpinner();
        fragmentList.moveCameraToHunt();
        /*fragmentMap.updateFocusHunts();
        changePage(FRAGMENT_MAP);
        fragmentMap.updateSpinner();*/
    }

    public void collectionClicked(){

        changePage(ActivityBase.FRAGMENT_LIST);
        getmLocationTracker().updateDistances();

        fragmentList.changeTab(FragmentBirdsEyeViewContainer.FRAGMENT_LANDMARK_LIST);
        fragmentList.setFragment(FragmentList.FRAGMENT_BIRDS_EYE);
        updateList();
        updateMap();
        fragmentList.moveCameraToHunt();


    }
    public void collectionClickedMapView(){

        changePage(ActivityBase.FRAGMENT_LIST);
        getmLocationTracker().updateDistances();

        fragmentList.changeTab(FragmentBirdsEyeViewContainer.FRAGMENT_GOOGLE_MAPS);
        fragmentList.setFragment(FragmentList.FRAGMENT_BIRDS_EYE);
        updateList();
        updateMap();
        fragmentList.moveCameraToHunt();


    }
    public void onCollectionDeleted(){
        mHuntManager.updateFocusHuntDeleted();
        updateSpinner();
        updateList();
        fragmentHome.updateHuntsList();
        fragmentOverallHunt.updateHuntsList();
    }
    public void onCollectionRestored(){
        updateSpinner();
        updateList();
        fragmentHome.updateHuntsList();
    }
    public void onAddCollectionClicked(){
        changePage(FRAGMENT_OVERALL_HUNTS);
        fragmentOverallHunt.changeTab(FragmentOverallHuntTabs.FRAGMENT_HUNT_SEARCH);
    }
}
