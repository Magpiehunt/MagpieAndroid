package com.davis.tyler.magpiehunt.Hunts;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Hunt implements Serializable {
    private HashMap<Integer, Badge> mBadges;
    private Award mAward;
    private boolean mIsCompleted;
    private int mID;
    private String mAbbreviation;
    private boolean mIsAvailable;
    private Date mDateStart;
    private Date mDateEnd;
    private String mName;
    private boolean mIsOrdered;
    private String mDescription;
    private String mCity;
    private String mState;
    private int mZip;
    private String mSponsor;
    private String mRating;
    private boolean mIsFocused;


    public Hunt(HashMap<Integer, Badge> badges, Award award, boolean isCompleted, int id,
                String abbreviation, boolean isAvailable, Date start, Date end, String name,
                boolean isOrdered, String desc, String city, String state, int zip,
                String sponsor){
        mBadges = badges;
        mAward = award;
        mIsCompleted = isCompleted;
        mID = id;
        mAbbreviation = abbreviation;
        mIsAvailable = isAvailable;
        mDateStart = start;
        mDateEnd = end;
        mName = name;
        mIsOrdered = isOrdered;
        mDescription = desc;
        mCity = city;
        mState = state;
        mZip = zip;
        mSponsor = sponsor;


    }

    public Hunt(){

    }

    public void setIsFocused(boolean b){ mIsFocused = b;}
    public void setID(int id){mID = id;}
    public void setDescription(String s){mDescription = s;}
    public void setName(String s){
        mName = s;
    }
    public void setAbbreviation(String s){
        mAbbreviation = s;
    }


    public boolean isFocused(){return mIsFocused;}
    public int getAwardID(){return mAward.getID();}
    public Badge getBadge(int id){return mBadges.get(id);}
    public boolean getIsCompleted(){return mIsCompleted;}
    public int getID(){return mID;}
    public String getAbbreviation(){return mAbbreviation;}
    public boolean getIsAvailable(){return mIsAvailable;}
    public Date getDateStart(){return mDateStart;}
    public Date getDateEnd(){return mDateEnd;}
    public String getName(){return mName;}
    public boolean getIsOrdered(){return mIsOrdered;}
    public String getDescription(){return mDescription;}
    public String getCity(){return mCity;}
    public String getState(){return mState;}
    public int getZip(){return mZip;}
    public String getSponsor(){return mSponsor;}
    public String getRating(){return mRating;}
    public Award getAward(){return mAward;}
    public LinkedList<Badge> getAllBadges(){
        LinkedList<Badge> ll = new LinkedList();
        if(mBadges == null)
            return ll;
        Iterator it = mBadges.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            if(!((Badge)pair.getValue()).getIsCompleted())
                ll.add(((Badge)pair.getValue()));
        }
        return ll;
    }


}
