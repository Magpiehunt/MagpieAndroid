package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Listeners.OnSwipeTouchListener;
import com.davis.tyler.magpiehunt.R;

public class FragmentSigninInitial extends Fragment implements View.OnClickListener{
    private static final String TAG = "Signin Initial Fragment";
    private Button btn_signin;
    private FragmentSigninInitialListener mListener;



    public interface FragmentSigninInitialListener{
        void swipedLeftEvent();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin_initial, container, false);
        btn_signin = (Button)view.findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(this);
        view.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            public void onSwipeLeft() {
                mListener.swipedLeftEvent();
            }

            public void onSwipeRight() {
                mListener.swipedLeftEvent();
            }
        });
        return view;

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof FragmentSigninInitialListener){
            mListener = (FragmentSigninInitialListener)context;
        }
        else{
            throw new RuntimeException(context.toString()+ "must implement fragmentmaplistener");
        }
    }
    @Override
    public void onClick(View view) {
        //this is the login button clicked event. For now this goes straight to Home Activity.
        //later this will activate a fragment transaction.
        if(view.getId() == R.id.btn_signin){
            startActivity(new Intent(getActivity(), ActivityBase.class));
            getActivity().finish();
        }
    }
}
