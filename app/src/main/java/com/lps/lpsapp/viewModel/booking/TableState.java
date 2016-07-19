package com.lps.lpsapp.viewModel.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;


/**
 * Created by dle on 06.10.2015.
 */
public class TableState {
    public UUID bookingId;
    public UUID tableId;
    public int state;
    public TableState() {

    }

    @JsonIgnore
    public TableStateEnum getTableState() {
        if (state == 0) {
            return TableStateEnum.Free;
        } else if (state == 1) {
            return TableStateEnum.Booked;
        } else if (state == 2) {
            return TableStateEnum.BookedForMe;
        } else if (state == 3) {
            return TableStateEnum.Rejected;
        } else if (state == 4) {
            return TableStateEnum.Waiting;
        }
        return TableStateEnum.Free;
    }

    @JsonIgnore
    public void setTableState(TableStateEnum state) {
        if (state == TableStateEnum.Free) {
            this.state = 0;
        } else if (state == TableStateEnum.Booked) {
            this.state = 1;
        } else if (state == TableStateEnum.BookedForMe) {
            this.state = 2;
        } else if (state == TableStateEnum.Rejected) {
            this.state = 3;
        } else if (state == TableStateEnum.Waiting) {
            this.state = 4;
        }

    }
}
