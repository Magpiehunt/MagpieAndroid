package com.davis.tyler.magpiehunt.Hunts;

import android.location.Location;

import com.davis.tyler.magpiehunt.Location.LocationTracker;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Hunt implements Serializable {
    public static final int AGE_ALL = 0;
    public static final int AGE_TEEN = 1;
    public static final int AGE_YOUNG_ADULT = 2;
    public static final int AGE_MATURE = 3;
    private HashMap<Integer, Badge> mBadges;
    private Award mAward;
    private boolean mIsCompleted;
    private int mID;
    private String mAbbreviation;
    private boolean mIsAvailable;
    private String mDateStart;
    private String mDateEnd;
    private String mName;
    private boolean mIsOrdered;
    private String mDescription;
    private String mCity;
    private String mState;
    private int mZip;
    private String mSponsor;
    private String mRating;
    private boolean mIsFocused;
    private int mAudience;
    private boolean mIsDeleted;
    private boolean mIsDownloaded;
    private double mDistance;

    public Hunt(HashMap<Integer, Badge> badges, Award award, boolean isCompleted, int id,
                String abbreviation, boolean isAvailable, String start, String end, String name,
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
    public void setIsAvailable(boolean b){ mIsAvailable = b; }
    public void setmDateStart(String d){ mDateStart = d; }
    public void setmDateEnd(String d){ mDateEnd = d; }
    public void setmSponsor(String s){ mSponsor = s; }
    public void setmCity(String s){ mCity = s; }
    public void setmState(String s){ mState = s; }
    public void setmZip(int i){ mZip = i; }
    public void setmAward(Award a){ this.mAward = a; }
    public void setmBadges(HashMap<Integer, Badge> b){ this.mBadges = b; }
    public void setmIsCompleted(boolean b){mIsCompleted = b;}
    public void updateIsCompleted(){
        LinkedList<Badge> ll = getAllBadges();
        setmIsCompleted(true);
        for(Badge b: ll){
            if(!b.getIsCompleted()){
                setmIsCompleted(false);
            }
        }
        //System.out.println("setting award: "+this+" to true");
        mAward.setIsNew(true);
    }
    public void setmIsDeleted(boolean b){mIsDeleted = b;}
    public void setmIsDownloaded(boolean b){mIsDownloaded = b;}
    public void setmAudience(String s){
        if(s.equalsIgnoreCase("all")){
            mAudience = 4;
        }
        else if(s.equalsIgnoreCase("teen")){
            mAudience = 13;
        }
        else if(s.equalsIgnoreCase("young")){
            mAudience = 21;
        }
        else if(s.equalsIgnoreCase("mature")){
            mAudience = 21;
        }
    }
    public void setmAudience(int x)
    {
        this.mAudience = x;
    }

    public boolean getIsDownloaded(){return mIsDownloaded;}
    public int getAudience(){
        return mAudience;
    }
    public boolean getIsDeleted(){return mIsDeleted;}
    public boolean isFocused(){return mIsFocused;}
    public int getAwardID(){return mAward.getID();}
    public Badge getBadge(int id){return mBadges.get(id);}
    public boolean getIsCompleted(){return mIsCompleted;}
    public int getID(){return mID;}
    public String getAbbreviation(){return mAbbreviation;}
    public boolean getIsAvailable(){return mIsAvailable;}
    public String getDateStart(){return mDateStart;}
    public String getDateEnd(){return mDateEnd;}
    public String getName(){return mName;}
    public boolean getIsOrdered(){return mIsOrdered;}
    public String getDescription(){return mDescription;}
    public String getCity(){return mCity;}
    public String getState(){return mState;}
    public int getZip(){return mZip;}
    public String getSponsor(){return mSponsor;}
    public String getRating(){return mRating;}
    public int getNumBadges(){return mBadges.size();}
    public Award getAward(){return mAward;}
    public LinkedList<Badge> getAllUncompletedBadges(){
        LinkedList<Badge> ll = new LinkedList();
        //System.out.println("num badges: "+mBadges.size());
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
    public double getClosestBadgeLocation(LocationTracker lt)
    {
        double min = 1000000.00;

        LinkedList<Badge> badges = getAllBadges();
        for(int x = 0; x < badges.size(); x++)
        {
            Badge b = badges.get(x);
            Location temp = new Location("temp");
            temp.setLatitude(b.getLatitude());
            temp.setLongitude(b.getLongitude());
            double distance = lt.distanceToPoint(temp);
            if(min > distance)
                min = distance;



        }
        return min;
    }
    public LinkedList<Badge> getAllBadges(){
        LinkedList<Badge> ll = new LinkedList();
        //System.out.println("num badges: "+mBadges.size());
        if(mBadges == null)
            return ll;
        Iterator it = mBadges.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
                ll.add(((Badge)pair.getValue()));
        }
        return ll;
    }
    public double getmDistance(){return mDistance;}
    public void setmDistance(double distance){this.mDistance = distance;}
    public double getTime(){return round((getmDistance() / 2.6) + 3.0/60 * getAllBadges().size(), 1);}

    public double getDistanceBetweenBadges(Badge b1, Badge b2)
    {
        Location loc1 = new Location("b1");
        loc1.setLatitude(b1.getLatitude());
        loc1.setLongitude(b1.getLongitude());
        Location loc2 = new Location("b2");
        loc2.setLatitude(b2.getLatitude());
        loc2.setLongitude(b2.getLongitude());

        return loc1.distanceTo(loc2) * 0.000621371192;
    }

    public void shortestPath()
    {
        LinkedList<Badge> ll = this.getAllBadges();
        int size = ll.size();
        Graph graph = new Graph(this);
        double min = 1000000.00;

        for(int x = 0; x < size; x++)
        {
            double ret = shortestPathHelper(graph, x, 0);
            if(ret < min)
                min = ret;
            graph.resetChecked();
        }


        min = round(min, 1);

        mDistance = min; // CHANGE
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double shortestPathHelper(Graph g, int cur, double min)
    {
        ArrayList<Graph.Edge> edges = g.getEdges(cur);
        Graph.GraphNode toSet = g.getGraphNode(cur);
        toSet.setChecked(true);
        int sink = -1;
        boolean done = true;
        double smallest = 1000000.00;
        Graph.Edge newCur = null;
        //System.out.println("HERE1");
        for(int x = 0; x < edges.size(); x++)
        {
           // System.out.println("HERE2");
            Graph.Edge edge = edges.get(x);
            if(cur == edge.getU())
                sink = edge.getV();
            else
                sink = edge.getU();

            Graph.GraphNode gn = g.getGraphNode(sink);
            if(gn.getChecked() == false) {
                done = false;

                if(smallest > edge.getDistance()) {
                    smallest = edge.getDistance();
                    newCur = edge;
                }
            }
            //System.out.println("HERE3");
        }
        if(done)
            return min;

        int newSink = -1;
        if(newCur.getU() == cur)
            newSink = newCur.getV();
        else
            newSink = newCur.getU();
        //System.out.println("HERE: "+newCur.getDistance());
        return shortestPathHelper(g, newSink, min + newCur.getDistance());
    }
    //***********GRAPH CLASS***************
    public class Graph
    {
        ArrayList<GraphNode> nodes;
        ArrayList<Edge> edges;

        public Graph(Hunt h)
        {
            this.edges = new ArrayList<>();
            LinkedList<Badge> b = h.getAllBadges();
            int size = b.size();
            this.nodes = new ArrayList<>();
            for(int x = 0; x < size; x++)
            {
                this.nodes.add(new GraphNode(x));
            }

            for(int x = 0; x < size; x++)
            {
                for(int y = x+1; y < size; y++)
                {
                    this.edges.add(new Edge(x, y, h.getDistanceBetweenBadges(b.get(x), b.get(y))));
                }
            }
        }
        public Edge getEdge(int vertex1, int vertex2)
        {
            Edge edge = null;
            for(int x = 0; x < this.edges.size(); x++)
            {
                edge = this.edges.get(x);
                int u = edge.getU();
                int v = edge.getV();

                if((vertex1 == u && vertex2 == v) || (vertex2 == u && vertex1 == v))
                    return edge;
            }
            return edge;
        }
        public ArrayList<Edge> getEdges(int vertex)
        {
            ArrayList<Edge> toFind = new ArrayList<>();

            for(int x = 0; x < this.edges.size(); x++)
            {
                Edge edge = this.edges.get(x);
                if(edge.getU() == vertex || edge.getV() == vertex)
                    toFind.add(edge);
            }
            return toFind;
        }
        public GraphNode getGraphNode(int x)
        {
            return this.nodes.get(x);
        }
        public void resetChecked()
        {
            for(GraphNode g: this.nodes)
            {
                g.setChecked(false);
            }
        }
        //***************EDGE CLASS
        public class Edge
        {
            int u;
            int v;
            double distance;

            public Edge(int u, int v, double distance)
            {
                this.u = u;
                this.v = v;
                this.distance = distance;
            }
            public int getU(){return u;}
            public int getV(){return v;}

            public double getDistance() {
                return distance;
            }
        }
        public class GraphNode
        {
            int index;
            boolean checked;

            public GraphNode(int index)
            {
                this.index = index;
                this.checked = false;
            }

            public void setChecked(boolean checked) {
                this.checked = checked;
            }
            public boolean getChecked(){return this.checked;}
            public int getIndex(){return this.index;}

        }


    }


}

