package com.lps.lpsapp.positions;

import com.lps.lpsapp.viewModel.chat.BeaconInRoom;

/**
 * Created by dle on 29.10.2015.
 */
public class RangedBeacon {
    private BeaconInRoom beaconInRoom;
    private double avrRssi;
    private double avrDistance;
    private int txPower;
    private double errorFactor;

    public RangedBeacon(BeaconInRoom b,int txPower,double avrRssi,double avrDistance) {
        this.beaconInRoom = b;
        this.avrRssi = avrRssi;
        this.txPower = txPower;
        this.avrDistance = avrDistance;
        this.errorFactor = 1.0;
    }

    public float getX() {
        return this.beaconInRoom.x;
    }

    public float getY() {
        return this.beaconInRoom.y;
    }

    public int getId3() {
        return this.beaconInRoom.id3;
    }

    public double getAvrRssi(){
        return this.avrRssi * this.errorFactor;
    }

    public double getAvrDistance(){
        return this.avrDistance;
    }
    public int getTxPower() {
        return this.txPower;
    }

    public void incriseErrorFactor() {
        this.errorFactor += 0.05;
    }



}
