package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davis.tyler.magpiehunt.Activities.ActivitySignIn;
import com.davis.tyler.magpiehunt.R;
import com.davis.tyler.magpiehunt.Adapters.SectionsStatePagerAdapter;

public class FragmentWalkthroughContainer extends Fragment implements View.OnClickListener{
    private static final String TAG = "Walkthrough Container";
    private ViewPager mViewPager;
    private Button signin;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walkthrough_container, container, false);
        Log.e(TAG, "Oncreate"+ this);
        mViewPager = (ViewPager)view.findViewById(R.id.walkthrough_pager);
        /*if(savedInstanceState != null) {
            System.out.println("oncreate isnull not"+this);
            mViewPager.setCurrentItem(2);
        }else{
            System.out.println("oncreate isnull"+this);
            setupViewPager(mViewPager);
        }*/
        setupViewPager(mViewPager);
        Log.e(TAG, "Setting up pager6");
        mViewPager.setCurrentItem(((ActivitySignIn)getActivity()).getCurWalkthrough());
        Log.e(TAG, "Setting up pager7");
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((ActivitySignIn)getActivity()).setCurWalkthrough(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        signin = view.findViewById(R.id.btn_signin);
        Log.e(TAG, "Setting up pager8");
        signin.setOnClickListener(this);
        Log.e(TAG, "Setting up pager9");
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "Onattach");
    }
    public static FragmentWalkthroughContainer newInstance() {
        FragmentWalkthroughContainer f = new FragmentWalkthroughContainer();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    public void setupViewPager(ViewPager viewPager){
        Log.e(TAG, "Setting up pager1");
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getChildFragmentManager());

        Log.e(TAG, "Setting up pager2");
        adapter.addFragment(new FragmentWalkthrough1(), "Walkthrough1");
        Log.e(TAG, "Setting up pager3");
        adapter.addFragment(new FragmentWalkthrough2(), "Walkthrough2");
        Log.e(TAG, "Setting up pager4");
        adapter.addFragment(new FragmentWalkthrough3(), "Walkthrough3");
        Log.e(TAG, "Setting up pager");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_signin){
            ((ActivitySignIn)getActivity()).setFragment(ActivitySignIn.FRAGMENT_SIGNIN);
        }
    }

}

