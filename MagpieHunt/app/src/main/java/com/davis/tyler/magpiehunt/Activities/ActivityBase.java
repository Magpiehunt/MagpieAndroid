package com.davis.tyler.magpiehunt.Activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.davis.tyler.magpiehunt.CameraManager;
import com.davis.tyler.magpiehunt.Fragments.FragmentLandmarkList;
import com.davis.tyler.magpiehunt.Fragments.FragmentAccount;
import com.davis.tyler.magpiehunt.Fragments.FragmentHome;
import com.davis.tyler.magpiehunt.Fragments.FragmentLandmarkInfo;
import com.davis.tyler.magpiehunt.Fragments.FragmentMap;
import com.davis.tyler.magpiehunt.Fragments.FragmentPrizes;
import com.davis.tyler.magpiehunt.Fragments.FragmentSearch;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.LocationTracker;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Adapters.SectionsStatePagerAdapter;
import com.davis.tyler.magpiehunt.SpinnerHuntFilter;

public class ActivityBase extends AppCompatActivity implements
        FragmentLandmarkList.OnLandmarkSelectedListener, FragmentLandmarkInfo.onClickListener,
        SpinnerHuntFilter.OnSpinnerEventsListener{
    public static final String TAG = "Activity_Base";
    public static final int FRAGMENT_MAP = 0;
    public static final int FRAGMENT_ACCOUNT = 1;
    public static final int FRAGMENT_HOME = 2;
    public static final int FRAGMENT_SEARCH = 3;
    public static final int FRAGMENT_PRIZES = 4;
    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private FragmentMap fragmentMap;
    private FragmentSearch fragmentSearch;
    private FragmentHome fragmentHome;
    private HuntManager mHuntManager;
    private LocationTracker mLocationTracker;
    private CameraManager mCameraManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_base);
        if(savedInstanceState != null){
            mHuntManager = (HuntManager)savedInstanceState.getSerializable("huntmanager");
        }
        else {
            mHuntManager = new HuntManager(this);
        }
        mCameraManager = new CameraManager();
        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.action_bar_gradient, null));

        mViewPager = findViewById(R.id.currentfragment);
        mNavigationView = findViewById(R.id.menu);
        mNavigationView.setSelectedItemId(R.id.menu_home);
        mLocationTracker = new LocationTracker(this, this, mHuntManager);

        setupViewPager(mViewPager);
        //TODO this makes everything heckin fast!!! comment this out if it proves to be an issue later
        mViewPager.setOffscreenPageLimit(5);
        if(mHuntManager.getSelectedHuntsSize()== 1){
            getSupportActionBar().setTitle(mHuntManager.getFocusHunt().getName());
        }
        else {
            getSupportActionBar().setTitle("Badges Near Me");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupFragments();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "item id: "+item.getItemId());

        //TODO: figure out why its this random number 16908332
        if(item.getItemId() == 16908332) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupFragments() {
        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_map:
                                mViewPager.setCurrentItem(FRAGMENT_MAP);
                                fragmentMap.setFragment(FragmentMap.FRAGMENT_GOOGLE_MAPS);
                                break;
                            case R.id.menu_account:
                                mViewPager.setCurrentItem(FRAGMENT_ACCOUNT);
                                break;
                            case R.id.menu_home:
                                mViewPager.setCurrentItem(FRAGMENT_HOME);
                                fragmentHome.setFragment(FragmentHome.FRAGMENT_LANDMARK_LIST);
                                break;
                            case R.id.menu_search:
                                mViewPager.setCurrentItem(FRAGMENT_SEARCH);
                                break;
                            case R.id.menu_prizes:
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
        if ( fragmentMap != null && mViewPager.getCurrentItem() == FRAGMENT_MAP){
            // Get the fragment fragment manager - and pop the backstack
            fragmentMap.onBackPressed();
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
        //if a page is changed due to left/right swipes, update navigation menu, and actionbar title
        switch(page){
            case FRAGMENT_MAP:

                mNavigationView.setSelectedItemId(R.id.menu_map);
                if(mHuntManager.getSelectedHuntsSize()== 1){
                    getSupportActionBar().setTitle(mHuntManager.getFocusHunt().getName());
                }
                else {
                    getSupportActionBar().setTitle("Badges Near Me");
                }
                fragmentMap.setFragment(FragmentMap.FRAGMENT_GOOGLE_MAPS);

                break;
            case FRAGMENT_HOME:
                mNavigationView.setSelectedItemId(R.id.menu_home);
                if(mHuntManager.getSelectedHuntsSize()== 1){
                    getSupportActionBar().setTitle(mHuntManager.getFocusHunt().getName());
                }
                else {
                    getSupportActionBar().setTitle("Badges Near Me");
                }
                fragmentHome.setFragment(FragmentHome.FRAGMENT_LANDMARK_LIST);
                fragmentHome.updateFocusHunts();
                break;
            case FRAGMENT_SEARCH:
                getSupportActionBar().setTitle("My Collections");
                mNavigationView.setSelectedItemId(R.id.menu_search);
                break;
            case FRAGMENT_PRIZES:
                getSupportActionBar().setTitle("My Prizes");
                mNavigationView.setSelectedItemId(R.id.menu_prizes);
                break;

        }
    }
    public void setupViewPager(ViewPager viewPager){
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        //YOU NEED TO HAVE THIS IN THE SAME ORDER AS NAV MENU
        fragmentMap = FragmentMap.newInstance(mHuntManager, mCameraManager);
        fragmentSearch = FragmentSearch.newInstance(mHuntManager);
        fragmentHome = FragmentHome.newInstance(mHuntManager);

        adapter.addFragment(fragmentMap, "MapFragment");
        adapter.addFragment(new FragmentAccount(), "AccountFragment");
        adapter.addFragment(fragmentHome, "HomeFragment");
        adapter.addFragment(fragmentSearch, "SearchFragment");
        adapter.addFragment(new FragmentPrizes(), "PrizesFragment");
        viewPager.setAdapter(adapter);

        mViewPager.setCurrentItem(FRAGMENT_HOME);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //note: for children fragments, change support action bar title where fragmentTransaction is called;
                switch(position){
                    case FRAGMENT_MAP:
                        if(mHuntManager.getSelectedHuntsSize()== 1){
                            getSupportActionBar().setTitle(mHuntManager.getFocusHunt().getName());
                        }
                        else {
                            getSupportActionBar().setTitle("Badges Near Me");
                        }
                        mNavigationView.setSelectedItemId(R.id.menu_map);
                        break;
                    case FRAGMENT_ACCOUNT:
                        getSupportActionBar().setTitle("My Account");
                        mNavigationView.setSelectedItemId(R.id.menu_account);
                        break;
                    case FRAGMENT_HOME:
                        if(mHuntManager.getSelectedHuntsSize()== 1){
                            getSupportActionBar().setTitle(mHuntManager.getFocusHunt().getName());
                        }
                        else {
                            getSupportActionBar().setTitle("Badges Near Me");
                        }
                        mNavigationView.setSelectedItemId(R.id.menu_home);
                        break;
                    case FRAGMENT_SEARCH:
                        getSupportActionBar().setTitle("My Collections");
                        mNavigationView.setSelectedItemId(R.id.menu_search);
                        break;
                    case FRAGMENT_PRIZES:
                        getSupportActionBar().setTitle("My Prizes");
                        mNavigationView.setSelectedItemId(R.id.menu_prizes);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("huntmanager", mHuntManager);

    }


    @Override
    public void onLandmarkSelected(Badge b) {
        mHuntManager.setFocusBadge(b.getID());
        fragmentHome.setFragment(FragmentHome.FRAGMENT_LANDMARK_INFO);
    }

    @Override
    public void onMapClicked(Badge badge) {
        mViewPager.setCurrentItem(FRAGMENT_MAP);
        mNavigationView.setSelectedItemId(FRAGMENT_MAP);
        fragmentMap.setCameraOnLandmark(badge);
    }

    @Override
    public void onCollectMoveCloser(Badge badge) {
        //if user isnt close enough, switch to map fragment, and set camera to target landmark
        mCameraManager.setMoveCloser(true);
        mViewPager.setCurrentItem(FRAGMENT_MAP);
        mNavigationView.setSelectedItemId(FRAGMENT_MAP);
        fragmentMap.setCameraOnLandmark(badge);
    }

    public boolean isCloseEnough(Badge badge){
        //check if user location is within valid distance to the landmark
        Location loc = new Location("");
        loc.setLatitude(badge.getLatitude());
        loc.setLongitude(badge.getLongitude());
        return mLocationTracker.isValidDistance(loc);
    }

    public void notifyLocationChanged(){
        if(fragmentHome != null)
            fragmentHome.notifyLocationChanged();

    }


    @Override
    public void onSpinnerOpened() {

    }

    @Override
    public void onSpinnerClosed() {
        //when spinner closes, update focus hunts, markers shown, and landmarks listed
        if(fragmentMap != null)
            fragmentMap.updateFocusHunts();
        if(fragmentHome != null)
            fragmentHome.updateFocusHunts();
        if(mHuntManager.getSelectedHuntsSize()== 1){
            //if only one hunt is selected, have title bar be set to hunt name
            getSupportActionBar().setTitle(mHuntManager.getFocusHunt().getName());
        }
        else{
            getSupportActionBar().setTitle("Badges Near Me");
        }
    }

    public void updateList(){
        fragmentMap.updateFocusHunts();
        fragmentHome.updateFocusHunts();
    }
}
