package com.lps.lpsapp.map;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.lps.lpsapp.R;
import com.lps.lpsapp.viewModel.booking.TableState;
import com.lps.lpsapp.viewModel.booking.TableStateEnum;

/**
 * Created by dle on 20.07.2016.
 */
public class GuiTable extends ImageView {
    protected static int[] STATE_FREE = {R.attr.state_free};
    protected static int[] STATE_BOOKED = {R.attr.state_booked};
    protected static int[] STATE_BOOKEDFORME = {R.attr.state_bookedForMe};
    protected static int[] STATE_REJECTED = {R.attr.state_rejected};
    protected static int[] STATE_WAITING = {R.attr.state_waiting};

    private final Animation animation = new AlphaAnimation(1, 0);

    private TableStateEnum mState;

    public GuiTable(Context ctx, int tableType) {
        super(ctx);
        if (tableType == 1) {
            this.setImageResource(R.drawable.table1);
        } else if (tableType == 2) {
            this.setImageResource(R.drawable.table2);
        } else if (tableType == 3) {
            this.setImageResource(R.drawable.table3);
        } else if (tableType == 4) {
            this.setImageResource(R.drawable.table4);
        }
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        if (this.isSelected()) {
            return super.onCreateDrawableState(extraSpace);
        }

        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mState == TableStateEnum.Free) {
            mergeDrawableStates(drawableState, STATE_FREE);
        } else if (mState == TableStateEnum.Booked) {
            mergeDrawableStates(drawableState, STATE_BOOKED);
        } else if (mState == TableStateEnum.BookedForMe) {
            mergeDrawableStates(drawableState, STATE_BOOKEDFORME);
        } else if (mState == TableStateEnum.Rejected) {
            mergeDrawableStates(drawableState, STATE_REJECTED);
        } else if (mState == TableStateEnum.Waiting) {
            mergeDrawableStates(drawableState, STATE_WAITING);
        }

        return drawableState;
    }

    private void onStateChanged() {
        if (this.mState == TableStateEnum.Waiting) {
            this.startAnimation(this.animation);
        } else {
            this.clearAnimation();
            this.refreshDrawableState();
        }
    }

    public void setState(TableState state) {
        this.mState = state.getTableState();
        this.onStateChanged();
    }

    public void setState(TableStateEnum state) {
        this.mState = state;
        this.onStateChanged();
    }

}
