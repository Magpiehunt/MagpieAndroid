package com.davis.tyler.magpiehunt.CMS;

import com.davis.tyler.magpiehunt.Hunts.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
                Hunt h = parseHunt(jObj);
                if(h != null) {
                    huntList.add(parseHunt(jObj));
                }
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

            String status = huntObj.getString("status");
            if(!status.equalsIgnoreCase("published")){
                return null;
            }

            hunt.setID(huntObj.getInt("huntId"));
            System.out.println("questions1");
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
                badge.setmHuntID(hunt.getID());
                badge.setmName(badges.getJSONObject(i).getString("name"));
                badge.setmDescription(badges.getJSONObject(i).getString("description"));
                badge.setmLandmarkImage(badges.getJSONObject(i).getString("landmarkImage"));
                //badge.setQRurl(badges.getJSONObject(i).getString("qrCode"));
                badge.setQRurl(badge.getName());
                System.out.println("Made badge: "+badge.getName());
                String lat = badges.getJSONObject(i).getString("lat");
                String lon = badges.getJSONObject(i).getString("lon");
                badge.setmLatitude(Double.parseDouble(lat));
                badge.setmLongitude(Double.parseDouble(lon));
                badge.setmLandmarkName(badges.getJSONObject(i).getString("landmarkName"));
                //System.out.println("questions1");
                String str = badges.getJSONObject(i).getString("quiz");
                if(str != null && str.length() > 3) {
                    badge.setQuiz(parseQuizText(badges.getJSONObject(i).getString("quiz")));

                }

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
                huntAward.setmAddress(award.getString("address"));
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

    public Quiz parseQuizText(String quizText) throws JSONException
    {
        int i, size;
        Quiz newQuiz = new Quiz();
        LinkedList<Question> quizQuestions = new LinkedList<Question>();
        JSONObject questionObj;
        try {
            questionObj = new JSONObject(quizText);
        }catch(Exception e){
            return null;
        }


        size = questionObj.getInt("size");
        System.out.println("size: "+size);

        for(i = 0; i < size; i++)
        {
            Question question = new Question();
            String answer;
            LinkedList<String> choices = new LinkedList<String>();
            JSONArray questionArr = questionObj.getJSONArray(i + "");

            //get question
            question.setQuestion(questionArr.getJSONObject(0).getString("question"));
            //get options
            JSONArray optionArray = questionArr.getJSONObject(1).getJSONArray("options");
            for(int j = 0; j < optionArray.length(); j++)
            {
                String s = optionArray.getString(j);
                if(s.length()>0)
                    choices.add(s);
            }


            //get answer
            answer = questionArr.getJSONObject(2).getString("answer");
            choices.add(answer);
            question.setAllChoices(choices);
            question.setAnswer(answer);
            question.setIsCompleted(false);
            quizQuestions.add(question);
        }
        newQuiz.setQuestions(quizQuestions);
        /*for(Question q: quizQuestions){
            System.out.println("question: "+q.getQuestion());
        }*/

        return newQuiz;
    }
}
