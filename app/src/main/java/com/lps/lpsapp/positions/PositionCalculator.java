package com.lps.lpsapp.positions;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

import com.lps.lpsapp.viewModel.chat.BeaconInRoom;
import com.lps.lpsapp.viewModel.chat.BeaconModel;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dle on 29.10.2015.
 */
public class PositionCalculator {
    private static String TAG = "PositionCalculator";
    private static float scaleFactor = 1.0f;
    private static double mRoomWigth;
    private static double mRoomHeight;
    public IPositionCalculatorListener positionCalculatorListener;


    private static HashMap<Integer,BeaconInRoom> beaconsInRoom = new HashMap<>();
    private static Comparator<Beacon> comparator  = new Comparator<Beacon>() {
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

    public PositionCalculator(BeaconModel model)
    {
        beaconsInRoom.clear();
        for(BeaconInRoom beacon:model.beacons)
        {
            beaconsInRoom.put(beacon.id3,beacon);
        }
        mRoomHeight = model.height;
        mRoomWigth = model.wight;
    }


    public PointD calculatePosition(Collection<Beacon> beacons)
    {
        if(beacons.size() == 0 || beaconsInRoom.size() == 0)
        {
            return null;
        }

        List<Beacon> list = new ArrayList<>(beacons);
        Collections.sort(list,comparator);

        if(list.size() > 3) {
            list = list.subList(0,3);
        }

        List<BeaconData> beaconDatas = new ArrayList<>();
        for(int i = 0;i < list.size();i++)
        {
            BeaconInRoom beaconInRoom = beaconsInRoom.get(list.get(i).getId3().toInt());
            if(beaconInRoom == null)
            {
                Log.d(TAG,"Beacon " + list.get(i).getId1() + ":" + list.get(i).getId2() + ":" + list.get(i).getId3() + " not found");
                continue;
            }
            BeaconData data = new BeaconData(list.get(i).getDistance(),beaconInRoom.x,beaconInRoom.y);
            data.beaconId = list.get(i).getId3().toString();
            beaconDatas.add(data);
        }

        if(beaconDatas.size() == 0)
        {
            return null;
        }
        else if(beaconDatas.size() == 1)
        {
            return new PointD(beaconDatas.get(0).x,beaconDatas.get(0).y);
        }
        else if(beaconDatas.size() == 2)
        {
            return new PointD(beaconDatas.get(0).x,beaconDatas.get(0).y);
        }

        calculateDistanceFactor(beaconDatas);

        Rect region = calculateRegion(beaconDatas);
        if(region == null)
        {
            Log.d(TAG,"position not found");
            return null;
        }
        else
        {
            String msg = "";
            for (BeaconData data:beaconDatas) {
                msg += "Id=" + data.beaconId + " distance=" + data.getDistance() + '\n';
            }
            Log.d(TAG,msg + " Position x=" + region.exactCenterX() + " y=" + region.exactCenterY());
            return new PointD(region.exactCenterX() / scaleFactor, region.exactCenterY() / scaleFactor);
        }
    }

    private void calculateDistanceFactor(List<BeaconData> beaconDatas)
    {
        List<Double> factors = new ArrayList<>();
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(0).x - beaconDatas.get(1).x,2.0) + Math.pow(beaconDatas.get(0).y - beaconDatas.get(1).y,2.0)) / (beaconDatas.get(0).getDistance() + beaconDatas.get(1).getDistance()));
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(0).x - beaconDatas.get(2).x, 2.0) + Math.pow(beaconDatas.get(0).y - beaconDatas.get(2).y, 2.0)) / (beaconDatas.get(0).getDistance() + beaconDatas.get(2).getDistance()));
        factors.add(Math.sqrt(Math.pow(beaconDatas.get(1).x - beaconDatas.get(2).x, 2.0) + Math.pow(beaconDatas.get(1).y - beaconDatas.get(2).y, 2.0)) / (beaconDatas.get(1).getDistance() + beaconDatas.get(2).getDistance()));

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
            Region clip = new Region(0, 0, (int) (mRoomWigth * scaleFactor), (int) (mRoomHeight * scaleFactor));

            for(int i = 0;i < 1000;i++) {

                Region firstRegion = null;
                for(BeaconData beaconData:beaconDatas) {
                    Path path = new Path();
                    path.addCircle(beaconData.x * scaleFactor, beaconData.y * scaleFactor, (float) beaconData.getDistance() * scaleFactor, Path.Direction.CW);
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
                    Log.d(TAG,"Position calculated. Iteration count:" + i);
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
