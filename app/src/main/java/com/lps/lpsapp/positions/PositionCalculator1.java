package com.lps.lpsapp.positions;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;

import com.lps.lpsapp.activities.SettingsActivity;
import com.lps.lpsapp.altbeacon.DistanceRssiConverter;
import com.lps.lpsapp.viewModel.chat.BeaconInRoom;
import com.lps.lpsapp.viewModel.chat.BeaconModel;
import com.lps.lpsapp.viewModel.rooms.RoomModel;

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
public class PositionCalculator1 {
    private static String TAG = "PositionCalculator";
    private static float scaleFactor = 100.0f;
    private static double mRoomWigth;
    private static double mRoomHeight;
    public static DistanceRssiConverter mConverter;

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

    private static Comparator<PositionData> positionDataComparator  = new Comparator<PositionData>() {
        @Override
        public int compare(PositionData lhs, PositionData rhs) {
            if (lhs.mDifference < rhs.mDifference) {
                return -1;
            } else if (lhs.mDifference > rhs.mDifference) {
                return 1;
            }
            return 0;
        }
    };

    public PositionCalculator1(BeaconModel model)
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

        if(SettingsActivity.BeaconCount != 0)
        {
            if(SettingsActivity.BeaconCount < list.size()) {
                list = list.subList(0,SettingsActivity.BeaconCount);
            }
        }

        List<BeaconData> beaconDatas = new ArrayList<>();
        for(int i = 0;i < list.size();i++)
        {
            BeaconInRoom beaconInRoom = beaconsInRoom.get(list.get(i).getId3().toInt());
            BeaconData data = new BeaconData(list.get(i).getDistance(),beaconInRoom.x,beaconInRoom.y);
            beaconDatas.add(data);
        }

        if(beaconDatas.size() == 0)
        {
            return null;
        }
        else if(beaconDatas.size() == 1)
        {
            return new PointD(beaconDatas.get(0).v.x,beaconDatas.get(0).v.y);
        }

        Rect grobResult = calculateDistanceFactor(beaconDatas);
        if(grobResult == null)
        {
            Log.d(TAG," grobCalculator result == null");
            return null;
        }
        else if(beaconDatas.get(0).getDistanceFactor() != 1.0f)
        {
            Log.d(TAG," DistanceFactor=" + beaconDatas.get(0).getDistanceFactor());
            return new PointD(grobResult.exactCenterX() / scaleFactor, grobResult.exactCenterY() / scaleFactor);
        }

        List<PositionData> pointsList = new ArrayList<>();
        for(int i = 0;i < beaconDatas.size();i++)
        {
            for(int j = i + 1;j < beaconDatas.size();j++)
            {
                List<PositionData> points = calculateFine(beaconDatas.get(i),beaconDatas.get(j));
                pointsList.addAll(points);
            }
        }

        /*List<Vector2D> validePoints = new ArrayList<>();
        for(int i = 0;i < pointsList.size();i++)
        {
            PointD test = new PointD(pointsList.get(i).x * scaleFactor,pointsList.get(i).y * scaleFactor);
            if(grobResult.contains((int)test.x,(int)test.y))
            {
                validePoints.add(pointsList.get(i));
            }
        }

        if(validePoints.size() == 0)
        {
            return null;
        }*/


        for(int i = 0; i < pointsList.size();i++)
        {
            PositionData positionData = pointsList.get(i);
            double diff = 0;
            for(int j = 0;j < beaconDatas.size();j++) {
                diff +=  Math.abs(beaconDatas.get(j).v.minus(positionData.mPosition).getLength() - beaconDatas.get(j).getDistance());
            }
            positionData.mDifference = diff;
        }

        Collections.sort(pointsList,positionDataComparator);
        if(pointsList != null)
        {
            return new PointD(pointsList.get(0).mPosition.x,pointsList.get(0).mPosition.y);
        }

        return null;

    }

    private static List<PositionData> calculateFine(BeaconData a,BeaconData b)
    {
        List<PositionData> result = new ArrayList<>();
        double abLength = a.v.minus(b.v).getLength();

        Double s = (Math.pow(abLength,2.0) + Math.pow(a.getDistance(),2.0) - Math.pow(b.getDistance(),2.0))/2.0/abLength;
        if(s.isNaN())
        {
            Log.d(TAG,"s=0");
            return result;
        }

        Double h =  Math.sqrt(Math.pow(a.getDistance(), 2.0) - Math.pow(s, 2.0));
        if(h.isNaN() || h == 0)
        {
            Vector2D result1 = a.v.plus(b.v.minus(a.v).withLength(s));
            Vector2D result2 = b.v.plus(a.v.minus(b.v).withLength(s));
            result.add(new PositionData(result1));
            result.add(new PositionData(result2));
            return result;
        }

        Vector2D v2 = b.v.minus(a.v).withLength(s);
        Vector2D v31 = v2.ortogonal().withLength(h);
        Vector2D v32 = new Vector2D(-v31.x,-v31.y);

        Vector2D result1 = a.v.plus(v2).plus(v31);
        Vector2D result2 = a.v.plus(v2).plus(v32);

        if (!result1.x.isNaN() && !result1.y.isNaN())
        {
            result.add(new PositionData(result1));
        }
        if (!result2.x.isNaN() && !result2.y.isNaN())
        {
            result.add(new PositionData(result2));
        }

        return result;
    }

    private static Rect calculateDistanceFactor(List<BeaconData> beaconDatas)
    {
        Region clip = new Region(0, 0, (int) (mRoomWigth * scaleFactor), (int) (mRoomHeight * scaleFactor));

        for(int i = 0;i < 1000;i++) {

            Region firstRegion = null;
            for(BeaconData beaconData:beaconDatas) {
                Path path = new Path();
                path.addCircle(beaconData.v.x.floatValue() * scaleFactor, beaconData.v.y.floatValue() * scaleFactor, (float) beaconData.getDistance() * scaleFactor, Path.Direction.CW);
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
                Log.d(TAG,"new Position iteration count:" + i + " beacon count:" + beaconDatas.size());
                bounds.top -= 5;
                bounds.bottom += 5;
                bounds.left -= 5;
                bounds.right += 5;
                return bounds;
            }


        }
        Log.d(TAG,"Position not found beacon count:" + beaconDatas.size());
        return null;
    }
}
