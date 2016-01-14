package com.lps.lpsapp.viewModel.chat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16.12.2015.
 */
public class BeaconModel {
    public float wight;
    public float height;


    public BeaconModel()
    {
       this.beacons = new ArrayList<BeaconInRoom>();
    }

    @JsonDeserialize(as=ArrayList.class, contentAs= BeaconInRoom.class)
    public List<BeaconInRoom> beacons;
}
