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
import com.davis.tyler.magpiehunt.R;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class PrizesAdapter extends RecyclerView.Adapter<PrizesAdapter.PrizeHolder> {

private static final String TAG = "LandmarkAdapter";
private final Context context;
private final FragmentPrizesList fragment;
private FragmentPrizesList.OnHuntSelectedListener listener;


public LinkedList<Hunt> huntList;

public PrizesAdapter(LinkedList<Hunt> huntList, Context context, FragmentPrizesList fragment, FragmentPrizesList.OnHuntSelectedListener listener) {
        this.huntList = huntList;
        this.context = context;
        this.fragment = fragment;
        this.listener = listener;
        //TODO MAKE TEST LANDMARK LSIT


        }//end DVC

@Override
public PrizeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.card_prize, parent, false);

        return new PrizeHolder(view);
        }//end onCreateViewHolder


@Override
public void onBindViewHolder(PrizeHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.setData(huntList.get(position), position);
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

    private int position;

    // fields for CardView (While Condensed)
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

    public void setData(Hunt currentObject, int position) {
        // attach class fields to their respective items on card_landmark.xml

        txt_huntName.setText(currentObject.getName());
        txt_huntAbbr.setText(currentObject.getAbbreviation());
        img_superBadge.setImageResource(R.drawable.magpie_test_cardview_collectionimage);
        if(currentObject.getAward().getIsNew()) {
            txt_new.setVisibility(View.VISIBLE);
        }
        else{
            txt_new.setVisibility(View.GONE);
        }
        this.currentObject = currentObject;
        Picasso.get().load("http://206.189.204.95/superbadge/image/"+currentObject.getAward().getSuperBadgeIcon()).fit().centerCrop().into(img_superBadge);
        this.position = position;
    }//end setData

    public void setListeners() {
        // set listeners for items to be implemented with onClick functionality
        this.card.setOnClickListener(this);
        // TODO: set listeners

    }//end setListeners

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // TODO: implement click functionality
            case R.id.prize_card:
                listener.onHuntSelected(currentObject);

                break;
            default:
                break;
        }//end switch
    }// end onClick

    private void startLandmark() {


    }

    // will be used at some point.
    //TODO: decide on gesture or button removal.
    public void removeItem(int position) {
        huntList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, huntList.size());
    }// end removeItem

    public void addItem(int position, Hunt currentObject) {
        huntList.add(position, currentObject);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, huntList.size());
    }// end addItem
}//end inner class: LandmarkHolder
}//end LandmarkAdapter