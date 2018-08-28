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
import android.widget.TextView;


import com.davis.tyler.magpiehunt.Fragments.FragmentLandmarkList;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.R;

import java.util.List;

//import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by Blake Impecoven on 1/26/18.
 */

public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.LandmarkHolder> {

    private static final String TAG = "LandmarkAdapter";
    private final String fragmentTag;
    private final Context context;
    private final FragmentLandmarkList fragment;
    private FragmentLandmarkList.OnLandmarkSelectedListener listener;


    public List<Badge> landmarkList;

    public LandmarkAdapter(List<Badge> landmarkList, String tag, Context context, FragmentLandmarkList fragment, FragmentLandmarkList.OnLandmarkSelectedListener listener) {
        this.landmarkList = landmarkList;
        this.fragmentTag = tag;
        this.context = context;
        this.fragment = fragment;
        this.listener = listener;
        //TODO MAKE TEST LANDMARK LSIT


    }//end DVC

    @Override
    public LandmarkAdapter.LandmarkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.landmark_card, parent, false);

        return new LandmarkHolder(view);
    }//end onCreateViewHolder

    @Override
    public void onBindViewHolder(LandmarkHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.setData(landmarkList.get(position), position);
    }//end onBindViewHolder

    @Override
    public int getItemCount() {
        return landmarkList.size();
    }//end getItemCount

    public class LandmarkHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = "LandmarkHolder";

        private int position;

        // fields for CardView (While Condensed)
        private TextView landmarkSponsor, landmarkName, minutestext, milestext;
        private TextView landmarkMiles, landmarkTime;
        private ImageView landmarkImage;

        private Badge currentObject;

        // fields for CardView (While Expanded)
        private TextView description;
        private LinearLayout card;
        private Typeface font;

        public LandmarkHolder(View itemView) {
            super(itemView);
            // attach to landmark_card.xml items
            font = ResourcesCompat.getFont(context, R.font.font_awesome);
            this.landmarkSponsor = itemView.findViewById(R.id.landmarkSponsor);
            this.landmarkName = itemView.findViewById(R.id.landmarkName);
            this.landmarkMiles = itemView.findViewById(R.id.landmarkMiles);
            this.landmarkTime = itemView.findViewById(R.id.landmarkTime);
            this.landmarkImage = itemView.findViewById(R.id.landmarkImage);
            minutestext = itemView.findViewById(R.id.minutestext);
            milestext = itemView.findViewById(R.id.milestext);



            this.card = itemView.findViewById(R.id.landmarkCard);
            setListeners();
        }//end EVC

        public void setData(Badge currentObject, int position) {
            // attach class fields to their respective items on landmark_card.xml

            landmarkSponsor.setText(currentObject.getLandmarkName()); // may need to move sponsor to landmark to display on card
            landmarkName.setText(currentObject.getName());

            Log.e(TAG, "setting distance in list");
            landmarkMiles.setText(""+currentObject.getDistance()); // need to get distance calculated
            landmarkTime.setText(""+currentObject.getMinutes()); // need to get time using lat and long from google services and their estimated time
            landmarkImage.setImageResource(R.drawable.magpie_test_cardview_collectionimage); // replace once we find out how to deal w/ images
            //this.landmarkName.setTypeface(font);
            //this.minutestext.setTypeface(font);
            //this.milestext.setTypeface(font);
            //this.landmarkSponsor.setTypeface(font);
            this.currentObject = currentObject;
            this.position = position;
        }//end setData

        public void setListeners() {
            // set listeners for items to be implemented with onClick functionality
            this.card.setOnClickListener(LandmarkHolder.this);
            // TODO: set listeners

        }//end setListeners

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                // TODO: implement click functionality
                case R.id.landmarkCard:
                    Log.d(TAG, "LandmarkClick: " + currentObject.getLandmarkName());
                    listener.onLandmarkSelected(currentObject);

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
            landmarkList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, landmarkList.size());
        }// end removeItem

        public void addItem(int position, Badge currentObject) {
            landmarkList.add(position, currentObject);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, landmarkList.size());
        }// end addItem
    }//end inner class: LandmarkHolder
}//end LandmarkAdapter
