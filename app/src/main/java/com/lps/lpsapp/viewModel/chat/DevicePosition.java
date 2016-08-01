package com.lps.lpsapp.viewModel.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

/**
 * Created by dle on 28.10.2015.
 */
public class DevicePosition {
    public String deviceId;
    public UUID roomId;
    public float x;
    public float y;


    public DevicePosition() {

    }

    @JsonIgnore
    public void setX(float x) {
        this.x = x;
    }

    @JsonIgnore
    public void setY(float y) {
        this.y = y;
    }

    @JsonIgnore
    public float getX() {
        return this.x;
    }

    @JsonIgnore
    public float getY() {
        return this.y;
    }
}
