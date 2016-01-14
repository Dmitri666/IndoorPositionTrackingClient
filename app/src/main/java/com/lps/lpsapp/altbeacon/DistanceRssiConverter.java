package com.lps.lpsapp.altbeacon;

/**
 * Created by dle on 10.09.2015.
 */
public class DistanceRssiConverter {
    public double mCoefficient1;
    public double mCoefficient2;
    public double mCoefficient3;
    public DistanceRssiConverter(double coefficient1,double coefficient2,double coefficient3)
    {
        this.mCoefficient1 = coefficient1;
        this.mCoefficient2 = coefficient2;
        this.mCoefficient3 = coefficient3;
    }

    public double ConvertDistanceToRssi(double distance,int txPower)
    {
        if (distance < 1.0d)
        {
            return Math.pow(distance, 0.1d) * txPower;
        }

        return Math.pow((distance - this.mCoefficient3) / this.mCoefficient1, 1.0 / this.mCoefficient2) * txPower;
    }

    /// <summary>
    /// The convert ratio to distance.
    /// </summary>
    /// <param name="ratio">
    /// The ratio.
    /// </param>
    /// <returns>
    /// The <see cref="double"/>.
    /// </returns>
    public double ConvertRssiToDistance(double rssi,int txPower)
    {
        double ratio = rssi / txPower;
        if (ratio < 1.0d)
        {
            return Math.pow(ratio, 10.0d);
        }

        return this.mCoefficient1 * Math.pow(ratio, this.mCoefficient2) + this.mCoefficient3;
    }
}
