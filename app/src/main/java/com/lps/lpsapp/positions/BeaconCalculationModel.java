package com.lps.lpsapp.positions;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by dle on 06.09.2016.
 */
public class BeaconCalculationModel {
    private HashMap<Integer,RangedBeacon> beacons;
    private HashSet<TrippleGroup> trippleGroups;
    private HashSet<DubbleGroup> dubbleGroups;

    public BeaconCalculationModel() {
        this.beacons = new HashMap<>();
        this.trippleGroups = new HashSet<>();
        this.dubbleGroups = new HashSet<>();
    }
}
