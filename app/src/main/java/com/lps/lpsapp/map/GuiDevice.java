package com.lps.lpsapp.map;

import android.content.Context;
import android.widget.Button;

/**
 * Created by user on 28.07.2015.
 */
public class GuiDevice extends Button {
    public String deviceId;
    public float wight;
    public float height;


    public GuiDevice(Context context, String deviceId) {
        super(context, null);
        this.wight = 40f;
        this.height = 40f;
        this.deviceId = deviceId;
    }
}
