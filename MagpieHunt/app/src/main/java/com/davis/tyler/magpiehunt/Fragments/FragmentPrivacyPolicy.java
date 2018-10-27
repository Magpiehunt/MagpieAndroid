package com.davis.tyler.magpiehunt.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davis.tyler.magpiehunt.Activities.ActivitySignIn;
import com.davis.tyler.magpiehunt.R;

public class FragmentPrivacyPolicy extends Fragment implements View.OnClickListener
{
    private static final String TAG = "FragmentPrivacyPolicy";
    private Button acceptBtn;

    public static FragmentPrivacyPolicy newInstance() {
        FragmentPrivacyPolicy fragment = new FragmentPrivacyPolicy();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "oncreate");
        View rootView = inflater.inflate(R.layout.fragment_privacypolicy, container, false);
        this.acceptBtn = rootView.findViewById(R.id.privacyAcceptBtn);
        this.acceptBtn.setVisibility(View.INVISIBLE);
        this.acceptBtn.setOnClickListener(this);
        if(getActivity() instanceof ActivitySignIn)
            showButton(true);
        else
            showButton(false);
        return rootView;
    }
    public void showButton(boolean show)
    {
        Log.e(TAG, "showbutton");
        if(show)
            this.acceptBtn.setVisibility(View.VISIBLE);
        else
            this.acceptBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.privacyAcceptBtn:
                ((ActivitySignIn)getActivity()).setPrivacyAccept();
                ((ActivitySignIn)getActivity()).setFragment(ActivitySignIn.FRAGMENT_TERMS);

                break;


            default:
                break;
        }//end switch

    }
}
