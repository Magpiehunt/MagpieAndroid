package com.davis.tyler.magpiehunt.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davis.tyler.magpiehunt.Activities.ActivitySignIn;
import com.davis.tyler.magpiehunt.R;

public class FragmentTermsAndConditions extends Fragment implements View.OnClickListener
{
    private static final String TAG = "FragmentTermsAndConditions";
    private Button acceptBtn;

    public static FragmentTermsAndConditions newInstance() {
        FragmentTermsAndConditions fragment = new FragmentTermsAndConditions();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_termsandconditions, container, false);
        this.acceptBtn = rootView.findViewById(R.id.TermsAcceptBtn);
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
        if(show)
            this.acceptBtn.setVisibility(View.VISIBLE);
        else
            this.acceptBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TermsAcceptBtn:
                ((ActivitySignIn)getActivity()).setTermsAccept();
                ((ActivitySignIn)getActivity()).setFragment(ActivitySignIn.FRAGMENT_SIGNIN);

                break;


            default:
                break;
        }//end switch

    }
}
