package com.lps.lpsapp.viewModel.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.UUID;

/**
 * Created by dle on 14.10.2015.
 */
public class BookingData {
    public BookingData()
    {

    }

    public UUID bookingId;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy'-'MM'-'dd'T'HH':'mm':'ss")
    public Date time;

    public UUID tableId;

    public int peopleCount;

    public String tableClientName;

    public int state;

    @JsonIgnore
    public BookingStateEnum getBookingState()
    {
        if(state == 0) {
            return BookingStateEnum.Waiting;
        }
        else if(state == 1) {
            return BookingStateEnum.Accepted;
        }
        else if(state == 2) {
            return BookingStateEnum.Rejected;
        }
        else if(state == 3) {
            return BookingStateEnum.Canceled;
        }
        return BookingStateEnum.Waiting;
    }

}
