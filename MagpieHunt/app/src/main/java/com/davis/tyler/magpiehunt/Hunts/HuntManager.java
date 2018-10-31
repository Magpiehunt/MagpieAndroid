package com.davis.tyler.magpiehunt.Hunts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.davis.tyler.magpiehunt.Location.LocationTracker;
import com.davis.tyler.magpiehunt.R;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.sort;

public class HuntManager implements Serializable{
    //TODO make sure to write all hunts to app files in OnDestroy (or figure out how to write only changes)
    //TODO all icons are strings to filepath, all getIcon() methods take context as argument.
    private static final String TAG = "HuntManager";
    private HashMap<Integer, Hunt> mHunts;
    private Badge mFocusBadge; /*since this object is shared among fragments, set focus badge from
    any fragment, then from another fragment, get focusbadge, so that we dont have to have fragments talk
    to eachother, they can only talk to hunt manager
    */
    private Hunt mFocusHunt;
    private Award mFocusAward;
    private Set<Hunt> mSelectedHunts;
    private LinkedList<Hunt> mSearchHunts;
    private LinkedList<Badge> mSearchBadges;
    private LinkedList<Hunt> mSearchNearbyHunts;
    private boolean huntsFilterUpdated;


    public HuntManager(Context context){
        Log.e(TAG, "Constructing new hunt manager object");
        mHunts = new HashMap<>();
        huntsFilterUpdated = false;
        mSelectedHunts = new HashSet<>();
        mSearchHunts = new LinkedList<>();
        mSearchNearbyHunts = new LinkedList<>();
        //addTestHunt(context);//this is a temporary method to make up local hunts
        for(Hunt h: getAllHunts()){
            if(h.isFocused()) {
                mSelectedHunts.add(h);
                mFocusHunt = h;
            }
        }

    }


    public void getSortedHuntsByDistance(LinkedList<Hunt> hunts)
    {
        HuntSorter.sortByShortestPath(hunts);
    }
    public void getSortedHuntsByNumberOfBadges(LinkedList<Hunt> hunts)
    {
        HuntSorter.sortHuntsNumberofBadges(hunts);
    }
    public void getSortedHuntsByClosestBadge(LinkedList<Hunt> hunts, LocationTracker lt)
    {
        HuntSorter.sortHuntsByBadgeClosest(hunts, lt);
    }
    public void addHunt(Hunt hunt){
        if(mHunts.get(hunt.getID()) == null)
            mHunts.put(hunt.getID(), hunt);
    }


    public Hunt getHuntByID(int id){
        return mHunts.get(id);
        /*LinkedList<Hunt> ll = new LinkedList<>();
        Iterator it = mHunts.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            if(((Hunt)pair.getValue()).getID() == id)
                return((Hunt)pair.getValue());
        }
        return null;*/
    }
    public LinkedList<Hunt> getAllUnCompletedHunts(){
        LinkedList<Hunt> ll = new LinkedList<>();
        Iterator it = mHunts.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            if(!((Hunt)pair.getValue()).getIsCompleted())
                ll.add(((Hunt)pair.getValue()));
        }
        return ll;
    }
    public LinkedList<Hunt> getAllCompletedHunts(){
        LinkedList<Hunt> ll = new LinkedList<>();
        Iterator it = mHunts.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            if(((Hunt)pair.getValue()).getIsCompleted())
                ll.add(((Hunt)pair.getValue()));
        }
        return ll;
    }
    public LinkedList<Hunt> getAllHunts(){
        LinkedList<Hunt> ll = new LinkedList<>();
        Iterator it = mHunts.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            ll.add(((Hunt)pair.getValue()));
        }
        return ll;
    }

    public LinkedList<Hunt> getAllDownloadedHunts(){
        System.out.println("saving: hunt size: "+mHunts.size());
        LinkedList<Hunt> ll = new LinkedList<>();
        Iterator it = mHunts.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Hunt h = ((Hunt)pair.getValue());
            System.out.println("saving: looking at hunt: "+h.getName());
            if(h.getIsDownloaded())
                ll.add((Hunt)pair.getValue());
        }
        return ll;
    }
    public LinkedList<Hunt> getAllDownloadedUndeletedHunts(){
        LinkedList<Hunt> ll = new LinkedList<>();
        Iterator it = mHunts.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Hunt h = ((Hunt)pair.getValue());
            if(h.getIsDownloaded() && !h.getIsDeleted())
                ll.add((Hunt)pair.getValue());
        }
        return ll;
    }
    public Badge getBadgeByID(int id){
        Iterator it = mHunts.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            if(((Hunt)pair.getValue()).getBadge(id) != null){
                return ((Hunt)pair.getValue()).getBadge(id);
            }

        }
        return null;
    }

    public void setmSearchBadges(LinkedList<Badge> ll){
        mSearchBadges = ll;
    }
    public void setFocusAward(int huntID){
        mFocusAward = mHunts.get(huntID).getAward();
    }
    public void setFocusBadge(int badgeID){
        mFocusBadge = getBadgeByID(badgeID);
    }
    /*public void setmFocusHunt(int huntID){
        mFocusHunt = mHunts.get(huntID);
    }*/
    public void setmSearchNearbyHunts(LinkedList<Hunt> ll){
        mSearchNearbyHunts = ll;
    }

    public LinkedList<Hunt> getmSearchNearbyHunts() {
        return mSearchNearbyHunts;
    }

    public Award getFocusAward(){return mFocusAward;}
    public Badge getFocusBadge(){return mFocusBadge;}
    public Hunt getFocusHunt(){return mFocusHunt;}
    public LinkedList<Badge> getAllBadges(){
        LinkedList<Hunt> ll = new LinkedList<>();
        LinkedList<Badge> bl = new LinkedList<>();
        Iterator it = mHunts.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            bl.addAll(((Hunt)pair.getValue()).getAllBadges());

        }
        return bl;
    }

    public Set<Hunt> getSelectedHunts(){
        return mSelectedHunts;
    }
    public void removeUndownloadedSelectedHunts(){
        LinkedList<Hunt> toDelete = new LinkedList<>();
        for(Hunt h: mSelectedHunts){
            if(!h.getIsDownloaded()){
                h.setIsFocused(false);
                toDelete.add(h);
            }
        }
        mSelectedHunts.removeAll(toDelete);
    }
    public Set<Hunt> getSelectedDownloadedHunts(){
        removeUndownloadedSelectedHunts();
        return mSelectedHunts;
    }
    public int getSelectedHuntsSize(){return mSelectedHunts.size();}
    public void setFocusHunt(int huntID){
        for(Hunt hunt:mSelectedHunts)
            hunt.setIsFocused(false);
        mSelectedHunts.clear();
        Hunt h = mHunts.get(huntID);
        mFocusHunt = h;
        h.setIsFocused(true);
        mSelectedHunts.add(h);
    }
    public boolean getHuntsFilterUpdated(){return huntsFilterUpdated;}
    public void setHuntsFilterUpdated(boolean b){huntsFilterUpdated = b;}

    public List<Badge> getSortedBadges()
    {
        List<Badge> sortedBadges = new LinkedList<>();
        List<Hunt> hunts = this.getAllUnCompletedHunts();
        List<Badge> toAdd;

        for(int i = 0; i < hunts.size(); i++)
        {
            toAdd = hunts.get(i).getAllBadges();
            for(int j = 0; j < toAdd.size(); j++)
            {
                sortedBadges.add(toAdd.get(j));
            }
        }
        sort(sortedBadges);
        return sortedBadges;
    }

    public List<Badge> getFocusedSortedBadges()
    {
        LinkedList<Badge> ll = new LinkedList<>();
        Iterator it = mSelectedHunts.iterator();
        while (it.hasNext()) {
            Hunt h = (Hunt)it.next();
            ll.addAll((h).getAllBadges());

        }
        sort(ll);
        return ll;
    }

    public LinkedList<Badge> getmSearchBadges() {
        return mSearchBadges;
    }

    public Hunt getSingleSelectedHunt(){
        Iterator it = mSelectedHunts.iterator();
        while (it.hasNext()) {
            Hunt h = (Hunt)it.next();
            return h;

        }
        return null;
    }

    public void deleteHuntByID(int id){
        System.out.println("Deleting hunt: "+id);
        mSelectedHunts.remove(mHunts.get(id));
        mHunts.remove(id);
    }

    public void updateFocusHuntDeleted(){
        LinkedList<Hunt> ll = new LinkedList<>();
        for(Hunt h: mSelectedHunts){
            if(h.getIsDeleted())
                ll.add(h);
        }
        for(Hunt h: ll){
            mSelectedHunts.remove(h);
        }
    }
    public void setmSearchHunts(LinkedList<Hunt> ll){
        mSearchHunts = ll;
    }

    public LinkedList<Hunt> getmSearchHunts() {
        return mSearchHunts;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public void setAllHuntsFocused()
    {
        for(Hunt h: getAllHunts()){
            if(h.isFocused()) {
                mSelectedHunts.add(h);
                mFocusHunt = h;
            }
        }
    }

    public void addAllHunts(LinkedList<Hunt> ll){
        for(Hunt h: ll){
            if(mHunts.get(h.getID()) == null)
                mHunts.put(h.getID(), h);
        }
    }

    public LinkedList<Badge> getAllBadgesByKeyword(String key)
    {
        LinkedList<Badge> ret = new LinkedList<>();
        LinkedList<Hunt> ll = null;
        //if(both)
            ll = this.getAllDownloadedUndeletedHunts();
        /*else if(completedHunt)
            ll = this.getAllCompletedHunts();
        else
            ll = this.getAllUnCompletedHunts();*/

        for(Hunt h : ll)
        {
            LinkedList<Badge> bList = h.getAllBadges();
            for(int x = 0; x < bList.size(); x++)
            {
                Badge b = bList.get(x);

                if(b.getName().toLowerCase().contains(key.toLowerCase()))
                    ret.add(b);
                else if(b.getLandmarkName() != null)
                {
                    if(b.getLandmarkName().toLowerCase().contains(key.toLowerCase()))
                        ret.add(b);
                }
            }
        }



        return ret;
    }
}
