package com.lps.lpsapp.positions;

import android.graphics.Path;
import android.graphics.Rect;

import java.util.List;

/**
 * Created by dle on 19.02.2016.
 */
public interface PositionCalculatorNotifier {
    void onCalculationResult(List<RangedBeacon> beaconDatas, Rect bounds, Path path);
}
