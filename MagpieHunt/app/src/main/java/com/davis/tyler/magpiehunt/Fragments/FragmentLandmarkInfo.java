package com.davis.tyler.magpiehunt.Fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.GrayScaleTransformation;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;
import com.squareup.picasso.Picasso;

public class FragmentLandmarkInfo extends Fragment implements View.OnClickListener {
    private static final String TAG = "LandmarkInfo Fragment";
    private FragmentLandmarkInfo mFragmentLandmarkInfo;
    private ImageView img_badgeButton;
    private ImageView img_landmark;
    private TextView txt_badgeName;
    private TextView txt_Title;
    private TextView txt_Description;
    private TextView txt_Collect;
    private Button btn_map;
    private RelativeLayout btn_share;
    private RelativeLayout btn_nextbadge;
    private Button btn_nextbadgebottom;
    private HuntManager mHuntManager;
    private Badge mBadge;
    private onClickListener mListener;
    private View foregroundView;
    private SharedPreferences preferences;

    @Override
    public void onClick(View view) {
        if(mListener != null){
            if(view.getId() == R.id.mapButton) {
                mListener.onMapClicked(mBadge);
            }
            else if(view.getId() == R.id.lyt_nextbadge){
                Hunt h = mHuntManager.getHuntByID(mBadge.getHuntID());
                updateScreen(h.getNextBadge(mBadge));

            }
            else if(view.getId() == R.id.collectButton || view.getId() == R.id.collectText || view.getId() == R.id.foregroundCollect){
                if(mBadge.getIsCompleted()){
                    Toast.makeText(getContext(), "Badge already completed.", Toast.LENGTH_SHORT ).show();
                    return;
                }

                qrOrQuiz();
                /*THIS IS FOR WHEN YOU GET QR FUNCTIONALITY COMPLETE FROM CMS
                //If there is a qr code, dont check distance, just go to qr scanner
                if(mBadge.getQRurl() != null && !mBadge.getQRurl().equalsIgnoreCase("null")) {
                    if(!preferences.getBoolean("camera", false)) {
                        Toast.makeText(getContext(), "Cannot open QR Scanner without camera permission", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (getParentFragment() instanceof FragmentMap) {

                        ((FragmentMap) getParentFragment()).setFragment(FragmentMap.FRAGMENT_QR_READER);
                    } else if (getParentFragment() instanceof FragmentList) {
                        ((FragmentList) getParentFragment()).setFragment(FragmentList.FRAGMENT_QR_READER);
                    }
                }
                else if(((ActivityBase)getActivity()).getmLocationTracker().hasLocPermission()) {
                    if(((ActivityBase)getActivity()).isCloseEnough(mBadge)) {
                        System.out.println("qr code: " + mBadge.getQRurl());
                        if (mBadge.getQuiz() == null) {
                            Toast.makeText(getContext(), "No quiz or qr code found...", Toast.LENGTH_LONG).show();
                            mBadge.setmIsCompleted(true);
                            if (getParentFragment() instanceof FragmentMap) {

                                ((FragmentMap) getParentFragment()).setFragment(FragmentMap.FRAGMENT_BADGE_OBTAINED);
                            } else if (getParentFragment() instanceof FragmentList) {
                                ((FragmentList) getParentFragment()).setFragment(FragmentList.FRAGMENT_BADGE_OBTAINED);
                            }
                        } else if (mBadge.getQuiz() != null) {
                            Log.e(TAG, "Switching to Quiz fragment...");
                            if (getParentFragment() instanceof FragmentMap) {

                                ((FragmentMap) getParentFragment()).setFragment(FragmentMap.FRAGMENT_QUIZ);
                            } else if (getParentFragment() instanceof FragmentList) {
                                ((FragmentList) getParentFragment()).setFragment(FragmentList.FRAGMENT_QUIZ);
                            }
                        }
                    }
                    else
                        mListener.onCollectMoveCloser(mBadge);
                }
                else{
                    Toast.makeText(getContext(), "Turn On location permission to collect this badge", Toast.LENGTH_LONG).show();
                }*/

            }
        }
    }

    public void qrOrQuiz(){
        if (mBadge.getQuiz() != null) {
            //preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            if(!preferences.getBoolean("fine", false)){
                Toast.makeText(getContext(), "You must turn on location permissions to collect this.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (((ActivityBase) getActivity()).isCloseEnough(mBadge)) {
                /*if (getParentFragment() instanceof FragmentMap) {

                    ((FragmentMap) getParentFragment()).setFragment(FragmentMap.FRAGMENT_QUIZ);
                } else if (getParentFragment() instanceof FragmentList) {
                    ((FragmentList) getParentFragment()).setFragment(FragmentList.FRAGMENT_QUIZ);
                }*/
                ((FragmentList)getParentFragment()).setFragment(FragmentList.FRAGMENT_QUIZ);
            }
            else
                mListener.onCollectMoveCloser(mBadge);
            return;
        }

        if(mBadge.getQRurl() != null && !mBadge.getQRurl().equalsIgnoreCase("null")) {
            if(!preferences.getBoolean("camera", false)) {
                Toast.makeText(getContext(), "Cannot open QR Scanner without camera permission", Toast.LENGTH_SHORT).show();
                return;
            }
            /*if (getParentFragment() instanceof FragmentMap) {

                ((FragmentMap) getParentFragment()).setFragment(FragmentMap.FRAGMENT_QR_READER);
            } else if (getParentFragment() instanceof FragmentList) {
                ((FragmentList) getParentFragment()).setFragment(FragmentList.FRAGMENT_QR_READER);
            }*/
            ((FragmentList)getParentFragment()).setFragment(FragmentList.FRAGMENT_QR_READER);
        }
        else if(((ActivityBase)getActivity()).getmLocationTracker().hasLocPermission()) {
            if(((ActivityBase)getActivity()).isCloseEnough(mBadge)) {
                if (mBadge.getQuiz() == null) {
                    Toast.makeText(getContext(), "No quiz or qr code found...", Toast.LENGTH_LONG).show();
                    mBadge.setmIsCompleted(true);
                    /*if (getParentFragment() instanceof FragmentMap) {

                        ((FragmentMap) getParentFragment()).setFragment(FragmentMap.FRAGMENT_BADGE_OBTAINED);
                    } else if (getParentFragment() instanceof FragmentList) {
                        ((FragmentList) getParentFragment()).setFragment(FragmentList.FRAGMENT_BADGE_OBTAINED);
                    }*/
                    ((FragmentList)getParentFragment()).setFragment(FragmentList.FRAGMENT_BADGE_OBTAINED);
                }
            }

        }
        else{
            Toast.makeText(getContext(), "Turn On location permission to collect this badge", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateScreen(Badge b){
        mBadge = b;
        if(mBadge.getIsCompleted()){
            btn_share.setVisibility(View.INVISIBLE);
            btn_nextbadge.setVisibility(View.VISIBLE);
            btn_nextbadgebottom.setVisibility(View.VISIBLE);
            txt_Collect.setText("YOU ROCK!");

        }else{
            btn_share.setVisibility(View.VISIBLE);
            btn_nextbadge.setVisibility(View.INVISIBLE);
            btn_nextbadgebottom.setVisibility(View.INVISIBLE);
            txt_Collect.setText("COLLECT");
        }

        ImageManager imageManager = new ImageManager();
        imageManager.fillLandmarkImage(getContext(),mBadge, img_landmark);
        imageManager.fillBadgeImage(getContext(), mBadge, img_badgeButton);

        ((ActivityBase)getActivity()).setBackButtonOnOff(true);

        txt_Title.setText(mBadge.getLandmarkName());
        txt_Description.setText(mBadge.getDescription());
        txt_badgeName.setText(mBadge.getName());
    }
    public interface onClickListener{
        public void onMapClicked(Badge badge);
        public void onCollectMoveCloser(Badge badge);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landmark_info, container, false);
        Log.e(TAG, "on create");
        mHuntManager = ((ActivityBase)getActivity()).getData();
        img_badgeButton = (ImageView)view.findViewById(R.id.collectButton);
        foregroundView = (View)view.findViewById(R.id.foregroundCollect);
        img_landmark = (ImageView)view.findViewById(R.id.landmarkImage);
        txt_Title = (TextView)view.findViewById(R.id.landmarkSubTitle);
        txt_Collect = (TextView)view.findViewById(R.id.collectText);
        txt_Collect.setOnClickListener(this);
        txt_Description = (TextView)view.findViewById(R.id.landmarkDescription);
        txt_badgeName = (TextView)view.findViewById(R.id.landmarkName);
        btn_map = (Button)view.findViewById(R.id.mapButton);
        btn_nextbadge = view.findViewById(R.id.lyt_nextbadge);
        btn_share = view.findViewById(R.id.lyt_share);
        btn_nextbadgebottom = view.findViewById(R.id.btn_nextbadge);
        mListener = ((onClickListener)getParentFragment());
        foregroundView.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_nextbadge.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());



        mBadge = mHuntManager.getFocusBadge();
        img_badgeButton.setOnClickListener(this);

        updateScreen(mBadge);
        return view;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        //mHuntManager = (HuntManager)args.getSerializable("huntmanager");

    }

    public static FragmentLandmarkInfo newInstance(HuntManager huntManager) {
        FragmentLandmarkInfo f = new FragmentLandmarkInfo();
        Bundle args = new Bundle();
        //args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "OndestroyView()");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.e(TAG, "visible");
        } else {
            Log.e(TAG, "not visible");
        }
    }


}
