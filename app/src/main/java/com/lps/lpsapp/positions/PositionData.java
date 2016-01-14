package com.lps.lpsapp.positions;

/**
 * Created by dle on 06.11.2015.
 */
public class PositionData {
    public Vector2D mPosition;
    Double mDifference;

    public PositionData(Vector2D position)
    {
        this.mPosition = position;
        this.mDifference = Double.NaN;
    }
}
