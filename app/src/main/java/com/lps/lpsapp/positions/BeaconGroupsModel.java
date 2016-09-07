package com.lps.lpsapp.positions;

import com.lps.lpsapp.altbeacon.DefaultDistanceCalculator;
import com.lps.lpsapp.viewModel.chat.BeaconInRoom;
import com.lps.lpsapp.viewModel.chat.BeaconModel;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dle on 28.06.2016.
 */
public class BeaconGroupsModel {
    private float realScaleFactor;
    private float wight;
    private float height;
    private HashMap<Integer,BeaconInRoom> beaconsInRoom;
    private HashSet<TrippleGroup> trippleGroups;
    private HashSet<DubbleGroup> dubbleGroups;

    public BeaconGroupsModel(BeaconModel model) {
        this.height = model.height;
        this.wight = model.wight;
        this.realScaleFactor = model.realScaleFactor;
        this.beaconsInRoom = new HashMap<>();
        this.trippleGroups = new HashSet<>();
        this.dubbleGroups = new HashSet<>();
        for (BeaconInRoom b : model.beacons) {
            this.beaconsInRoom.put(b.id3, b);
        }

        for (int i = 0; i < model.beacons.size(); i++) {
            for (int j = i + 1; j < model.beacons.size(); j++) {
                BeaconInRoom br1 = model.beacons.get(i);
                BeaconInRoom br2 = model.beacons.get(j);
                DubbleGroup group = new DubbleGroup(br1.id3, br2.id3);
                if (!this.dubbleGroups.contains(group)) {
                    this.dubbleGroups.add(group);
                }
            }
        }

        for (int i = 0; i < model.beacons.size(); i++) {
            for (int j = i + 1; j < model.beacons.size(); j++) {
                for (int k = j + 1; k < model.beacons.size(); k++) {
                    BeaconInRoom br1 = model.beacons.get(i);
                    BeaconInRoom br2 = model.beacons.get(j);
                    BeaconInRoom br3 = model.beacons.get(k);
                    TrippleGroup group = new TrippleGroup(br1.id3, br2.id3, br3.id3);
                    if (!this.trippleGroups.contains(group)) {
                        if (this.Validate(group)) {
                            this.trippleGroups.add(group);
                        }
                    }
                }
            }
        }
    }

    private Boolean Validate(TrippleGroup group) {
        float xDelta = this.wight / 10;
        float yDelta = this.height / 10;

        List<Float> x = new ArrayList<>();
        List<Float> y = new ArrayList<>();

        float mX = 0;
        float mY = 0;


        for (Integer id3: group.getGroupIds()) {
            BeaconInRoom rb = this.beaconsInRoom.get(id3);
            x.add(rb.x);
            y.add(rb.y);
        }

        for (int i = 0; i < x.size(); i++) {
            mX += x.get(i);
        }
        mX = mX / x.size();
        for (int i = 0; i < y.size(); i++) {
            mY += y.get(i);
        }
        mY = mY / x.size();

        double diffX = 0;

        for (int i = 0; i < x.size(); i++) {
            double diff = Math.abs(mX - x.get(i));
            if (diff > diffX) {
                diffX = diff;
            }

        }

        double diffY = 0;
        for (int i = 0; i < y.size(); i++) {
            double diff = Math.abs(mY - y.get(i));
            if (diff > diffY) {
                diffY = diff;
            }

        }


        if (diffX < xDelta || diffY < yDelta) {
            return false;
        }

        return true;

    }

    public float getRealScaleFactor() {
        return this.realScaleFactor;
    }

    public float getWight() {
        return this.wight;
    }

    public float getHeight() {
        return this.height;
    }



    public BeaconCalculationModel getCalculationModel(Collection<Beacon> beacons) {

        HashMap<Integer, Double> distances = new HashMap<>();
        HashMap<Integer,RangedBeacon> rangedBeacons = new HashMap<>();
        for (Beacon beacon : beacons) {
            Integer id3 = beacon.getId3().toInt();
            distances.put(id3, beacon.getDistance());
            DefaultDistanceCalculator dc = (DefaultDistanceCalculator)Beacon.getDistanceCalculator();
            double rssi = dc.calculateRssi(beacon.getTxPower(),beacon.getDistance());
            BeaconInRoom bir = this.beaconsInRoom.get(id3);
            if(bir != null) {
                RangedBeacon rb = new RangedBeacon(bir,beacon.getTxPower(), rssi);
                rangedBeacons.put(id3, rb);
            }

        }
        HashSet<TrippleGroup> tripples = this.getMatchedTrippleGroups(distances.keySet());
        HashSet<DubbleGroup> dobleGroups = this.getMatchedDobleGroups(distances.keySet());

        BeaconCalculationModel calculationModel = new BeaconCalculationModel(tripples,dobleGroups,rangedBeacons);


        return calculationModel;
    }

    private HashSet<TrippleGroup> getMatchedTrippleGroups(Set<Integer> keys) {
        HashSet<TrippleGroup> subSet = new HashSet<>();
        for (TrippleGroup group : this.trippleGroups) {
            if (keys.containsAll(group.getGroupIds())) {
                subSet.add(group);
            }
        }
        return subSet;
    }

    private HashSet<DubbleGroup> getMatchedDobleGroups(Set<Integer> keys) {
        HashSet<DubbleGroup> subSet = new HashSet<>();
        for (DubbleGroup group : this.dubbleGroups) {
            if (keys.containsAll(group.getGroupIds())) {
                subSet.add(group);
            }
        }
        return subSet;
    }


    private class BeaconGroupComporator implements Comparator<BeaconGroup> {
        HashMap<Integer, Double> distances;

        public BeaconGroupComporator(HashMap<Integer, Double> distances) {
            this.distances = distances;
        }

        @Override
        public int compare(BeaconGroup source, BeaconGroup target) {
            float sourceDistance = source.getSummDistance(distances);
            float targetDistance = target.getSummDistance(distances);
            if (sourceDistance < targetDistance) {
                return -1;
            } else if (sourceDistance > targetDistance) {
                return 1;
            }

            return 0;

        }

        @Override
        public boolean equals(Object o) {

            return false;
        }
    }
}
