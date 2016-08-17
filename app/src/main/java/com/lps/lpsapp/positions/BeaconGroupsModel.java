package com.lps.lpsapp.positions;

import com.lps.lpsapp.activities.SettingsActivity;
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
public class BeaconGroupsModel extends HashMap<BeaconGroupKey, BeaconGroup> {
    private float realScaleFactor;
    private float wight;
    private float height;

    public BeaconGroupsModel(BeaconModel model) {
        this.height = model.height;
        this.wight = model.wight;
        this.realScaleFactor = model.realScaleFactor;

        for (int i = 0; i < model.beacons.size(); i++) {
            for (int j = i + 1; j < model.beacons.size(); j++) {
                for (int k = j + 1; k < model.beacons.size(); k++) {
                                        BeaconGroup group = new BeaconGroup();
                    BeaconInRoom br1 = model.beacons.get(i);
                    BeaconInRoom br2 = model.beacons.get(j);
                    BeaconInRoom br3 = model.beacons.get(k);
                    group.put(br1.id3, new Point2D(br1.x, br1.y));
                    group.put(br2.id3, new Point2D(br2.x, br2.y));
                    group.put(br3.id3, new Point2D(br3.x, br3.y));

                    if (!this.containsKey(group.getGroupKey())) {
                        if (group.IsValide(this.wight / 10, this.height / 10)) {
                            this.put(group.getGroupKey(), group);
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

    public BeaconCalculationModel getCalculationModel(Collection<Beacon> beacons) {
        HashMap<Integer, Double> distances = new HashMap<>();
        for (Beacon beacon : beacons) {
            distances.put(beacon.getId3().toInt(), beacon.getDistance());
        }
        List<BeaconGroup> subSet = getSubSet(distances.keySet());
        Collections.sort(subSet, new BeaconGroupComporator(distances));
        if (subSet.size() > 0) {
            int count = 1;
            if (SettingsActivity.BeaconGroupCount != null) {
                count = SettingsActivity.BeaconGroupCount;
                if (count > subSet.size()) {
                    count = subSet.size();
                }
            }

            subSet = subSet.subList(0, count);
            if(subSet.size() == 1) {
                BeaconGroup nextGroup = subSet.get(0);
                if(this.mCurrentGroup != null) {
                    if(!nextGroup.isEquals(this.mCurrentGroup)) {
                        this.mCurrentGroup = nextGroup;
                        return new BeaconCalculationModel();
                    }
                }
                this.mCurrentGroup = nextGroup;
            }
        }

        BeaconCalculationModel calculationModel = new BeaconCalculationModel();
        for (BeaconGroup group : subSet) {
            List<BeaconData> datas = new ArrayList<>();
            for (int id3 : group.keySet()) {
                BeaconData data = new BeaconData(id3, group.get(id3).x, group.get(id3).y);
                data.setDistance(distances.get(id3) * this.realScaleFactor);
                datas.add(data);
            }
            calculationModel.put(group.getGroupKey(), datas);
        }

        return calculationModel;
    }

    private BeaconGroup mCurrentGroup;
    private List<BeaconGroup> getSubSet(Set<Integer> keys) {
        List<BeaconGroup> subSet = new ArrayList<>();
        for (BeaconGroupKey key : this.keySet()) {
            if (keys.containsAll(key)) {
                subSet.add(this.get(key));
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
