package com.davis.tyler.magpiehunt.Fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.R;

public class FragmentTimer extends Fragment {
    private static final String TAG = "fragment_timer";
    private TextView txt_timer;
    private int txt_num;
    private Thread thread;
    private long timetilfinished;
    private CountDownTimer countDownTimer;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        Log.e(TAG, "ONCREATE");
        txt_timer = view.findViewById(R.id.txt_timer);

        return view;
    }

    public static FragmentTimer newInstance() {
        FragmentTimer f = new FragmentTimer();
        return f;
    }
    public void startTimer(){
        ((ActivityBase)getActivity()).setBackButtonOnOff(false);
        ((ActivityBase)getActivity()).setNavigationViewVisibility(View.GONE);
        txt_num = 10;
        countDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(txt_timer != null) {
                    int num = (int)(millisUntilFinished / 1000);
                    timetilfinished = millisUntilFinished;
                    if(num >= 10)
                        txt_timer.setText("00:00:" + num);
                    else
                        txt_timer.setText("00:00:0" + num);
                }
            }

            public void onFinish() {
                ((ActivityBase)getActivity()).setPagerSwipe(true);
                ((ActivityBase)getActivity()).setNavigationViewVisibility(View.VISIBLE);
                Fragment f = getParentFragment();
                if(f instanceof FragmentMap){
                    ((FragmentMap) f).setFragment(FragmentMap.FRAGMENT_QUIZ);
                }
                else if( f instanceof FragmentList){
                    ((FragmentList) f).setFragment(FragmentList.FRAGMENT_QUIZ);
                }
            }
        }.start();
    }

    @Override
    public void onPause() {
        countDownTimer.cancel();
        super.onPause();
    }

    @Override
    public void onResume() {
        if(timetilfinished > 0) {
            countDownTimer = new CountDownTimer(timetilfinished, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (txt_timer != null) {
                        int num = (int) (millisUntilFinished / 1000);
                        timetilfinished = millisUntilFinished;
                        if (num >= 10)
                            txt_timer.setText("00:00:" + num);
                        else
                            txt_timer.setText("00:00:0" + num);
                    }
                }

                public void onFinish() {
                    ((ActivityBase) getActivity()).setPagerSwipe(true);
                    ((ActivityBase) getActivity()).setNavigationViewVisibility(View.VISIBLE);
                    Fragment f = getParentFragment();
                    if (f instanceof FragmentMap) {
                        ((FragmentMap) f).setFragment(FragmentMap.FRAGMENT_QUIZ);
                    } else if (f instanceof FragmentList) {
                        ((FragmentList) f).setFragment(FragmentList.FRAGMENT_QUIZ);
                    }
                }
            }.start();
        }
        super.onResume();
    }
}
