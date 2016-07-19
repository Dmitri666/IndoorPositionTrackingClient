package com.lps.lpsapp.viewModel.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by dle on 14.10.2015.
 */
public class BookingData {
    public UUID bookingId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy'-'MM'-'dd'T'HH':'mm", timezone = "GMT")
    public Date time;
    @JsonDeserialize(as = ArrayList.class, contentAs = RoomTableData.class)
    public List<RoomTableData> roomTableDataList;
    public int peopleCount;
    public int state;
    @JsonIgnore
    public Date createTime;

    public BookingData() {

    }

    @JsonIgnore
    public BookingStateEnum getBookingState() {
        if (state == 0) {
            return BookingStateEnum.Waiting;
        } else if (state == 1) {
            return BookingStateEnum.Accepted;
        } else if (state == 2) {
            return BookingStateEnum.Rejected;
        } else if (state == 3) {
            return BookingStateEnum.Canceled;
        }
        return BookingStateEnum.Waiting;
    }

}
