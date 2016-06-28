package com.lps.lpsapp.positions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dle on 28.06.2016.
 */
public class BeaconGroupPoints extends HashMap<Integer,BeaconPoint> {


    public Boolean IsValide() {
        List<Float> x = new ArrayList<>();
        List<Float> y = new ArrayList<>();

        double mX = 0;
        double mY = 0;



        for(BeaconPoint point:this.values()) {
            x.add(point.getX());
            y.add(point.getY());
        }

        for(int i = 0;i < x.size();i++) {
            mX += x.get(i);
        }
        mX = mX / x.size();
        for(int i = 0;i < y.size();i++) {
            mY += y.get(i);
        }
        mY = mY / x.size();

        double diffX = 0;

        for(int i = 0;i < x.size();i++) {
            double diff = Math.abs(mX - x.get(i));
            if(diff > diffX) {
                diffX = diff;
            }

        }

        double diffY = 0;
        for(int i = 0;i < y.size();i++) {
            double diff = Math.abs(mY - y.get(i));
            if(diff > diffY) {
                diffY = diff;
            }

        }


        if(diffX < 20 || diffY < 20 ) {
            return false;
        }

        return true;

    }

    public float getSummDistance(HashMap<Integer,Double> distances) {
        float distance = 0;
        for(Integer key:this.keySet()) {
            if(distances.containsKey(key)) {
                distance += distances.get(key);
            }
        }
        return distance;
    }
}
