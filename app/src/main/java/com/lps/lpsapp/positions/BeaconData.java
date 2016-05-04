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
    private int id3;


    public BeaconData(int id3,float x,float y)
    {
        this.distanceFactor = 1.0f;
        this.id3 = id3;
        this.x = x;
        this.y = y;
    }

    public double getFactoredDistance()
    {
        return this.distance * distanceFactor;
    }
    public void setDistance(double distance)
    {
        this.distance = distance;
        this.distanceFactor = 1.0f;
    }
    public void increaseDistanceFactor()
    {
        this.distanceFactor += 0.1f;
    }

    public void setDistanceFactor(float distanceFactor)
    {
        this.distanceFactor = distanceFactor;
    }
}
