package com.lps.lpsapp.altbeacon;

import android.util.Log;

import org.altbeacon.beacon.distance.DistanceCalculator;

/**
 * Created by dle on 17.02.2016.
 */
public class AvarageDistanceCalculator implements DistanceCalculator {
    private static String TAG = "DefaultDistanceCalculator";
    @Override
    public double calculateDistance(int txPower, double rssi) {
        double distance = rssi * 1.0 /txPower;
        Log.d(TAG, "avg mRssi: " + rssi + " distance: " + distance);
        return distance;
    }

    public int calculateRssi(int txPower, double distance) {
        int rssi = (int)distance * txPower;
        return rssi;
    }
}
