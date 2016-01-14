package com.lps.lpsapp.viewModel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dle on 21.09.2015.
 */
public class RangingData {
    public RangingData()
    {
        this.beaconDataList = new ArrayList<>();
    }

    public String deviceId;

    @JsonDeserialize(as=ArrayList.class, contentAs= BeaconData.class)
    public List<BeaconData> beaconDataList;
}
