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
public class PositionCalculatorTest {
    private static String TAG = "PositionCalculatorTest";
    public PositionCalculatorNotifier positionCalculatorListener;
    private BeaconGroupsModel beaconModel;


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

    public PositionCalculatorTest(BeaconGroupsModel model) {
        beaconModel = model;
    }


    public CalculationResultModel calculatePosition(Collection<Beacon> beacons) {
        BeaconCalculationModel calculationModel = this.beaconModel.getCalculationModel(beacons);
        CalculationResultModel resultModel = new CalculationResultModel();
        resultModel.clip = new Region(0, 0, Math.round(beaconModel.getWight()), Math.round(beaconModel.getHeight()));

        List<RangedBeacon> firstGroup = null;
        for (BeaconGroupKey key : calculationModel.keySet()) {
            List<RangedBeacon> data = calculationModel.get(key);
            if(firstGroup == null) {
                firstGroup = data;
            }
            Path region = calculateRegion(data,firstGroup);
            if (region != null) {
                CalculationResult result = new CalculationResult(region);
                resultModel.add(result);
            }
        }



        return resultModel;
    }

    private void calculateDistanceFactor(List<RangedBeacon> beaconDatas) {
        List<Double> factors = new ArrayList<>();
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(0).x - beaconDatas.get(1).x, 2.0) + Math.pow(beaconDatas.get(0).y - beaconDatas.get(1).y, 2.0)) / (beaconDatas.get(0).getFactoredDistance() + beaconDatas.get(1).getFactoredDistance()));
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(0).x - beaconDatas.get(2).x, 2.0) + Math.pow(beaconDatas.get(0).y - beaconDatas.get(2).y, 2.0)) / (beaconDatas.get(0).getFactoredDistance() + beaconDatas.get(2).getFactoredDistance()));
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(1).x - beaconDatas.get(2).x, 2.0) + Math.pow(beaconDatas.get(1).y - beaconDatas.get(2).y, 2.0)) / (beaconDatas.get(1).getFactoredDistance() + beaconDatas.get(2).getFactoredDistance()));

        double factor = Collections.max(factors);
        Log.d(TAG, " factor=" + factor);

        for (RangedBeacon beaconData : beaconDatas) {
            beaconData.setDistanceFactor((float) factor);
        }

    }

    private Path calculateRegion(List<RangedBeacon> beaconDatas, List<RangedBeacon> firstGroup ) {
        try {
            for (int i = 0; i < 1000; i++) {
                Path path1 =new Path();
                path1.moveTo(firstGroup.get(0).x, firstGroup.get(0).y);
                path1.lineTo(firstGroup.get(1).x, firstGroup.get(1).y);
                path1.lineTo(firstGroup.get(2).x, firstGroup.get(2).y);
                path1.lineTo(firstGroup.get(0).x, firstGroup.get(0).y);
                path1.close();

                boolean found = false;
                for (RangedBeacon beaconData : beaconDatas) {
                    Path path = new Path();
                    path.addCircle(beaconData.x, beaconData.y, (float) beaconData.getFactoredDistance(), Path.Direction.CW);
                    path.close();


                    found = path1.op(path, Path.Op.INTERSECT);

                }

                if (!found) {
                    for (RangedBeacon beaconData : beaconDatas) {
                        beaconData.increaseDistanceFactor();
                    }
                } else {
                    if (SettingsActivity.ShowCircles && this.positionCalculatorListener != null) {
                        //this.positionCalculatorListener.onCalculationResult(beaconDatas, bounds,path1);
                    }
                    Log.d(TAG, "Iteration count:" + i);

                    return path1;
                }
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        Log.d(TAG, "Position not found beacon count:" + beaconDatas.size());
        return null;
    }

    private Rect calculateRegion1(List<RangedBeacon> beaconDatas, List<RangedBeacon> firstGroup ) {
        try {
            Region clip = new Region(0, 0, Math.round(beaconModel.getWight()), Math.round(beaconModel.getHeight()));

            Path path1 =new Path();
            path1.moveTo(firstGroup.get(0).x, firstGroup.get(0).y);
            path1.lineTo(firstGroup.get(1).x, firstGroup.get(1).y);
            path1.lineTo(firstGroup.get(2).x, firstGroup.get(2).y);
            path1.lineTo(firstGroup.get(0).x, firstGroup.get(0).y);
            path1.close();



            for (int i = 0; i < 1000; i++) {
                Region firstRegion = new Region();
                firstRegion.setPath(path1,clip);

                for (RangedBeacon beaconData : beaconDatas) {
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
                    for (RangedBeacon beaconData : beaconDatas) {
                        beaconData.increaseDistanceFactor();
                    }
                } else {
                    if (SettingsActivity.ShowCircles && this.positionCalculatorListener != null) {
                        this.positionCalculatorListener.onCalculationResult(beaconDatas, bounds,path1);
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
