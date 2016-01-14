package com.lps.lpsapp.viewModel.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lps.lpsapp.map.GuiDevice;

import java.util.UUID;

/**
 * Created by dle on 28.10.2015.
 */
public class DevicePosition {
    public String deviceId;
    public UUID roomId;
    public double x;
    public double y;

    @JsonIgnore
    public GuiDevice guiElement;

    public DevicePosition()
    {

    }
}
