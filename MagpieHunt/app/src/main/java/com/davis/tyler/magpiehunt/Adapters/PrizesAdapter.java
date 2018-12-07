package com.davis.tyler.magpiehunt.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Fragments.FragmentPrizesList;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class PrizesAdapter extends RecyclerView.Adapter<PrizesAdapter.PrizeHolder> {

private static final String TAG = "LandmarkAdapter";
private final Context context;
private FragmentPrizesList.OnHuntSelectedListener listener;


public LinkedList<Hunt> huntList;

public PrizesAdapter(LinkedList<Hunt> huntList, Context context, FragmentPrizesList.OnHuntSelectedListener listener) {
        this.huntList = huntList;
        this.context = context;
        this.listener = listener;


        }//end DVC

@Override
public PrizeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.card_prize, parent, false);

        return new PrizeHolder(view);
        }//end onCreateViewHolder


@Override
public void onBindViewHolder(PrizeHolder holder, int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.setData(huntList.get(position));
        }//end onBindViewHolder

@Override
public int getItemCount() {
        return huntList.size();
        }//end getItemCount
public void updateList(LinkedList<Hunt> ll){
    huntList = ll;
}
public class PrizeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = "PrizeHolder";

    private TextView txt_huntAbbr, txt_huntName;
    private ImageView img_superBadge;
    private TextView txt_new;

    private Hunt currentObject;
    private LinearLayout card;

    public PrizeHolder(View itemView) {
        super(itemView);
        txt_huntAbbr = itemView.findViewById(R.id.tvAbbreviation_prize);
        txt_huntName = itemView.findViewById(R.id.tvTitle_prize);
        txt_new = itemView.findViewById(R.id.txt_new);
        this.img_superBadge = itemView.findViewById(R.id.img_super_badge);



        this.card = itemView.findViewById(R.id.prize_card);
        setListeners();
    }//end EVC

    public void setData(Hunt currentObject) {
        // attach class fields to their respective items on card_landmark.xml

        txt_huntName.setText(currentObject.getName());
        txt_huntAbbr.setText(currentObject.getAbbreviation());
        if(currentObject.getAward().getIsNew()) {
            txt_new.setVisibility(View.VISIBLE);
        }
        else{
            txt_new.setVisibility(View.GONE);
        }
        this.currentObject = currentObject;
        ImageManager imageManager = new ImageManager();
        imageManager.fillSuperBadgeImage(context, currentObject, img_superBadge);
    }//end setData

    public void setListeners() {
        // set listeners for items to be implemented with onClick functionality
        this.card.setOnClickListener(this);

    }//end setListeners

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prize_card:
                listener.onHuntSelected(currentObject);

                break;
            default:
                break;
        }//end switch
    }// end onClick

}//end inner class: LandmarkHolder
}//end LandmarkAdapter