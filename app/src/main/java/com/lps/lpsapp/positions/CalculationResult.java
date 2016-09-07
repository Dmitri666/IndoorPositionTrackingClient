package com.lps.lpsapp.positions;

import android.graphics.Path;
import android.graphics.Rect;

/**
 * Created by dle on 10.08.2016.
 */
public class CalculationResult {
    public Point2D point;

    public float distanceFactor;
    public Rect region;

    public CalculationResult(Point2D point) {
        this.point = point;
        //this.groupKey = groupKey;
        //this.distanceFactor = distanceFactor;
        //this.region = region;
    }

    public Path path;
    public CalculationResult(Path path)
    {
        this.path = path;
    }
}
