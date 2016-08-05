package com.lps.lpsapp.viewModel.chat;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lps.lpsapp.map.GuiDevice;

import java.util.UUID;

/**
 * Created by dle on 03.08.2015.
 */
public class Actor {
    private static String TAG = "Actor";

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
    private AnimatorSet animSetXY;

    public Actor() {
        this.wight = 40f;
        this.height = 40f;
    }

    @JsonIgnore
    public void setPosition(float x,float y,long duration) {
        if(animSetXY == null) {
            ObjectAnimator animX = ObjectAnimator.ofFloat(this, "x", x);
            ObjectAnimator animY = ObjectAnimator.ofFloat(this, "y", y);
            animSetXY = new AnimatorSet();
            animSetXY.playTogether(animX, animY);
            animSetXY.setDuration(duration);
        } else {
            if(animSetXY.isRunning()) {
                animSetXY.end();
            }

            for(Animator animator:animSetXY.getChildAnimations()) {
                ObjectAnimator objectAnimator = (ObjectAnimator)animator;
                if(objectAnimator.getPropertyName().equals("x")) {
                    objectAnimator.setFloatValues(x);
                } else {
                    objectAnimator.setFloatValues(y);
                }
            }
            animSetXY.setDuration(duration);

        }
        animSetXY.setupStartValues();
        animSetXY.start();


    }
}
