package com.lps.lpsapp.altbeacon;

import org.altbeacon.beacon.distance.DistanceCalculator;

/**
 * Created by dle on 17.02.2016.
 */
public class AvarageDistanceCalculator implements DistanceCalculator {
    @Override
    public double calculateDistance(int txPower, double rssi) {
        return txPower/rssi;
    }
}
