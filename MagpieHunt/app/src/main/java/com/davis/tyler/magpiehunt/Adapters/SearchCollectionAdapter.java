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
import android.widget.TextView;
import android.widget.Toast;

import com.davis.tyler.magpiehunt.CMS.DownloadImage;
import com.davis.tyler.magpiehunt.FileSystemManager;
import com.davis.tyler.magpiehunt.Fragments.FragmentSearch;
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


/**
 * Created by Blake Impecoven on 1/22/18.
 * TODO:
 * - setup listeners for deletion and expansion of the cards.
 * - tap or swipe to delete?
 * - setup expansion functionality of the cards.
 * - make any corresponding changes to collection_cardn_card.xml.
 * - make any corresponding changes to CollectionModel.java.
 * - setup data set for testing of the cards.
 * - setup dummy data set for testing sooner (waiting on room data).
 * - tweak collection_card.xmlrd.xml font/expansion arrow.
 */

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
        // this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_search_hunt, parent, false);

        return new CollectionHolder(view);
    }//end onCreateViewHolder

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final CollectionHolder holder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

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

        private Button addCollectionBtn;

        public CollectionHolder(View itemView) {
            super(itemView);

            this.collectionTitle = itemView.findViewById(R.id.tvTitle_search);
            this.collectionAbbreviation = itemView.findViewById(R.id.tvAbbreviation_search);
            this.imgThumb = itemView.findViewById(R.id.img_thumb_search);
            this.expandArrow = itemView.findViewById(R.id.expandArrow_search);


            // expanded views
            this.expandableLinearLayout = itemView.findViewById(R.id.expandableLayout_search);
            this.description = itemView.findViewById(R.id.dropdown_description_search);
            this.addCollectionBtn = itemView.findViewById(R.id.button_addCollection_search);
            this.numBadges = itemView.findViewById(R.id.collectionBadges_search);
            this.age = itemView.findViewById(R.id.collectionAge_search);
            this.rewardName = itemView.findViewById(R.id.collectionRewardName);
            this.rewardWorth = itemView.findViewById(R.id.collectionRewardWorth);
        }//end DVC

        void setCondensedData(int position) {
            currentObject = collectionList.get(position);
            System.out.println("querying for superbadge of: "+currentObject.getName());
            Picasso.get().load("http://206.189.204.95/superbadge/image/"+currentObject.getAward().getSuperBadgeIcon()).fit().centerCrop().into(imgThumb);

            this.collectionTitle.setText(currentObject.getName());
            this.collectionAbbreviation.setText(currentObject.getAbbreviation());
            this.imgThumb.setImageResource(R.drawable.magpie_test_cardview_collectionimage);

            // use the following line once images are in the DB. for now, we will use a dummy.
//            this.imgThumb.setImageResource(currentObject.getImage());
//            setListeners(); // uncomment when click functionality implemented.
        }//end setCondensedData

        void setExpandedData(int position) {
            Hunt currentObject = collectionList.get(position);
            this.age.setText(""+currentObject.getAudience()+"+");
            this.description.setText(currentObject.getDescription());
            this.numBadges.setText(""+currentObject.getNumBadges());
            this.rewardName.setText(currentObject.getAward().getName());
            this.rewardWorth.setText(currentObject.getAward().getWorth()+"$");

//            this.rating.setText(currentObject.getRating());
        }//end setExpandedData


        public void setListeners() {
            expandArrow.setOnClickListener(CollectionHolder.this);
            addCollectionBtn.setOnClickListener(CollectionHolder.this);
            /*if(this.expandableLinearLayout.isExpanded())
                this.expandableLinearLayout.toggle();*/
            //TODO: change this listener to respond to a click of the whole card?
            // imgThumb.setOnClickListener(CollectionHolder.this);
            //addBtn.setOnClickListener(CollectionHolder.this);
        }//end setListeners

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.expandArrow_search:
                    this.expandableLinearLayout.toggle();
                    break;

                case R.id.img_thumb_search:
                    //TODO: implement opening the collection (view landmarks)
                    break;

                //TODO: implement deletion below.
//                case delete item:
//                    removeItem(position);
//                    break;
                case R.id.button_addCollection_search:
                    Hunt h = huntManager.getHuntByID(currentObject.getID());
                    if(h == null){
                        Log.e(TAG, "adding hunt: "+currentObject.getName());
                        currentObject.setIsFocused(true);
                        currentObject.setmIsDownloaded(true);
                        huntManager.addHunt(currentObject);
                        huntManager.getSelectedHunts().add(currentObject);
                        listener.onAddHuntListener();
                        addHuntsToFileSystem();
                        //TODO implement this
                        saveImagesToFileSystem(currentObject);

                        //TODO IMPORTANT
                        //wherever there is an img, use picasso to get img from filesystem
                        //if failed, use picasso to query the cms for img

                    }
                    //     break;
                default:
                    break;
            }//end switch
        }//end onClick

        private void addHuntsToFileSystem(){
            FileSystemManager fm = new FileSystemManager();
                        try {
                            fm.addHuntList(listener.getContext(), huntManager.getAllHunts());
                        }catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(listener.getContext(), "Download failed.", Toast.LENGTH_LONG).show();
                        }
                        listener.onAddHuntListener();
        }

        private void saveImagesToFileSystem(Hunt h){
            LinkedList<Badge> badges = h.getAllBadges();

            FileSystemManager fm = new FileSystemManager();
            for(Badge b: badges){
                System.out.println("Attempting to make file for badge: "+b.getName());
                //fm.imageDownload(context, b.getBadgeImageFileName(), "http://206.189.204.95/badge/icon/"+b.getIcon());
                /*try {
                    fm.saveImageToInternalStorage(context, fm.getBitmapFromURL("http://206.189.204.95/badge/icon/" + b.getIcon())
                            , b.getBadgeImageFileName());
                }catch (Exception e){
                    e.printStackTrace();
                }*/
                String src = "http://206.189.204.95/badge/icon/"+b.getIcon();
                try {
                    URL url = new URL(src);
                    new DownloadImage(context, b.getBadgeImageFileName()).execute(url );
                }catch (Exception e){
                    e.printStackTrace();
                }
                src = "http://206.189.204.95/landmark/image/"+b.getLandmarkImage();
                try {
                    URL url = new URL(src);
                    new DownloadImage(context, b.getLandmarkImageFileName()).execute(url );
                }catch (Exception e){
                    e.printStackTrace();
                }

                //fm.imageDownload(context, b.getLandmarkImageFileName(), "http://206.189.204.95/landmark/image/"+b.getLandmarkImage());
            }
            Award award = h.getAward();
            //fm.imageDownload(context, award.getSuperBadgeImageFileName(), "http://206.189.204.95/superbadge/image/"+award.getSuperBadgeIcon());
            String src = "http://206.189.204.95/superbadge/image/"+award.getSuperBadgeIcon();
            try {
                URL url = new URL(src);
                new DownloadImage(context, award.getSuperBadgeImageFileName()).execute(url );
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        // will be used at some point.
        //TODO: decide on gesture or button removal.
        public void removeItem(int position) {
            collectionList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, collectionList.size());
        }//end removeItem

        public void addItem(int position, Hunt currentObject) {
            collectionList.add(position, currentObject);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, collectionList.size());
        }//end addItem

        /*private void addCollectionToDB(Collection c) {
            final MagpieDatabase db = MagpieDatabase.getMagpieDatabase(context);
            db.collectionDao().addCollection(c);
            log.d(TAG, c.getName() + " added to MagpieDatabase");
            ApiService apiService = ServiceGenerator.createService(ApiService.class);

            Call<List<Landmark>> call = apiService.getLandmarks(c.getCID());

            call.enqueue(new Callback<List<Landmark>>() {
                @Override
                public void onResponse(Call<List<Landmark>> call, Response<List<Landmark>> response) {
                    List<Landmark> landmarks = response.body();
                    if (landmarks != null) {
                        for (Landmark l : landmarks) {
                            ImageDownloader imageDownloader = new ImageDownloader();
                            Landmark li = imageDownloader.downloadImage(l);
                            db.landmarkDao().addLandmark(li);
                            log.d(TAG, l.getLandmarkName() + " added to MagpieDatabase");

                        }

                    }

                }

                @Override
                public void onFailure(Call<List<Landmark>> call, Throwable t) {
                    log.e(TAG, "Failed call to add landmarks to DB");
                    log.e(TAG, t.getMessage());

                }
            });

        }*/
    }//end inner class: CollectionHolder


}//end CollectionAdapter
