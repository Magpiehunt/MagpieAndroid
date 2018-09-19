package com.davis.tyler.magpiehunt.CMS;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.davis.tyler.magpiehunt.Hunts.Hunt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class CMSCommunicator {
    private static final String TAG = "CMScommunicator";
    //TODO this class should handle parsing the whole response and instead of returning strings
    //TODO return hunt/badge/award objects and lists...

    public LinkedList<Hunt> getAllHunts(JSONArray jsonArray){

        //This list will be type Hunt, left as a string list for now for testing purpose
        LinkedList<String> mEntries = new LinkedList<>();

        //in this hunt each iteration, make a hunt object then add to huntsList
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);//each jsonobject is 1 entire hunt data chunk
                mEntries.add(jsonObject.toString());
                Log.e(TAG, "adding: "+jsonObject.getString("name"));
            }
            catch(JSONException e) {
                mEntries.add("Error: " + e.getLocalizedMessage());
            }
        }
        return null;
    }

    //This wont work for previously created hunts, darci fixed it for only new hunts
    public String getHuntsByState(String state){
        HttpRequests request = new HttpRequests();
        if(state.length() != 2)
            return null;
        try {
            return request.execute("http://206.189.204.95/api/v3/hunts/state/"+state).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //This wont work for previously created hunts, darci fixed it for only new hunts
    public String getHuntsByZip(String zip){
        HttpRequests request = new HttpRequests();
        try {
            return request.execute("http://206.189.204.95/api/v3/hunts/zipcode/"+zip).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getHuntsByCity(String city){
        HttpRequests request = new HttpRequests();
        try {
            return request.execute("http://206.189.204.95/api/v3/hunts/city/"+city).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO make this return a bitmap image asap
    public String getLandmarkImage(String filename){
        HttpRequests request = new HttpRequests();
        try {
            return request.execute("http://206.189.204.95/landmark/"+filename).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO make this return a bitmap image asap
    public String getBadgeImage(String filename){
        HttpRequests request = new HttpRequests();
        try {
            return request.execute("http://206.189.204.95/badge/"+filename).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO make this return a bitmap image asap
    public String getSuperBadgeImage(String filename){
        HttpRequests request = new HttpRequests();
        try {
            return request.execute("http://206.189.204.95/superbadge/"+filename).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
