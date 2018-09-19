package com.davis.tyler.magpiehunt.CMS;

import com.davis.tyler.magpiehunt.Hunts.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Date;

public class JSONParser
{
    JSONArray array;

    public JSONParser(JSONArray jArray)
    {
       this.array = jArray;
    }

    //call for single and multiple hunts
    public LinkedList<Hunt> getAllHunts()
    {
        System.out.println("test ");
        LinkedList<String> mEntries = new LinkedList();
        LinkedList<Hunt> huntList = new LinkedList<Hunt>();

        for(int i = 0; i < this.array.length(); i++)
        {
            try
            {
                JSONObject jObj = this.array.getJSONObject(i);
                mEntries.add(jObj.toString());
                System.out.println("test "+jObj.toString());
                huntList.add(parseHunt(jObj));
            }
            catch(JSONException e)
            {
                mEntries.add("Error:" + e.getLocalizedMessage());
            }
        }

        return huntList;
    }


    public Hunt parseHunt(JSONObject jObj)
    {
        Hunt hunt = new Hunt();
        HashMap<Integer, Badge> badgesList = new HashMap<Integer, Badge>();
        Award huntAward = new Award();
        try {
            //status, audience, superBadge, locationID fields are not in Hunt object
            /*Date dateEnd = parseDate(jObj.getString("date_end"));
            Date dateStart = parseDate(jObj.getString("date_start"));*/
            JSONObject huntObj = jObj.getJSONObject("hunt");
            hunt.setID(huntObj.getInt("huntId"));
            hunt.setDescription(huntObj.getString("summary"));
            hunt.setAbbreviation(huntObj.getString("abbreviation"));
            hunt.setIsAvailable(huntObj.getBoolean("available"));
            hunt.setName((huntObj.getString("name")));
            hunt.setmAudience(huntObj.getString("audience"));
            huntAward.setSuperBadgeIcon(huntObj.getString("superBadge"));
            hunt.setmIsDeleted(false);
            hunt.setmIsDownloaded(false);
            /*hunt.setmDateStart(dateStart);
            hunt.setmDateEnd(dateEnd);*/
            hunt.setmSponsor(huntObj.getString("sponsor"));

            System.out.println("name: "+hunt.getName()+"abbr: "+hunt.getAbbreviation());
            JSONObject huntLocation = huntObj.getJSONObject("huntLocation");
            hunt.setmCity(huntLocation.getString("city"));
            hunt.setmState(huntLocation.getString("state"));

            String zip = huntLocation.getString("zipcode");
            hunt.setmZip(Integer.parseInt(zip));

            hunt.setmIsCompleted(false);



            JSONArray badges = huntObj.getJSONArray("badges");
            for(int i = 0; i < badges.length(); i++)
            {
                Badge badge = new Badge();
                badge.setmIsCompleted(false);
                badge.setmName(badges.getJSONObject(i).getString("name"));
                badge.setmDescription(badges.getJSONObject(i).getString("description"));
                badge.setmLandmarkImage(badges.getJSONObject(i).getString("landmarkImage"));
                badge.setQRurl(badges.getJSONObject(i).getString("qrCode"));
                System.out.println("Made badge: "+badge.getName());
                String lat = badges.getJSONObject(i).getString("lat");
                String lon = badges.getJSONObject(i).getString("lon");
                badge.setmLatitude(Double.parseDouble(lat));
                badge.setmLongitude(Double.parseDouble(lon));

                badge.setmBadgeIcon(badges.getJSONObject(i).getString("icon"));
                badge.setmID(badges.getJSONObject(i).getInt("badge_id"));

                badgesList.put(badge.getID(), badge);
            }

            try {
                JSONObject award = huntObj.getJSONObject("award");
                huntAward.setmID(award.getInt("awardId"));

                huntAward.setIsNew(false);
                huntAward.setmName((award.getString("awardName")));
                huntAward.setmDescription(award.getString("awardDescription"));
                huntAward.setmWorth(Integer.parseInt(award.getString("worth")));
                huntAward.setmLocationDescription(award.getString("address"));
                huntAward.setmTerms(award.getString("terms"));
                huntAward.setmValue(Integer.parseInt(award.getString("hasValue")));
                System.out.println("Award set, name: "+huntAward.getName());
            }catch(Exception e){
                System.out.println("award error");
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        hunt.setmBadges(badgesList);
        hunt.setmAward(huntAward);
        return hunt;
    }

    public Date parseDate(String inputDate)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try
        {
            date = formatter.parse(inputDate);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }
}
