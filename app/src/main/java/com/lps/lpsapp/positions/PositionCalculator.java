package com.lps.lpsapp.positions;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

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
    private BeaconGroupsModel beaconModel;
    public IPositionCalculatorListener positionCalculatorListener;
    private PositionData lastPosition;

    private Comparator<Beacon> comparator  = new Comparator<Beacon>() {
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

    public PositionCalculator(BeaconGroupsModel model)
    {
        beaconModel = model;
        lastPosition = null;
    }


    public PositionData calculatePosition(Collection<Beacon> beacons)
    {
        List<Beacon> list = new ArrayList<>(beacons);
        Collections.sort(list,comparator);

        if(list.size() > 3) {
            list = list.subList(0,3);
        }

        for(int i = 0; i < list.size();i++) {
            Log.d(TAG," min :" + list.get(i).getId3().toString());
        }

        List<BeaconData> beaconDatas = this.beaconModel.getCalculationModel(beacons);
        if(beaconDatas == null || beaconDatas.size() == 0)
        {
            return null;
        }

        if(beaconDatas.size() < 3)
        {
            if(lastPosition != null) {
                return lastPosition;
            } else {
                if(beaconDatas.size() == 0) {
                    return lastPosition;
                } else if (beaconDatas.size() == 1) {
                    BeaconGroupKey key = new BeaconGroupKey();
                    key.add(beaconDatas.get(0).id3);
                    return new PositionData(key,new Point2D(beaconDatas.get(0).x, beaconDatas.get(0).y));
                }  else if (beaconDatas.size() == 2) {
                    BeaconGroupKey key = new BeaconGroupKey();
                    key.add(beaconDatas.get(0).id3);
                    key.add(beaconDatas.get(1).id3);
                    return new PositionData(key,new Point2D((beaconDatas.get(0).x + beaconDatas.get(1).x) / 2f, (beaconDatas.get(0).y + beaconDatas.get(1).y) / 2f));
                }
            }

        }

        //calculateDistanceFactor(beaconDatas);

        Rect region = calculateRegion(beaconDatas);
        if(region == null)
        {
            Log.e(TAG,"position not found");
            return null;
        }
        else
        {
            BeaconGroupKey key = new BeaconGroupKey();
            key.add(beaconDatas.get(0).id3);
            key.add(beaconDatas.get(1).id3);
            key.add(beaconDatas.get(2).id3);
            PositionData result = new PositionData(key,new Point2D(region.exactCenterX(), region.exactCenterY()));
            lastPosition = result;
            Log.d(TAG,"GroupKey (" + beaconDatas.get(0).id3 + "," + beaconDatas.get(1).id3 + "," + beaconDatas.get(2).id3 + ")");
            Log.d(TAG,"Position (" + result.position.x / beaconModel.getRealScaleFactor()  + "," + result.position.y / beaconModel.getRealScaleFactor() + ")");
            return result;
        }
    }

    private void calculateDistanceFactor(List<BeaconData> beaconDatas)
    {
        List<Double> factors = new ArrayList<>();
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(0).x - beaconDatas.get(1).x,2.0) + Math.pow(beaconDatas.get(0).y - beaconDatas.get(1).y,2.0)) / (beaconDatas.get(0).getFactoredDistance() + beaconDatas.get(1).getFactoredDistance()));
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(0).x - beaconDatas.get(2).x, 2.0) + Math.pow(beaconDatas.get(0).y - beaconDatas.get(2).y, 2.0)) / (beaconDatas.get(0).getFactoredDistance() + beaconDatas.get(2).getFactoredDistance()));
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(1).x - beaconDatas.get(2).x, 2.0) + Math.pow(beaconDatas.get(1).y - beaconDatas.get(2).y, 2.0)) / (beaconDatas.get(1).getFactoredDistance() + beaconDatas.get(2).getFactoredDistance()));

        double factor = Collections.max(factors);
        Log.d(TAG," factor=" + factor);

        for (BeaconData beaconData:beaconDatas) {
            beaconData.setDistanceFactor((float)factor);
        }

    }

    private Rect calculateRegion(List<BeaconData> beaconDatas)
    {
        try
        {
            Region clip = new Region(0, 0, Math.round(beaconModel.getWight()), Math.round(beaconModel.getHeight()));
            /*Path tpath = new Path();
            tpath.moveTo(beaconDatas.get(0).x,beaconDatas.get(0).y);
            tpath.lineTo(beaconDatas.get(1).x,beaconDatas.get(1).y);
            tpath.lineTo(beaconDatas.get(2).x,beaconDatas.get(2).y);
            tpath.lineTo(beaconDatas.get(0).x,beaconDatas.get(0).y);
            tpath.close();
            Region tregion = new Region();
            tregion.setPath(tpath, clip);*/
            for(int i = 0;i < 1000;i++) {

                Region firstRegion = null;
                for(BeaconData beaconData:beaconDatas) {
                    Path path = new Path();
                    path.addCircle(beaconData.x, beaconData.y, (float) beaconData.getFactoredDistance(), Path.Direction.CW);
                    path.close();
                    Region region = new Region();
                    region.setPath(path, clip);

                    if(firstRegion == null)
                    {
                        firstRegion = region;
                    }
                    else
                    {
                        if(!firstRegion.op(region,Region.Op.INTERSECT))
                        {
                            break;
                        }
                    }
                }

                Rect bounds = new Rect();
                firstRegion.getBounds(bounds);
                if(bounds.isEmpty()) {
                    for(BeaconData beaconData:beaconDatas) {
                        beaconData.increaseDistanceFactor();
                    }
                }
                else
                {
                    if(this.positionCalculatorListener != null)
                    {
                        this.positionCalculatorListener.calculationResult(beaconDatas,bounds);
                    }
                    Log.d(TAG,"Iteration count:" + i);
                    return bounds;
                }
            }

        } catch (Exception ex)
        {
            Log.e(TAG,ex.getMessage(),ex);
        }
        Log.d(TAG,"Position not found beacon count:" + beaconDatas.size());
        return null;
    }
}
