package com.lps.lpsapp.viewModel.rooms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lps.lpsapp.viewModel.chat.BeaconInRoom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dle on 27.07.2015.
 */
public class RoomModel extends RoomInfo {
    public float wight;
    public float height;


    public RoomModel() {
        this.border = new ArrayList<Point>();
        this.tables = new ArrayList<Table>();
    }

    @JsonDeserialize(as = ArrayList.class, contentAs = Point.class)
    public List<Point> border;

    @JsonDeserialize(as = ArrayList.class, contentAs = Table.class)
    public List<Table> tables;
}
