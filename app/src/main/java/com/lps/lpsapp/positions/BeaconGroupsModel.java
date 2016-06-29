package com.lps.lpsapp.positions;

import com.lps.lpsapp.viewModel.chat.BeaconInRoom;
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
public class BeaconGroupsModel extends HashMap<BeaconGroupKey,BeaconGroup> {
    private float realScaleFactor;
    private float wight;
    private float height;

    public BeaconGroupsModel(BeaconModel model) {
        this.height = model.height;
        this.wight = model.wight;
        this.realScaleFactor = model.realScaleFactor;

        for(int i = 0; i < model.beacons.size(); i++)
        {

            for(int j = i + 1; j < model.beacons.size(); j++) {

                for(int k = j + 1; k < model.beacons.size(); k++) {


                    BeaconGroupKey key = new BeaconGroupKey();
                    BeaconGroup group = new BeaconGroup();
                    BeaconInRoom br1 = model.beacons.get(i);
                    BeaconInRoom br2 = model.beacons.get(j);
                    BeaconInRoom br3 = model.beacons.get(k);

                    key.add(br1.id3);
                    group.put(br1.id3,new Point2D(br1.x,br1.y));

                    key.add(br2.id3);
                    group.put(br2.id3,new Point2D(br2.x,br2.y));

                    key.add(br3.id3);
                    group.put(br3.id3,new Point2D(br3.x,br3.y));

                    if(!this.containsKey(key)) {
                        if(group.IsValide(this.wight / 10,this.height / 10)) {
                            this.put(key, group);
                        }
                    }
                }

            }


        }
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
                for(BeaconGroup points:this.values()) {
                    if(points.containsKey(beacon.getId3().toInt())) {
                        Point2D point = points.get(beacon.getId3().toInt());
                        BeaconData data = new BeaconData(beacon.getId3().toInt(),point.x,point.y);
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
                for(BeaconGroup points:this.values()) {
                    if(points.containsKey(beacon.getId3().toInt())) {
                        Point2D point = points.get(beacon.getId3().toInt());
                        BeaconData data = new BeaconData(beacon.getId3().toInt(),point.x,point.y);
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
        List<BeaconGroup> subSet = getSubSet(distances.keySet());
        Collections.sort(subSet,new BeaconGroupPointsComporator(distances));


        for(int id3:subSet.get(0).keySet()) {
            BeaconData data = new BeaconData(id3,subSet.get(0).get(id3).x,subSet.get(0).get(id3).y);
            data.setDistance(distances.get(id3));
            result.add(data);
        }

        return result;
    }

    private List<BeaconGroup> getSubSet(Set<Integer> keys) {
        List<BeaconGroup> subSet = new ArrayList<>();
        for(BeaconGroupKey key:this.keySet()) {
            if(keys.containsAll(key)) {
                subSet.add(this.get(key));
            }

        }
        return subSet;
    }

    private class BeaconGroupPointsComporator implements Comparator<BeaconGroup> {
        HashMap<Integer,Double> distances;
        public BeaconGroupPointsComporator(HashMap<Integer,Double> distances) {
            this.distances = distances;
        }

        @Override
        public int compare(BeaconGroup source, BeaconGroup target) {
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
