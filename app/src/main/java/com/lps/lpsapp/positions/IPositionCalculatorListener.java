package com.lps.lpsapp.positions;

import android.graphics.Rect;

import java.util.List;

/**
 * Created by dle on 19.02.2016.
 */
public interface IPositionCalculatorListener {
    void calculationResult(List<BeaconData> beaconDatas,Rect bounds);
}
