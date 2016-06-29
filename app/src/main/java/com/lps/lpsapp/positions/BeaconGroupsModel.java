package com.lps.lpsapp.positions;

import com.lps.lpsapp.viewModel.chat.BeaconModel;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by dle on 28.06.2016.
 */
public class BeaconGroupsModel extends HashMap<BeaconGroupKey,BeaconGroupPoints> {
    private float realScaleFactor;
    private float wight;
    private float height;

    public BeaconGroupsModel(BeaconModel model) {
        this.height = model.height;
        this.wight = model.wight;
        this.realScaleFactor = model.realScaleFactor;
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

    public List<BeaconData> getCalculationModel(Collection<Beacon> beacons) {
        ArrayList<BeaconData> result = new ArrayList<>();
        if(beacons.size() == 0) {
            return result;
        }

        if(beacons.size() == 1) {
            for(Beacon beacon:beacons) {
                for(BeaconGroupPoints points:this.values()) {
                    if(points.containsKey(beacon.getId3().toInt())) {
                        BeaconPoint point = points.get(beacon.getId3().toInt());
                        BeaconData data = new BeaconData(beacon.getId3().toInt(),point.getX(),point.getY());
                        data.setDistance(beacon.getDistance());
                        result.add(data);
                        return result;
                    }
                }
            }
            return result;
        }

        if(beacons.size() == 2) {
            for(Beacon beacon:beacons) {
                for(BeaconGroupPoints points:this.values()) {
                    if(points.containsKey(beacon.getId3().toInt())) {
                        BeaconPoint point = points.get(beacon.getId3().toInt());
                        BeaconData data = new BeaconData(beacon.getId3().toInt(),point.getX(),point.getY());
                        data.setDistance(beacon.getDistance());
                        result.add(data);
                        if(result.size() == 2) {
                            return result;
                        } else {
                            break;
                        }
                    }
                }
            }
            return result;
        }

        HashMap<Integer,Double> distances = new HashMap<>();
        for(Beacon beacon:beacons) {
            distances.put(beacon.getId3().toInt(),beacon.getDistance());
        }
        List<BeaconGroupPoints> subSet = getSubSet(distances.keySet());
        Collections.sort(subSet,new BeaconGroupPointsComporator(distances));


        for(int id3:subSet.get(0).keySet()) {
            BeaconData data = new BeaconData(id3,subSet.get(0).get(id3).getX(),subSet.get(0).get(id3).getY());
            data.setDistance(distances.get(id3));
            result.add(data);
        }

        return result;
    }

    private List<BeaconGroupPoints> getSubSet(Set<Integer> keys) {
        List<BeaconGroupPoints> subSet = new ArrayList<>();
        for(BeaconGroupKey key:this.keySet()) {
            if(keys.containsAll(key)) {
                subSet.add(this.get(key));
            }

        }
        return subSet;
    }

    private class BeaconGroupPointsComporator implements Comparator<BeaconGroupPoints> {
        HashMap<Integer,Double> distances;
        public BeaconGroupPointsComporator(HashMap<Integer,Double> distances) {
            this.distances = distances;
        }

        @Override
        public int compare(BeaconGroupPoints source, BeaconGroupPoints target) {
            float sourceDistance = source.getSummDistance(distances);
            float targetDistance = target.getSummDistance(distances);
            if(sourceDistance < targetDistance) {
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
