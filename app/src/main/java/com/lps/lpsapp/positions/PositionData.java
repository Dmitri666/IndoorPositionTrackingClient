package com.lps.lpsapp.positions;

/**
 * Created by dle on 21.06.2016.
 */
public class PositionData {
    public BeaconGroupKey key;
    public Point2D position;

    public PositionData() {

    }

    public PositionData(BeaconGroupKey key, Point2D position) {
        this.key = key;
        this.position = position;
    }
}
