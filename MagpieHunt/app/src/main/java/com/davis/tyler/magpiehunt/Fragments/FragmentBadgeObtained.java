package com.davis.tyler.magpiehunt.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.FileSystemManager;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;
import com.squareup.picasso.Picasso;

public class FragmentBadgeObtained extends Fragment implements View.OnClickListener {
    private static final String TAG = "Prizes Info Fragment";
    private TextView txt_name;
    private ImageView img_badge;
    private HuntManager mHuntManager;
    private RelativeLayout relativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_badge_collected, container, false);

        relativeLayout = view.findViewById(R.id.relativelayout);
        txt_name = view.findViewById(R.id.txt_badge_name);
        img_badge = view.findViewById(R.id.img_badge);
        /*txt_name.setText(mHuntManager.getFocusBadge().getName());
        if(mHuntManager.getSelectedHuntsSize() == 1){
            mHuntManager.getSingleSelectedHunt().updateIsCompleted();
        }
        */

        ((ActivityBase)getActivity()).setBackButtonOnOff(true);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        Badge b = mHuntManager.getFocusBadge();
        txt_name.setText(b.getName());
        Hunt h = mHuntManager.getHuntByID(b.getHuntID());
        h.updateIsCompleted();

        relativeLayout.setOnClickListener(this);
        /*Picasso.get().load("http://206.189.204.95/badge/icon/"+mHuntManager.getFocusBadge().getIcon())
                .resize(200,200)
                .into(img_badge);*/
        ImageManager im = new ImageManager();
        im.fillBadgeImage(getContext(), mHuntManager.getFocusBadge(), img_badge);
        addHuntsToFileSystem();

        return view;
    }

    private void addHuntsToFileSystem(){
        FileSystemManager fm = new FileSystemManager();
        try {
            fm.addHuntList(getContext(), mHuntManager.getAllDownloadedHunts());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static FragmentBadgeObtained newInstance(HuntManager huntManager) {
        FragmentBadgeObtained f = new FragmentBadgeObtained();
        Bundle args = new Bundle();
        //args.putSerializable("huntmanager", huntManager);
        f.setArguments(args);
        return f;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        Log.e(TAG, "setArguments, args: " + args);
        //mHuntManager = (HuntManager) args.getSerializable("huntmanager");
        Log.e(TAG, "setArguments, huntman: " + mHuntManager);


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

    public void updateInfo(Badge b) {
        txt_name.setText(b.getName());
        b.setmIsCompleted(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.relativelayout){
            if(getParentFragment() instanceof FragmentList){
                ((FragmentList)getParentFragment()).setFragment(FragmentList.FRAGMENT_BIRDS_EYE);
                ((FragmentList) getParentFragment()).updateFocusHunts();
            }
            else if(getParentFragment() instanceof FragmentMap){
                ((ActivityBase)getActivity()).changePage(ActivityBase.FRAGMENT_MAP);
            }
        }
    }
}
