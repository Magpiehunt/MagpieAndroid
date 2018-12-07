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

        ((ActivityBase)getActivity()).setBackButtonOnOff(true);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        Badge b = mHuntManager.getFocusBadge();
        txt_name.setText(b.getName());
        Hunt h = mHuntManager.getHuntByID(b.getHuntID());
        mHuntManager.setFocusAward(h.getID());
        h.updateIsCompleted();

        relativeLayout.setOnClickListener(this);
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

    public static FragmentBadgeObtained newInstance() {
        FragmentBadgeObtained f = new FragmentBadgeObtained();
        return f;
    }

    public void updateInfo(Badge b) {
        txt_name.setText(b.getName());
        b.setmIsCompleted(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.relativelayout){
            ((FragmentOverallHunt)getParentFragment()).setFragment(FragmentOverallHunt.FRAGMENT_OVERALL_HUNT_TABS);
            ((FragmentOverallHunt) getParentFragment()).updateFocusHunts();
            Badge b = mHuntManager.getFocusBadge();
            Hunt h = mHuntManager.getHuntByID(b.getHuntID());
            h.updateIsCompleted();
            if(h.getIsCompleted()){
                mHuntManager.setFocusAward(h.getID());
                ((FragmentOverallHunt) getParentFragment()).setHuntCompleteNotification(h);

            }


        }
    }
}
