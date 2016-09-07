package com.lps.lpsapp.positions;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by dle on 06.09.2016.
 */
public class BeaconCalculationModel {
    public HashMap<Integer,RangedBeacon> beacons;
    public TreeSet<TrippleGroup> trippleGroups;
    public HashSet<DubbleGroup> dubbleGroups;

    public BeaconCalculationModel(HashSet<TrippleGroup> tripples,HashSet<DubbleGroup> dobleGroups,HashMap<Integer,RangedBeacon> beacons) {
        this.beacons = new HashMap<>();
        this.trippleGroups = new TreeSet<>(new TrippleGroupComporator());
        this.dubbleGroups = new HashSet<>();
        this.beacons = beacons;
        this.trippleGroups.addAll(tripples);
        this.dubbleGroups.addAll(dobleGroups);
    }

    public Path getPath(TrippleGroup group) {
        Path path1 = new Path();
        List<Integer> ids = new ArrayList<>(group.getGroupIds());
        Integer id3 = ids.get(0);
        RangedBeacon rb = this.beacons.get(id3);
        path1.moveTo(rb.getX(), rb.getY());

        id3 = ids.get(1);
        rb = this.beacons.get(id3);
        path1.lineTo(rb.getX(), rb.getY());

        id3 = ids.get(2);
        rb = this.beacons.get(id3);
        path1.lineTo(rb.getX(), rb.getY());

        id3 = ids.get(0);
        rb = this.beacons.get(id3);
        path1.lineTo(rb.getX(), rb.getY());
        path1.close();

        return path1;
    }

    private double getSummRssi(TrippleGroup group) {
        double sumRssi = 0;
        for (Integer id3:group.getGroupIds()) {
            RangedBeacon rb = this.beacons.get(id3);
            sumRssi += rb.getAvrRssi();
        }
        return sumRssi;
    }

    private class TrippleGroupComporator implements Comparator<TrippleGroup> {

        public TrippleGroupComporator() {

        }

        @Override
        public int compare(TrippleGroup source, TrippleGroup target) {
            double sourceDistance = getSummRssi(source);
            double targetDistance = getSummRssi(target);
            if (sourceDistance < targetDistance) {
                return 1;
            } else if (sourceDistance > targetDistance) {
                return -1;
            }

            return 0;

        }

        @Override
        public boolean equals(Object o) {

            return false;
        }
    }
}
