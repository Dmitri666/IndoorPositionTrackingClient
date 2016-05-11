package com.lps.lpsapp.viewModel.rooms;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lps.lpsapp.viewModel.booking.TableState;

import java.util.UUID;

/**
 * Created by dle on 27.07.2015.
 */
public class Table  {
    @JsonIgnore
    private TableState bookingState;

    @JsonIgnore
    private Boolean selected;

    @JsonIgnore
    public View guiElement;

    @JsonIgnore
    public final Animation animation = new AlphaAnimation(1, 0);


    public double wight;


    public double height;


    public UUID id;
    public double x;
    public double y;

    public double angle;
    public String type;

    public Table()
    {
        this.selected = false;
        this.bookingState = new TableState();
        this.bookingState.state = 0;
        this.bookingState.tableId = this.id;

        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);
    }

    public void setBookingState(TableState state)
    {
       /* bookingState = state;
        if(state.getTableState() == TableStateEnum.Waiting) {
            this.guiElement.startAnimation(this.animation);
            this.guiElement.setColorFilter(Color.BLUE);
        }
        else {
            this.guiElement.clearAnimation();
            if (state.getTableState() == TableStateEnum.Free) {
                this.guiElement.setColorFilter(Color.GREEN);
            } else if (state.getTableState() == TableStateEnum.Booked) {
                this.guiElement.setColorFilter(Color.RED);
            } else if (state.getTableState() == TableStateEnum.BookedForMe) {
                this.guiElement.setColorFilter(Color.BLUE);
            } else if (state.getTableState() == TableStateEnum.Rejected) {
                this.guiElement.setColorFilter(Color.RED);
            }
        }*/
    }

    public TableState getBookingState()
    {
        return this.bookingState;
    }

    public Boolean getSelected()
    {
        return this.selected;
    }

    public void setSelected(Boolean selected)
    {
        this.selected = selected;
        if(selected) {
            //this.guiElement.setColorFilter(Color.YELLOW);
        }
        else
        {
            this.setBookingState(this.bookingState);
        }
    }
}
