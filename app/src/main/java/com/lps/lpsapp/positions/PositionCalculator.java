package com.lps.lpsapp.positions;

import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.ejml.simple.SimpleMatrix;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dle on 29.10.2015.
 */
public class PositionCalculator {
    private static String TAG = "PositionCalculator";
    public PositionCalculatorNotifier positionCalculatorListener;
    private BeaconGroupsModel beaconGroupsModel;


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
        this.beaconGroupsModel = model;
    }


    public CalculationResultModel calculatePosition(Collection<Beacon> beacons) {
        BeaconCalculationModel calculationModel = this.beaconGroupsModel.getCalculationModel(beacons);
        CalculationResultModel resultModel = new CalculationResultModel();
        TrelatationCalculator trelatationCalculator = new TrelatationCalculator(calculationModel);
        Path area = null;
        for(TrippleGroup group :calculationModel.trippleGroups) {
            area = calculationModel.getPath(group);
            Path path = trelatationCalculator.calculate(group);
            if(path != null) {
                RectF bounds = new RectF();
                path.computeBounds(bounds,true);
                String g = "";
                CalculationResult cr = new CalculationResult(new Point2D(bounds.centerX(),bounds.centerY()));
                resultModel.add(cr);
                break;
            }

        }

        TrigometricCalculator trigometricCalculator = new TrigometricCalculator(calculationModel,area);
        for(DubbleGroup group :calculationModel.dubbleGroups) {

            Point2D point = trigometricCalculator.calculate(group);
            if(point != null) {
                CalculationResult cr = new CalculationResult(point);
                resultModel.add(cr);
            }

        }
//        resultModel.clip = new Region(0, 0, Math.round(beaconGroupsModel.getWight()), Math.round(beaconGroupsModel.getHeight()));
//
//        List<RangedBeacon> firstGroup = null;
//        for (BeaconGroupKey key : calculationModel.keySet()) {
//            List<RangedBeacon> data = calculationModel.get(key);
//            if(firstGroup == null) {
//                firstGroup = data;
//            }
//            Path region = calculateRegion(data,firstGroup);
//            if (region != null) {
//                CalculationResult result = new CalculationResult(region);
//                resultModel.add(result);
//            }
//        }



        return resultModel;
    }

    private class TrigometricCalculator {
        private BeaconCalculationModel calculationModel;
        private Path area;

        public TrigometricCalculator(BeaconCalculationModel calculationModel,Path area) {
            this.calculationModel = calculationModel;
            this.area = area;
        }

        public Point2D calculate(DubbleGroup group) {
            Integer id1 = group.getGroupIds().first();
            Integer id2 = group.getGroupIds().last();
            RangedBeacon rb1 = this.calculationModel.beacons.get(id1);
            RangedBeacon rb2 = this.calculationModel.beacons.get(id2);

            double distance1 = (Beacon.getDistanceCalculator()).calculateDistance(rb1.getTxPower(), rb1.getAvrRssi()) * beaconGroupsModel.getRealScaleFactor();
            double distance2 = (Beacon.getDistanceCalculator()).calculateDistance(rb2.getTxPower(), rb2.getAvrRssi()) * beaconGroupsModel.getRealScaleFactor();

            SimpleMatrix v1 = new SimpleMatrix(1,2,true,new double[]{rb1.getX(),rb1.getY()});
            SimpleMatrix v2 = new SimpleMatrix(1,2,true,new double[]{rb2.getX(),rb2.getY()});

            SimpleMatrix vd = v1.minus(v2);
            double d =  vd.s.determinant();
            double d1 =  vd.determinant();



            return null;
        }
    }

    private class TrelatationCalculator {
        private BeaconCalculationModel calculationModel;

        public TrelatationCalculator(BeaconCalculationModel calculationModel) {
            this.calculationModel = calculationModel;
        }

        public Path calculate(TrippleGroup group) {
            try {
                for (int i = 0; i < 1000; i++) {
                    Path path1 = this.calculationModel.getPath(group);

                    boolean found = false;
                    for (Integer id3 : group.getGroupIds()) {
                        Path path = new Path();
                        RangedBeacon rb = this.calculationModel.beacons.get(id3);
                        double distance = (Beacon.getDistanceCalculator()).calculateDistance(rb.getTxPower(), rb.getAvrRssi());
                        float radius = (float) (distance * beaconGroupsModel.getRealScaleFactor());
                        path.addCircle(rb.getX(), rb.getY(), radius, Path.Direction.CW);
                        path.close();


                        found = path1.op(path, Path.Op.INTERSECT);

                    }

                    if (path1.isEmpty()) {
                        for (Integer id3 : group.getGroupIds()) {
                            RangedBeacon rb = this.calculationModel.beacons.get(id3);
                            rb.incriseErrorFactor();
                        }

                    } else {
                        //if (SettingsActivity.ShowCircles && this.positionCalculatorListener != null) {
                        //this.positionCalculatorListener.onCalculationResult(beaconDatas, bounds,path1);
                        //}
                        Log.d(TAG, "Iteration count:" + i);

                        return path1;
                    }
                }

            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
            Log.d(TAG, "Position not found beacon count: TrelatationCalculator");
            return null;
        }
    }

    private Path calculateRegion(List<RangedBeacon> beaconDatas, List<RangedBeacon> firstGroup ) {
//        try {
//            for (int i = 0; i < 1000; i++) {
//                Path path1 =new Path();
//                path1.moveTo(firstGroup.get(0).x, firstGroup.get(0).y);
//                path1.lineTo(firstGroup.get(1).x, firstGroup.get(1).y);
//                path1.lineTo(firstGroup.get(2).x, firstGroup.get(2).y);
//                path1.lineTo(firstGroup.get(0).x, firstGroup.get(0).y);
//                path1.close();
//
//                boolean found = false;
//                for (RangedBeacon beaconData : beaconDatas) {
//                    Path path = new Path();
//                    path.addCircle(beaconData.x, beaconData.y, (float) beaconData.getFactoredDistance(), Path.Direction.CW);
//                    path.close();
//
//
//                    found = path1.op(path, Path.Op.INTERSECT);
//
//                }
//
//                if (!found) {
//                    for (RangedBeacon beaconData : beaconDatas) {
//                        beaconData.increaseDistanceFactor();
//                    }
//                } else {
//                    if (SettingsActivity.ShowCircles && this.positionCalculatorListener != null) {
//                        //this.positionCalculatorListener.onCalculationResult(beaconDatas, bounds,path1);
//                    }
//                    Log.d(TAG, "Iteration count:" + i);
//
//                    return path1;
//                }
//            }
//
//        } catch (Exception ex) {
//            Log.e(TAG, ex.getMessage(), ex);
//        }
//        Log.d(TAG, "Position not found beacon count:" + beaconDatas.size());
        return null;
    }


}
