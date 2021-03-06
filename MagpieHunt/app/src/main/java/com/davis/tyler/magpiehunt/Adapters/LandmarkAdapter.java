package com.davis.tyler.magpiehunt.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.davis.tyler.magpiehunt.Fragments.FragmentLandmarkList;
import com.davis.tyler.magpiehunt.GrayScaleTransformation;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.LandmarkHolder> {

    private static final String TAG = "LandmarkAdapter";
    private final Context context;
    private FragmentLandmarkList.OnLandmarkSelectedListener listener;


    public List<Badge> landmarkList;

    public LandmarkAdapter(List<Badge> landmarkList,Context context, FragmentLandmarkList.OnLandmarkSelectedListener listener) {
        this.landmarkList = landmarkList;
        this.context = context;
        this.listener = listener;


    }//end DVC

    @Override
    public LandmarkAdapter.LandmarkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_landmark, parent, false);

        return new LandmarkHolder(view);
    }//end onCreateViewHolder

    @Override
    public void onBindViewHolder(LandmarkHolder holder, int position) {
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

        // fields for CardView (While Condensed)
        private TextView landmarkSponsor, landmarkName;
        private TextView landmarkMiles, landmarkTime;
        private ImageView landmarkImage;

        private Badge currentObject;

        private LinearLayout card;

        public LandmarkHolder(View itemView) {
            super(itemView);
            this.landmarkSponsor = itemView.findViewById(R.id.landmarkName);
            this.landmarkName = itemView.findViewById(R.id.badgeName);
            this.landmarkMiles = itemView.findViewById(R.id.landmarkMiles);
            this.landmarkTime = itemView.findViewById(R.id.landmarkTime);
            this.landmarkImage = itemView.findViewById(R.id.landmarkImage);



            this.card = itemView.findViewById(R.id.landmarkCard);
            setListeners();
        }//end EVC

        public void setData(Badge currentObject, int position) {
            // attach class fields to their respective items on card_landmark.xml

            landmarkSponsor.setText(currentObject.getLandmarkName()); // may need to move sponsor to landmark to display on card
            landmarkName.setText(currentObject.getName());
            if(currentObject.getIsCompleted())
                landmarkName.setTextColor(ContextCompat.getColor(context, R.color.colorMagpieBlack));
            else
                landmarkName.setTextColor(ContextCompat.getColor(context, R.color.colorMagpieLightGray));

            landmarkMiles.setText(""+round(currentObject.getDistance(), 2)); // need to get distance calculated
            landmarkTime.setText(""+currentObject.getMinutes()); // need to get time using lat and long from google services and their estimated time

            this.currentObject = currentObject;
            ImageManager im = new ImageManager();
            im.fillBadgeImage(context, currentObject, landmarkImage);
        }//end setData
        public double round(double value, int places) {
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
        public void setListeners() {
            // set listeners for items to be implemented with onClick functionality
            this.card.setOnClickListener(LandmarkHolder.this);
        }//end setListeners

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.landmarkCard:
                    Log.d(TAG, "LandmarkClick: " + currentObject.getLandmarkName());
                    listener.onLandmarkSelected(currentObject);

                    break;
                default:
                    break;
            }//end switch
        }// end onClick


    }//end inner class: LandmarkHolder
}//end LandmarkAdapter
