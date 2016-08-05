package com.lps.lpsapp.positions;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

import com.lps.lpsapp.activities.SettingsActivity;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dle on 29.10.2015.
 */
public class PositionCalculator {
    private static String TAG = "PositionCalculator";
    public PositionCalculatorNotifier positionCalculatorListener;
    private BeaconGroupsModel beaconModel;
    private PositionData lastPosition;

    private Comparator<Beacon> comparator = new Comparator<Beacon>() {
        @Override
        public int compare(Beacon lhs, Beacon rhs) {
            if (lhs.getDistance() < rhs.getDistance()) {
                return -1;
            } else if (lhs.getDistance() > rhs.getDistance()) {
                return 1;
            }
            return 0;
        }
    };

    public PositionCalculator(BeaconGroupsModel model) {
        beaconModel = model;
        lastPosition = null;
    }


    public Point2D calculatePosition(Collection<Beacon> beacons) {
        List<Beacon> list = new ArrayList<>(beacons);
        Collections.sort(list, comparator);

        if (list.size() > 3) {
            list = list.subList(0, 3);
        }

        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, " min :" + list.get(i).getId3().toString());
        }

        BeaconCalculationModel calculationModel = this.beaconModel.getCalculationModel(beacons);

        List<Point2D> results = new ArrayList<>();
        for (List<BeaconData> data : calculationModel.values()) {
            Rect region = calculateRegion(data);
            if (region != null) {
                Point2D result = new Point2D(region.exactCenterX(), region.exactCenterY());
                results.add(result);
            }
        }

        Point2D result = new Point2D(0, 0);
        if (results.size() == 0) {
            return null;
        }

        for (Point2D point : results) {
            result.x += point.x;
            result.y += point.y;
        }

        result.x = result.x / results.size();
        result.y = result.y / results.size();

        return result;
    }

    private void calculateDistanceFactor(List<BeaconData> beaconDatas) {
        List<Double> factors = new ArrayList<>();
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(0).x - beaconDatas.get(1).x, 2.0) + Math.pow(beaconDatas.get(0).y - beaconDatas.get(1).y, 2.0)) / (beaconDatas.get(0).getFactoredDistance() + beaconDatas.get(1).getFactoredDistance()));
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(0).x - beaconDatas.get(2).x, 2.0) + Math.pow(beaconDatas.get(0).y - beaconDatas.get(2).y, 2.0)) / (beaconDatas.get(0).getFactoredDistance() + beaconDatas.get(2).getFactoredDistance()));
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(1).x - beaconDatas.get(2).x, 2.0) + Math.pow(beaconDatas.get(1).y - beaconDatas.get(2).y, 2.0)) / (beaconDatas.get(1).getFactoredDistance() + beaconDatas.get(2).getFactoredDistance()));

        double factor = Collections.max(factors);
        Log.d(TAG, " factor=" + factor);

        for (BeaconData beaconData : beaconDatas) {
            beaconData.setDistanceFactor((float) factor);
        }

    }

    private Rect calculateRegion(List<BeaconData> beaconDatas) {
        try {
            Region clip = new Region(0, 0, Math.round(beaconModel.getWight()), Math.round(beaconModel.getHeight()));

            for (int i = 0; i < 1000; i++) {

                Region firstRegion = null;
                for (BeaconData beaconData : beaconDatas) {
                    Path path = new Path();
                    path.addCircle(beaconData.x, beaconData.y, (float) beaconData.getFactoredDistance(), Path.Direction.CW);
                    path.close();
                    Region region = new Region();
                    region.setPath(path, clip);

                    if (firstRegion == null) {
                        firstRegion = region;
                    } else {
                        firstRegion.op(region, Region.Op.INTERSECT);
                    }
                }

                Rect bounds = new Rect();
                firstRegion.getBounds(bounds);
                if (bounds.isEmpty()) {
                    for (BeaconData beaconData : beaconDatas) {
                        beaconData.increaseDistanceFactor();
                    }
                } else {
                    if (SettingsActivity.ShowCircles && this.positionCalculatorListener != null) {
                        this.positionCalculatorListener.onCalculationResult(beaconDatas, bounds);
                    }
                    Log.d(TAG, "Iteration count:" + i);

                    return bounds;
                }
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        Log.d(TAG, "Position not found beacon count:" + beaconDatas.size());
        return null;
    }
}
