package com.davis.tyler.magpiehunt.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.QRFragment;
import com.davis.tyler.magpiehunt.R;

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
    private HuntManager mHuntManager;
    private Badge mBadge;
    private onClickListener mListener;
    private View foregroundView;

    @Override
    public void onClick(View view) {
        if(mListener != null){
            if(view.getId() == R.id.mapButton)
                mListener.onMapClicked(mBadge);
            else if(view.getId() == R.id.collectButton || view.getId() == R.id.collectText || view.getId() == R.id.foregroundCollect){
                if(((ActivityBase)getActivity()).isCloseEnough(mBadge)) {
                    if (getParentFragment() instanceof FragmentMap) {
                        Log.e(TAG, "Switching to QR code fragment...");
                        ((FragmentMap) getParentFragment()).setFragment(FragmentMap.FRAGMENT_QR_READER);
                    } else if (getParentFragment() instanceof FragmentHome) {
                        ((FragmentHome) getParentFragment()).setFragment(FragmentHome.FRAGMENT_QR_READER);
                    }
                }
                else
                    mListener.onCollectMoveCloser(mBadge);
            }
        }
    }

    public interface onClickListener{
        public void onMapClicked(Badge badge);
        public void onCollectMoveCloser(Badge badge);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landmark_info, container, false);

        img_badgeButton = (ImageView)view.findViewById(R.id.collectButton);
        foregroundView = (View)view.findViewById(R.id.foregroundCollect);
        img_landmark = (ImageView)view.findViewById(R.id.landmarkImage);
        txt_Title = (TextView)view.findViewById(R.id.landmarkSubTitle);
        txt_Collect = (TextView)view.findViewById(R.id.collectText);
        txt_Collect.setOnClickListener(this);
        txt_Description = (TextView)view.findViewById(R.id.landmarkDescription);
        txt_badgeName = (TextView)view.findViewById(R.id.landmarkName);
        btn_map = (Button)view.findViewById(R.id.mapButton);
        mListener = ((onClickListener)getActivity());
        foregroundView.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example_badge);
        Bitmap landmarkpic = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ewu);
        mBadge = mHuntManager.getFocusBadge();
        img_badgeButton.setImageBitmap(bitmap);
        img_badgeButton.setOnClickListener(this);
        img_landmark.setImageBitmap(landmarkpic);
        txt_Title.setText(mBadge.getLandmarkName());
        txt_Description.setText(mBadge.getDescription());
        txt_badgeName.setText(mBadge.getName());
        return view;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        mHuntManager = (HuntManager)args.getSerializable("huntmanager");

    }

    public static FragmentLandmarkInfo newInstance(HuntManager huntManager) {
        FragmentLandmarkInfo f = new FragmentLandmarkInfo();
        Bundle args = new Bundle();
        args.putSerializable("huntmanager", huntManager);
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
