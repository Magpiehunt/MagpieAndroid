package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
        Log.e(TAG, "Oncreate");
        mViewPager = (ViewPager)view.findViewById(R.id.walkthrough_pager);
        setupViewPager(mViewPager);
        signin = view.findViewById(R.id.btn_signin);
        signin.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "Onattach");
    }

    public void setupViewPager(ViewPager viewPager){
        Log.e(TAG, "Setting up pager");
        SectionsStatePagerAdapter adapter = new SectionsStatePagerAdapter(getChildFragmentManager());

        Log.e(TAG, "Setting up pager");
        adapter.addFragment(new FragmentWalkthrough1(), "Walkthrough1");
        Log.e(TAG, "Setting up pager");
        adapter.addFragment(new FragmentWalkthrough2(), "Walkthrough2");
        Log.e(TAG, "Setting up pager");
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
