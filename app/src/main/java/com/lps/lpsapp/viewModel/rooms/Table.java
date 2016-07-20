package com.lps.lpsapp.viewModel.rooms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lps.lpsapp.map.GuiTable;

import java.util.UUID;

/**
 * Created by dle on 27.07.2015.
 */
public class Table {

    public double wight;
    public double height;
    public UUID id;
    public double x;
    public double y;
    public String description;
    public double angle;
    public String type;

    @JsonIgnore
    public GuiTable guiElement;

    public Table() {

    }
}
