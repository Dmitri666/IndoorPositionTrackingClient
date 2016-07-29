package com.lps.lpsapp.map;

import android.content.Context;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;

import com.lps.lpsapp.R;

/**
 * Created by user on 28.07.2015.
 */
public class GuiDevice extends Button {
    protected static int[] STATE_SELF = {R.attr.state_isSelf};
    protected static int[] STATE_NOTSELF = {R.attr.state_notSelf};

    public String deviceId;
    public float wight;
    public float height;

    private boolean isSelf;

    public GuiDevice(Context context, String deviceId, boolean isSelf) {
        super(context);
        this.setBackgroundResource(R.drawable.actor);
        this.isSelf = isSelf;
        this.wight = 40f;
        this.height = 40f;
        this.deviceId = deviceId;
        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                int size = 100;
                outline.setOval(0, 0, size, size);
            }
        };
        //this.setOutlineProvider(viewOutlineProvider);
        //this.setShadowLayer(24, 24, 24, Color.RED);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        if (this.isSelected()) {
            return super.onCreateDrawableState(extraSpace);
        }

        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.isSelf) {
            mergeDrawableStates(drawableState, STATE_SELF);
        } else {
            mergeDrawableStates(drawableState, STATE_NOTSELF);
        }

        return drawableState;
    }
}
