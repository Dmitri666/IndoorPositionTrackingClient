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
        this.wight = 0.2f;
        this.height = 0.2f;
        this.devicePosition = actor;
        this.devicePosition.guiElement = this;
        if(actor.deviceId.equals("001")) {
            this.setBackground(getResources().getDrawable(R.drawable.round_button));
        }
        else if(actor.deviceId.equals("4444"))
        {
            this.setBackground(getResources().getDrawable(R.drawable.round_button_yellow));
        }
        else
        {
            this.setBackground(getResources().getDrawable(R.drawable.round_button_blau));
        }
    }
}
