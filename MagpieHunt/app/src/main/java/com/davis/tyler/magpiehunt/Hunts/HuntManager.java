package com.davis.tyler.magpiehunt.Hunts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

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
    private boolean huntsFilterUpdated;


    public HuntManager(Context context){
        Log.e(TAG, "Constructing new hunt manager object");
        mHunts = new HashMap<>();
        huntsFilterUpdated = false;
        parseDownloadedHunts();
        mSelectedHunts = new HashSet<>();
        //addTestHunt(context);//this is a temporary method to make up local hunts
        for(Hunt h: getAllHunts()){
            if(h.isFocused()) {
                mSelectedHunts.add(h);
                mFocusHunt = h;
            }
        }

    }

    /*public void addTestHunt(Context context){
        HashMap<Integer, Badge> badges = new HashMap<>();
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.example_badge);
        //Bitmap landmarkpic = BitmapFactory.decodeResource(context.getResources(), R.drawable.ewu);
        String descb1 = "Eastern Washington University was established in 1882 by a $10,000 grant from expressman Benjamin Pierce Cheney, and was originally known as Benjamin P. Cheney Academy to honor its founder. In 1889 the school was renamed State Normal School at Cheney and in 1937 to Eastern Washington College of Education. The campus was almost totally destroyed twice by fire in 1891 and 1912, but was rebuilt each time, and grew quickly in size following World War II. The school became Eastern Washington State College. During this era, Eastern added various graduate and undergraduate degree programs. In 1977, the school's name was changed for the final time to Eastern Washington University by the Washington State Legislature.[15]\n" +
                "\n" +
                "In 1992, the core of the campus was listed on the National Register of Historic Places as the Washington State Normal School at Cheney Historic District.[16][17]";
        Badge badge1 = new Badge(0, descb1,"filepathhere", "landmark1",
                47.4873895,-117.5778622, "badge1", false, 0, "filepathhere");
        Badge badge2 = new Badge(1, "testbadge2","filepathhere", "landmark2",
                47.4873897,-117.5757624, "badge2", false, 0, "filepathhere");
        Badge badge3 = new Badge(2, "testbadge3","filepathhere", "landmark3",
                47.4862000,-117.5757111, "badge3", false, 0, "filepathhere");

        badge3.setQRurl("hello");
        //Bitmap bitmapSuperBadge = BitmapFactory.decodeResource(context.getResources(), R.drawable.example_super_badge);
        Award award = new Award(0, "2702 AL OGDON WAY CHENEY, WA", "testaward1",
                "award1", "this is my apartment area", 47.4872000,-117.5757111,
                "filepathhere");
        badges.put(badge1.getID(), badge1);
        badges.put(badge2.getID(), badge2);
        badges.put(badge3.getID(), badge3);
        int noOfDays = 14; //i.e two weeks
        Date dateOfStart = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfStart);
        calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
        Date dateOfEnd = calendar.getTime();
        Hunt hunt1 = new Hunt(badges, award, false, 2,"HNT", true,
                dateOfStart, dateOfEnd, "hunt1", false, "summary: this is hunt1",
                "CHENEY", "WA", 99004, "CJ's");
        hunt1.setIsFocused(true);
        hunt1.setmIsCompleted(false);
        mHunts.put(hunt1.getID(), hunt1);
    }*/

    public void getSortedHuntsByDistance(LinkedList<Hunt> hunts)
    {
        Collections.sort(hunts);
    }
    public void addHunt(Hunt hunt){
        mHunts.put(hunt.getID(), hunt);
    }

    public void parseDownloadedHunts(){

    }
    public void saveAllHuntInformation(){

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

    public void setFocusAward(int huntID){
        mFocusAward = mHunts.get(huntID).getAward();
    }
    public void setFocusBadge(int badgeID){
        mFocusBadge = getBadgeByID(badgeID);
    }
    /*public void setmFocusHunt(int huntID){
        mFocusHunt = mHunts.get(huntID);
    }*/
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

    public Hunt getSingleSelectedHunt(){
        Iterator it = mSelectedHunts.iterator();
        while (it.hasNext()) {
            Hunt h = (Hunt)it.next();
            return h;

        }
        return null;
    }

    public void deleteHuntByID(int id){
        mSelectedHunts.remove(mHunts.get(id));
        mHunts.remove(id);
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
            mHunts.put(h.getID(), h);
        }
    }
}
