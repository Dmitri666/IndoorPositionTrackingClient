package com.lps.lpsapp.viewModel.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lps.lpsapp.map.GuiDevice;

import java.util.UUID;

/**
 * Created by dle on 03.08.2015.
 */
public class Actor {
    public UUID userId;
    public String userName;
    public String photoPath;
    public DevicePosition position;

    @JsonIgnore
    public GuiDevice guiElement;

    public Actor() {
    }


}
