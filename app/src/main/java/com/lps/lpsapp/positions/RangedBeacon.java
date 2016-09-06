package com.lps.lpsapp.positions;

import com.lps.lpsapp.viewModel.chat.BeaconInRoom;

/**
 * Created by dle on 29.10.2015.
 */
public class RangedBeacon {
    public int id3;
    public float x;
    public float y;
    private double rssi;


    public RangedBeacon(BeaconInRoom b) {
        this.id3 = b.id3;
        this.x = b.x;
        this.y = b.y;

    }


}
