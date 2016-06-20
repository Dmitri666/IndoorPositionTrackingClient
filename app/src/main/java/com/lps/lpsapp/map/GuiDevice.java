package com.lps.lpsapp.map;

import android.content.Context;
import android.widget.Button;

import com.lps.lpsapp.R;
import com.lps.lpsapp.viewModel.chat.DevicePosition;

/**
 * Created by user on 28.07.2015.
 */
public class GuiDevice extends Button {
    public DevicePosition devicePosition;
    public float wight;
    public float height;


    public GuiDevice(Context context, DevicePosition actor) {
        super(context, null);
        this.wight = 20f;
        this.height = 20f;
        this.devicePosition = actor;
        this.devicePosition.guiElement = this;

    }
}
