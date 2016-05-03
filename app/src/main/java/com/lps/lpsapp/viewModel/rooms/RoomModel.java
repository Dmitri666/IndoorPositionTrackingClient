package com.lps.lpsapp.viewModel.rooms;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dle on 27.07.2015.
 */
public class RoomModel extends RoomInfo {
    public float wight;
    public float height;
    public String backgroungImage;

    public RoomModel() {
        this.tables = new ArrayList<Table>();
    }

    @JsonDeserialize(as = ArrayList.class, contentAs = Table.class)
    public List<Table> tables;
}
