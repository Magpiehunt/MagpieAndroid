package com.davis.tyler.magpiehunt.CMS;

import java.util.concurrent.ExecutionException;

public class CMSCommunicator {

    //TODO this class should handle parsing the whole response and instead of returning strings
    //TODO return hunt/badge/award objects and lists...

    public String getAllHunts(){
        HttpRequests request = new HttpRequests();
        try {
            return request.execute("http://206.189.204.95/api/v3/hunts/all").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //This wont work until darci swaps state and zipcode responses..
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

    //This wont work until darci swaps state and zipcode responses..
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
