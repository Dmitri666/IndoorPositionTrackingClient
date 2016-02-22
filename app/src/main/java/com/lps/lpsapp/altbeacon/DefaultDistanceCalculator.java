package com.lps.lpsapp.altbeacon;

import android.util.Log;

import org.altbeacon.beacon.distance.DistanceCalculator;

/**
 * Created by user on 21.02.2016.
 */
public class DefaultDistanceCalculator implements DistanceCalculator {
    private static String TAG = "DefaultDistanceCalculator";
    @Override
    public double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        Log.d(TAG, "calculating distance based on mRssi of" + rssi + " and txPower of " + txPower);


        double ratio = rssi*1.0/txPower;
        double distance;
        if (ratio < 1.0) {
            distance =  Math.pow(ratio,10);
        }
        else {
            distance =  (0.42093)*Math.pow(ratio,6.9476) + 0.54992;
        }
        Log.d(TAG, "avg mRssi: " + rssi + " distance: " + distance);
        return distance;
    }
}
