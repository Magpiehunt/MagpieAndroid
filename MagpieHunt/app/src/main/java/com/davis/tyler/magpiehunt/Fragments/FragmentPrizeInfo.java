package com.davis.tyler.magpiehunt.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.Hunts.Award;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;
import com.squareup.picasso.Picasso;

public class FragmentPrizeInfo extends Fragment {
    private static final String TAG = "Prizes Info Fragment";
    private TextView txt_prize;
    private TextView txt_code;
    private ImageView img_badge;
    private HuntManager mHuntManager;
    private TextView txt_terms;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prize_code, container, false);
        mHuntManager = ((ActivityBase)getActivity()).getData();
        txt_code = view.findViewById(R.id.txt_prize_code);
        txt_prize = view.findViewById(R.id.txt_prize_name);
        txt_terms = view.findViewById(R.id.txt_redeemable_at);
        img_badge = view.findViewById(R.id.img_super_badge);
        //txt_code.setText(""+mHuntManager.getFocusAward().getID());
        Award award = mHuntManager.getFocusAward();
        award.setIsNew(false);
        txt_prize.setText(award.getName());
        txt_terms.setText("Redeemable only at "+award.getAddress()+". "+award.getTerms());
        if(!award.getmRedemptionCode().equals("") && !award.getmRedemptionCode().equals("null"))
            txt_code.setText(award.getmRedemptionCode());
        ((ActivityBase)getActivity()).setBackButtonOnOff(true);

        ImageManager imageManager = new ImageManager();
        imageManager.fillAwardFinished(getContext(), award, img_badge);

        return view;
    }

    public static FragmentPrizeInfo newInstance() {
        FragmentPrizeInfo f = new FragmentPrizeInfo();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }


}