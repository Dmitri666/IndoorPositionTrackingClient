package com.lps.lpsapp.altbeacon;

import android.util.Log;

import org.altbeacon.beacon.distance.CurveFittedDistanceCalculator;
import org.altbeacon.beacon.distance.DistanceCalculator;

/**
 * Created by user on 21.02.2016.
 */
public class DefaultDistanceCalculator extends CurveFittedDistanceCalculator {
    private static String TAG = "DefaultDistanceCalculator";

    private double c1;
    private double c2;
    private double c3;

    public DefaultDistanceCalculator(double coefficient1, double coefficient2, double coefficient3) {
        super(coefficient1,coefficient2,coefficient3);
        this.c1 = coefficient1;
        this.c2 = coefficient2;
        this.c3 = coefficient3;
    }

    @Override
    public double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        Log.d(TAG, "calculating distance based on mRssi of" + rssi + " and txPower of " + txPower);


        double ratio = rssi * 1.0 / txPower;
        double distance;
        if (ratio < 1.0) {
            distance = Math.pow(ratio, 10);
        } else {
            distance = (this.c1) * Math.pow(ratio, this.c2) + this.c3;
        }
        Log.d(TAG, "avg mRssi: " + rssi + " distance: " + distance);
        return distance;
    }

    public double calculateRssi(int txPower, double distance) {
        double rssi;
        if (distance < 1.0) {
            rssi = Math.pow(distance, 0.1) * txPower;
        } else {
            rssi = Math.round(Math.pow(((distance - this.c3) / this.c1), 1.0 / this.c2) * txPower);
        }
        return rssi;
    }
}
