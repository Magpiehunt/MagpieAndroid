package com.davis.tyler.magpiehunt.Adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.Activities.ActivityBase;
import com.davis.tyler.magpiehunt.CMS.DownloadImage;
import com.davis.tyler.magpiehunt.FileSystemManager;
import com.davis.tyler.magpiehunt.Fragments.FragmentSearchHunts;
import com.davis.tyler.magpiehunt.Hunts.Award;
import com.davis.tyler.magpiehunt.Hunts.Badge;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.Hunts.HuntManager;
import com.davis.tyler.magpiehunt.R;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class SearchCollectionAdapter extends RecyclerView.Adapter<SearchCollectionAdapter.CollectionHolder> {

    private static final String TAG = "SearchCollectionAdapter";
    private final Context context;
    private List<Hunt> collectionList;
    private SparseBooleanArray expandState;
    protected HuntManager huntManager;
    protected FragmentSearchHunts listener;


    public SearchCollectionAdapter(List<Hunt> collectionList, Context context, HuntManager huntManager,
                                   FragmentSearchHunts listener) {
        this.collectionList = collectionList;
        this.expandState = new SparseBooleanArray();
        for (int x = 0; x < collectionList.size(); x++) {
            expandState.append(x, false);
        }//end for
        this.context = context;
        this.huntManager = huntManager;
        this.listener = listener;
    }//end DVC

    // Create new views (invoked by the layout manager)
    @Override
    public CollectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_search_hunt, parent, false);
        return new CollectionHolder(view);
    }//end onCreateViewHolder

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final CollectionHolder holder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.setIsRecyclable(false);
        holder.setCondensedData(position);

        holder.expandableLinearLayout.setInRecyclerView(true);
        holder.expandableLinearLayout.setExpanded(expandState.get(position));
        holder.expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {

            @Override
            public void onPreOpen() {
                changeRotate(holder.expandArrow, 0f, 180f).start();
                expandState.put(position, true);
            }

            @Override
            public void onPreClose() {
                changeRotate(holder.expandArrow, 180f, 0f).start();
                expandState.put(position, false);
            }
        });

        holder.expandArrow.setRotation(expandState.get(position) ? 180f : 0f);
        holder.setListeners();

        holder.setExpandedData(position);
    }//end onBindViewHolder

    private ObjectAnimator changeRotate(ImageView button, float to, float from) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "rotation", to, from);
        animator.setDuration(200);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }
    public void updateList(LinkedList<Hunt> list){
        collectionList = list;
    }
    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public class CollectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = "CollectionHolder";
        // fields for CardView (Expanded)
        ExpandableLinearLayout expandableLinearLayout;
        private int position;
        // We may need to add more fields here for expanding of the cards.
        // fields for CardView (Condensed)
        private TextView collectionTitle;
        private TextView collectionAbbreviation;
        private ImageView imgThumb, expandArrow;
        private Hunt currentObject;
        private TextView description;
        private TextView numBadges;
        private TextView age;
        private TextView rewardName;
        private TextView rewardWorth;
        private TextView distance;
        private TextView time;
        private LinearLayout container;

        private Button addCollectionBtn;

        public CollectionHolder(View itemView) {
            super(itemView);
            this.container = itemView.findViewById(R.id.card_search);
            this.collectionTitle = itemView.findViewById(R.id.tvTitle_search);
            this.collectionAbbreviation = itemView.findViewById(R.id.tvAbbreviation_search);
            this.imgThumb = itemView.findViewById(R.id.img_thumb_search);
            this.expandArrow = itemView.findViewById(R.id.expandArrow_search);
            this.distance = itemView.findViewById(R.id.collectionDistance_search);

            // expanded views
            this.expandableLinearLayout = itemView.findViewById(R.id.expandableLayout_search);
            this.description = itemView.findViewById(R.id.dropdown_description_search);
            this.addCollectionBtn = itemView.findViewById(R.id.button_addCollection_search);
            this.numBadges = itemView.findViewById(R.id.collectionBadges_search);
            this.age = itemView.findViewById(R.id.collectionAge_search);
            this.rewardName = itemView.findViewById(R.id.collectionRewardName);
            this.rewardWorth = itemView.findViewById(R.id.collectionRewardWorth);
            this.time = itemView.findViewById(R.id.collectionTime_search);
            container.setOnClickListener(this);
        }//end DVC

        void setCondensedData(int position) {
            currentObject = collectionList.get(position);
            Picasso.get().load("http://206.189.204.95/superbadge/image/"+currentObject.getAward().getSuperBadgeIcon()).fit().centerCrop().into(imgThumb);

            this.collectionTitle.setText(currentObject.getName());
            this.collectionAbbreviation.setText(currentObject.getAbbreviation());
            this.imgThumb.setImageResource(R.drawable.magpie_test_cardview_collectionimage);
            currentObject.shortestPath();
            distance.setText(currentObject.getmDistance()+"");
            time.setText(currentObject.getTime()+"");
        }//end setCondensedData

        void setExpandedData(int position) {
            Hunt currentObject = collectionList.get(position);
            this.age.setText(""+currentObject.getAudience()+"+");
            this.description.setText(currentObject.getDescription());
            this.numBadges.setText(""+currentObject.getNumBadges());
            this.rewardName.setText(currentObject.getAward().getName());
            this.rewardWorth.setText(currentObject.getAward().getWorth()+"$");
            Hunt h = huntManager.getHuntByID(currentObject.getID());
            if(h == null){
                addCollectionBtn.setText("ADD COLLECTION");
            }else if(h.getIsDownloaded() && !h.getIsDeleted()){
                addCollectionBtn.setText("DOWNLOADED");
            }else
                addCollectionBtn.setText("ADD COLLECTION");


        }//end setExpandedData


        public void setListeners() {
            expandArrow.setOnClickListener(CollectionHolder.this);
            addCollectionBtn.setOnClickListener(CollectionHolder.this);
        }//end setListeners

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.expandArrow_search:
                    this.expandableLinearLayout.toggle();
                    break;

                case R.id.card_search:
                    Hunt hunt = huntManager.getHuntByID(currentObject.getID());
                    if(hunt.getIsDownloaded() && !hunt.getIsDeleted()){
                        huntManager.setFocusHunt(hunt.getID());
                        ((ActivityBase)listener.getActivity()).collectionClicked();

                    }
                    else{
                        huntManager.setFocusHunt(hunt.getID());
                        ((ActivityBase)listener.getActivity()).updateMapForUndownloadedHunt();

                    }
                    break;

                case R.id.button_addCollection_search:
                    Hunt h = huntManager.getHuntByID(currentObject.getID());
                    if(h == null){
                        h = currentObject;
                        h.setmIsDeleted(false);
                        h.setmIsDownloaded(true);
                        listener.onAddHuntListener(currentObject);
                        addCollectionBtn.setText("DOWNLOADED");
                        notifyDataSetChanged();
                    }else if(h.getIsDownloaded() && !h.getIsDeleted()){
                        Toast.makeText(context, "Hunt already downloaded", Toast.LENGTH_LONG).show();
                    }else {
                        h.setmIsDeleted(false);
                        h.setmIsDownloaded(true);
                        listener.onAddHuntListener(currentObject);
                        addCollectionBtn.setText("DOWNLOADED");
                        notifyDataSetChanged();
                    }

                default:
                    break;
            }//end switch
        }//end onClick

    }//end inner class: CollectionHolder


}//end CollectionAdapter
