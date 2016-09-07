package com.lps.lpsapp.positions;

import com.lps.lpsapp.viewModel.chat.BeaconInRoom;

/**
 * Created by dle on 29.10.2015.
 */
public class RangedBeacon {
    private BeaconInRoom beaconInRoom;
    private double avrRssi;


    public RangedBeacon(BeaconInRoom b,double avrRssi) {
        this.beaconInRoom = b;
        this.avrRssi = avrRssi;
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
        return this.avrRssi;
    }



}
