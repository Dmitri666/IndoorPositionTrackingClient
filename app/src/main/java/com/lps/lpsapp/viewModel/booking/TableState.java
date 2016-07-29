package com.lps.lpsapp.viewModel.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;


/**
 * Created by dle on 06.10.2015.
 */
public class TableState {
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
}
