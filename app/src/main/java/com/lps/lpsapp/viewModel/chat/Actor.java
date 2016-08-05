package com.lps.lpsapp.viewModel.chat;

import android.animation.AnimatorSet;

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
    public double wight;

    @JsonIgnore
    public double height;

    @JsonIgnore
    public GuiDevice guiElement;

    @JsonIgnore
    public void setX(float x) {
        this.position.x = x;
        this.guiElement.invalidate();
    }

    @JsonIgnore
    public void setY(float y) {
        this.position.y = y;
        this.guiElement.invalidate();
    }

    @JsonIgnore
    public float getX() {
        return this.position.x;
    }

    @JsonIgnore
    public float getY() {
        return this.position.y;
    }

    @JsonIgnore
    public AnimatorSet animSetXY;

    public Actor() {
        this.wight = 40f;
        this.height = 40f;
    }


}
