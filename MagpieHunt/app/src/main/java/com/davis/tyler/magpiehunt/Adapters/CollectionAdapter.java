package com.davis.tyler.magpiehunt.Adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davis.tyler.magpiehunt.Fragments.FragmentHuntsList;
import com.davis.tyler.magpiehunt.GrayScaleTransformation;
import com.davis.tyler.magpiehunt.Hunts.Hunt;
import com.davis.tyler.magpiehunt.ImageManager;
import com.davis.tyler.magpiehunt.R;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;


public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionHolder> {

    private static final String TAG = "CollectionAdapter";
    private final Context context;

    private List<Hunt> collectionList;
    private SparseBooleanArray expandState;
    private FragmentHuntsList.OnCollectionSelectedListener listener;

    public CollectionAdapter(List<Hunt> collectionList, Context context,FragmentHuntsList.OnCollectionSelectedListener listener) {
        this.collectionList = collectionList;
        this.context = context;
        this.expandState = new SparseBooleanArray();
        this.listener = listener;
        for (int x = 0; x < collectionList.size(); x++) {
            expandState.append(x, false);
        }//end for
    }//end DVC

    // Create new views (invoked by the layout manager)
    @Override
    public CollectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_hunt, parent, false);

        return new CollectionHolder(view);
    }//end onCreateViewHolder

    public void updateList(LinkedList<Hunt> list){
        collectionList = list;
    }
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
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    @Override
    public int getItemCount() {
        return collectionList.size();
    }

    public void removeItem(int position) {

        collectionList.get(position).setmIsDeleted(true);
        collectionList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, collectionList.size());
        listener.onCollectionDeleted();
    }

    public class CollectionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = "CollectionHolder";
        // fields for CardView (Expanded)
        ExpandableLinearLayout expandableLinearLayout;
        private TextView collectionTitle;
        private TextView collectionAbbreviation;
        private ImageView imgThumb, expandArrow;
        private Hunt currentObject;
        private TextView description;
        private TextView age;
        private TextView numBadges;
        private TextView rewardWorth;
        private TextView rewardName;
        private TextView distance;
        private TextView time;
        public RelativeLayout viewBackground;
        public LinearLayout viewForeground;

        CollectionHolder(View itemView) {
            super(itemView);
            this.collectionTitle = itemView.findViewById(R.id.tvTitle_collection);
            this.collectionAbbreviation = itemView.findViewById(R.id.tvAbbreviation_collection);
            this.imgThumb = itemView.findViewById(R.id.img_thumb_collection);
            this.expandArrow = itemView.findViewById(R.id.expandArrow_collection);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
            // expanded views
            expandableLinearLayout = itemView.findViewById(R.id.expandableLayout_collection);
            this.description = itemView.findViewById(R.id.dropdown_description_collection);
            this.age = itemView.findViewById(R.id.collectionAge);
            this.numBadges = itemView.findViewById(R.id.collectionBadges);
            this.rewardWorth = itemView.findViewById(R.id.collectionRewardWorth);
            this.rewardName = itemView.findViewById(R.id.collectionRewardName);
            time = itemView.findViewById(R.id.collectionTime);
            distance = itemView.findViewById(R.id.collectionDistance);

        }

        void setCondensedData(int position) {
            currentObject = collectionList.get(position);

            this.collectionTitle.setText(currentObject.getName());
            this.collectionAbbreviation.setText(currentObject.getAbbreviation());

            ImageManager imageManager = new ImageManager();
            imageManager.fillSuperBadgeImage(context, currentObject, imgThumb);
            distance.setText(""+currentObject.getmDistance());
            time.setText(""+currentObject.getTime());


        }//end setCondensedData

        public void setListeners() {
            expandArrow.setOnClickListener(CollectionHolder.this);
            viewForeground.setOnClickListener(CollectionHolder.this);
        }//end setListeners

        void setExpandedData(int position) {
            Hunt currentObject = collectionList.get(position);

            this.description.setText(currentObject.getDescription());
            this.age.setText(currentObject.getAudience()+"+");
            this.numBadges.setText(""+currentObject.getNumBadges());
            this.rewardName.setText(currentObject.getAward().getName());
            this.rewardWorth.setText(currentObject.getAward().getWorth()+"$");
        }//end setExpandedData

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.view_foreground:
                    listener.onCollectionSelected(currentObject.getID(), currentObject.getName());
                    break;

                case R.id.expandArrow_collection:
                    this.expandableLinearLayout.toggle();

                    break;

                default:
                    break;
            }//end switch
        }//end onClick
    }//end inner class: CollectionHolder


}//end CollectionAdapter
