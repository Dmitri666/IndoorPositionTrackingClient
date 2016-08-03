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


    public void setX(float x) {
        this.x = x;
    }


    public void setY(float y) {
        this.y = y;
    }


    public float getX() {
        return this.x;
    }


    public float getY() {
        return this.y;
    }
}
