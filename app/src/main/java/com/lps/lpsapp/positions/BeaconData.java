package com.lps.lpsapp.positions;

/**
 * Created by dle on 29.10.2015.
 */
public class BeaconData
{
    private float distanceFactor;
    private double distance;
    public Vector2D v;


    public BeaconData(double distance,double x,double y)
    {
        this.distanceFactor = 1.0f;
        this.distance = distance;
        this.v = new Vector2D(x,y);
    }

    public double getDistance()
    {
        return this.distance * distanceFactor;
    }

    public void increaseDistanceFactor()
    {
        this.distanceFactor += 0.01f;
    }

    public float getDistanceFactor()
    {
        return this.distanceFactor;
    }
}
