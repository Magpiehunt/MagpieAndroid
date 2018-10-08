package com.davis.tyler.magpiehunt.Hunts;

import com.davis.tyler.magpiehunt.Location.LocationTracker;

import java.util.List;

public class HuntSorter
{
    public static void sortHuntsNumberofBadges(List<Hunt> hunts)
    {
        int n = hunts.size();
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (hunts.get(j).getNumBadges() > hunts.get(j+1).getNumBadges())
                {
                    // swap temp and arr[i]
                    Hunt temp = hunts.get(j);
                    hunts.set(j, hunts.get(j+1));
                    hunts.set(j+1, temp);
                }
    }
    public static void sortHuntsByBadgeClosest(List<Hunt> hunts, LocationTracker lt)
    {
        int n = hunts.size();
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                double d1 = hunts.get(j).getClosestBadgeLocation(lt);
                double d2 = hunts.get(j+1).getClosestBadgeLocation(lt);
                if (d1 > d2) {
                    // swap temp and arr[i]
                    Hunt temp = hunts.get(j);
                    hunts.set(j, hunts.get(j + 1));
                    hunts.set(j + 1, temp);
                }
            }
        }
    }
    public static void sortByShortestPath(List<Hunt> hunts)
    {
        int n = hunts.size();
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (hunts.get(j).getmDistance() > hunts.get(j+1).getmDistance())
                {
                    // swap temp and arr[i]
                    Hunt temp = hunts.get(j);
                    hunts.set(j, hunts.get(j+1));
                    hunts.set(j+1, temp);
                }
    }

}
