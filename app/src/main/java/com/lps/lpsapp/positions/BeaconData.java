package com.lps.lpsapp.positions;

/**
 * Created by dle on 29.10.2015.
 */
public class BeaconData
{
    private float distanceFactor;
    private double distance;
    public float x;
    public float y;


    public BeaconData(double distance,float x,float y)
    {
        this.distanceFactor = 1.0f;
        this.distance = distance;
        this.x = x;
        this.y = y;
    }

    public double getDistance()
    {
        return this.distance * distanceFactor;
    }

    public void increaseDistanceFactor()
    {
        this.distanceFactor += 0.01f;
    }


}
