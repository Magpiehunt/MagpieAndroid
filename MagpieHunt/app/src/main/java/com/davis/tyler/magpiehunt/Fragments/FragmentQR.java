package com.davis.tyler.magpiehunt.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.Activity.RESULT_OK;


/**
 * This fragment is used for scanning a QR code to verify that the user is at the correct location
 * if the previous fragment was the Landmark fragment then the result of the scan (upon qr code being found) is returned to that fragment
 * if this fragment was selected from the nav bar it currently just prints it to the screen. obviously this will need a bit of wiring work.
 */
//TODO handle denied permissions better
//TODO result of qr scan could be handled better
public class FragmentQR extends Fragment implements ZXingScannerView.ResultHandler{
    private static final String TAG = "Fragment QR";
    private ZXingScannerView scanner;
    private Badge mBadge;
    private HuntManager mHuntManager;
    private SharedPreferences preferences;


    public static FragmentQR newInstance() {
        FragmentQR f = new FragmentQR();
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mHuntManager = ((ActivityBase)getActivity()).getData();
        mBadge = mHuntManager.getFocusBadge();
        View view = inflater.inflate(R.layout.fragment_qr, container, false);
        scanner = view.findViewById(R.id.qr_scanner);
        ((ActivityBase)getActivity()).setBackButtonOnOff(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        if(preferences.getBoolean("camera", false)) {
            scanner.setResultHandler(this);
            scanner.startCamera();
        }
        else{
            Toast.makeText(getContext(), "Cannot collect badge without camera permission", Toast.LENGTH_SHORT).show();
            ((FragmentOverallHunt)getParentFragment()).setFragment(FragmentOverallHunt.FRAGMENT_LANDMARK_INFO);
        }
        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
        scanner.stopCamera();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(scanner != null){
            scanner.startCamera();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //handle result from qr scanner see top of file for a description of what this is currently doing.
    @Override
    public void handleResult(Result result) {
        Log.e(TAG, "Handling result");

            if(mBadge.getQRurl() != null && mBadge.getQRurl().equals(result.getText())) {
                Toast.makeText(getContext(), "Found: " + result.getText(), Toast.LENGTH_SHORT).show();
                Fragment f = getParentFragment();

                //If there is a quiz in badge object, go to quiz screen next
                if(mBadge.getQuiz() != null) {
                    Log.e(TAG, "Switching to Quiz fragment...");
                    ((FragmentOverallHunt)getParentFragment()).setFragment(FragmentOverallHunt.FRAGMENT_QUIZ);
                }
                else {
                    //There is no quiz so user gets badge
                    mHuntManager.getFocusBadge().setmIsCompleted(true);

                    ((FragmentOverallHunt)getParentFragment()).setFragment(FragmentOverallHunt.FRAGMENT_BADGE_OBTAINED);
                }
            }
            else {
                Toast.makeText(getContext(), "Wrong QR Code... found: " + result.getText(), Toast.LENGTH_SHORT).show();
                ((FragmentOverallHunt)getParentFragment()).setFragment(FragmentOverallHunt.FRAGMENT_LANDMARK_INFO);
            }

    }

}
