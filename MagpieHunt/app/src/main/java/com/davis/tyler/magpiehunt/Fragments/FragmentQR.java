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
    private OnFragmentInteractionListener mListener;
    private Badge mBadge;
    private HuntManager mHuntManager;
    private SharedPreferences preferences;


    // TODO: Rename and change types and number of parameters
    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");
        //mBadge = mHuntManager.getFocusBadge();

    }

    public static FragmentQR newInstance(HuntManager huntManager) {
        FragmentQR f = new FragmentQR();
        Bundle args = new Bundle();
        //args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
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
            Fragment parent = getParentFragment();
            if(parent instanceof FragmentMap){
                ((FragmentMap) parent).setFragment(FragmentMap.FRAGMENT_LANDMARK_INFO);
            }
            else if(parent instanceof FragmentList){
                ((FragmentList) parent).setFragment(FragmentList.FRAGMENT_LANDMARK_INFO);
            }
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


    /*public boolean checkQRPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PackageManager.PERMISSION_GRANTED);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }*/

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                    if (getParentFragment() instanceof FragmentMap) {

                        ((FragmentMap) getParentFragment()).setFragment(FragmentMap.FRAGMENT_QUIZ);
                    } else if (getParentFragment() instanceof FragmentList) {
                        ((FragmentList) getParentFragment()).setFragment(FragmentList.FRAGMENT_QUIZ);
                    }
                }
                else {
                    //There is no quiz so user gets badge
                    mHuntManager.getFocusBadge().setmIsCompleted(true);

                    if (f instanceof FragmentList) {
                        ((FragmentList) f).setFragment(FragmentList.FRAGMENT_BADGE_OBTAINED);
                    } else if (f instanceof FragmentMap) {
                        ((FragmentMap) f).setFragment(FragmentMap.FRAGMENT_BADGE_OBTAINED);
                    }
                }
            }
            else {
                Toast.makeText(getContext(), "Wrong QR Code... found: " + result.getText(), Toast.LENGTH_SHORT).show();
                Fragment parent = getParentFragment();
                if(parent instanceof FragmentMap){
                    ((FragmentMap) parent).setFragment(FragmentMap.FRAGMENT_LANDMARK_INFO);
                }
                else if(parent instanceof FragmentList){
                    ((FragmentList) parent).setFragment(FragmentList.FRAGMENT_LANDMARK_INFO);
                }
            }

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
