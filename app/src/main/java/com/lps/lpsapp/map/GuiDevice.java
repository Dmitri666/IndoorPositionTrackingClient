package com.lps.lpsapp.map;

import android.content.Context;
import android.widget.Button;

import com.lps.lpsapp.R;

/**
 * Created by user on 28.07.2015.
 */
public class GuiDevice extends Button {
    protected static int[] STATE_SELF = {R.attr.state_isSelf};
    protected static int[] STATE_NOTSELF = {R.attr.state_notSelf};

    public String deviceId;

    private boolean isSelf;

    public GuiDevice(Context context, String deviceId, boolean isSelf) {
        super(context);
        this.setBackgroundResource(R.drawable.actor);
        this.isSelf = isSelf;

        this.deviceId = deviceId;

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
