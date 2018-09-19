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

import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

public class FragmentTimer extends Fragment {
    private static final String TAG = "fragment_timer";
    private TextView txt_timer;
    private int num;
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
        num = 10;
        /*final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                num --;
                if(txt_timer != null) {
                    txt_timer.setText("00:00:" + num);
                }
                if(num < 1){

                    t.cancel();
                    t.purge();
                }
            }
        }, 0, 1000);*/
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                if(txt_timer != null) {
                    int num = (int)(millisUntilFinished / 1000);
                    if(num >= 10)
                        txt_timer.setText("00:00:" + num);
                    else
                        txt_timer.setText("00:00:0" + num);
                }
            }

            public void onFinish() {
                Fragment f = getParentFragment();
                if(f instanceof FragmentMap){
                    ((FragmentMap) f).setFragment(FragmentMap.FRAGMENT_QUIZ);
                }
                else if( f instanceof FragmentHome){
                    ((FragmentHome) f).setFragment(FragmentHome.FRAGMENT_QUIZ);
                }
            }
        }.start();
    }

}
