package com.lps.lpsapp.services;

import android.util.Log;

import com.lps.lpsapp.altbeacon.DistanceRssiConverter;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.distance.CurveFittedDistanceCalculator;
import org.altbeacon.beacon.distance.DistanceCalculator;
import org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator;

import java.lang.reflect.Field;

/**
 * Created by dle on 26.08.2015.
 */
public class BeaconAccessor extends Beacon{
    public static final String TAG = "BeaconAccessor";
    private DistanceRssiConverter converter;
    public BeaconAccessor(Beacon beacon)
    {
        super(beacon);
        init();
    }

    public DistanceRssiConverter getDistanceRssiConverter()
    {
        return  this.converter;
    }

    private void init()
    {
        try {
            double mCoefficient1;
            double mCoefficient2;
            double mCoefficient3;
            DistanceCalculator mcalc = Beacon.getDistanceCalculator();

            Field feld = ModelSpecificDistanceCalculator.class.getDeclaredField("mDistanceCalculator");
            feld.setAccessible(true);
            Object value = feld.get(mcalc);
            if(value == null)
            {
                return;
            }

            DistanceCalculator calc = (DistanceCalculator)value;

            feld = CurveFittedDistanceCalculator.class.getDeclaredField("mCoefficient1");
            feld.setAccessible(true);
            value = feld.get(calc);
            mCoefficient1 = (Double) value;


            feld = CurveFittedDistanceCalculator.class.getDeclaredField("mCoefficient2");
            feld.setAccessible(true);
            value = feld.get(calc);
            mCoefficient2 = (Double) value;


            feld = CurveFittedDistanceCalculator.class.getDeclaredField("mCoefficient3");
            feld.setAccessible(true);
            value = feld.get(calc);
            mCoefficient3 = (Double) value;


            converter = new DistanceRssiConverter(mCoefficient1,mCoefficient2,mCoefficient3);
        }
        catch (Exception ex)
        {
            Log.e(TAG,ex.getMessage(),ex);

        }
    }


}
