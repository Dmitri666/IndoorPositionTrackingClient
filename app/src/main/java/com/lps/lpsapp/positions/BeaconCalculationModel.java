package com.lps.lpsapp.positions;

import com.lps.lpsapp.viewModel.chat.BeaconInRoom;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by dle on 06.09.2016.
 */
public class BeaconCalculationModel {
    private HashMap<Integer,RangedBeacon> beacons;
    private HashSet<TrippleGroup> trippleGroups;
    private HashSet<DubbleGroup> dubbleGroups;

    public BeaconCalculationModel(HashSet<TrippleGroup> tripples,HashSet<DubbleGroup> dobleGroups,HashMap<Integer,RangedBeacon> beacons) {
        this.beacons = new HashMap<>();
        this.trippleGroups = new HashSet<>();
        this.dubbleGroups = new HashSet<>();

        this.trippleGroups.addAll(tripples);
        this.dubbleGroups.addAll(dobleGroups);

        for(Integer id3:beacons.keySet()) {
            RangedBeacon rb = new RangedBeacon(beacons.get(id3));
            this.beacons.put(id3,rb);
        }
    }
}
