package com.lps.lpsapp.viewModel.rooms;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by dle on 02.11.2015.
 */
public class RequestLocationData {
    public int radius;
    public double latitude;
    public double longitude;
    public List<UUID> kitchenTypes;
    public String locationName;

    public String locationCity;

    public RequestLocationData()
    {
        this.kitchenTypes = new ArrayList<>();
    }
}
