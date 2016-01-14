package com.lps.lpsapp.viewModel.booking;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.UUID;

/**
 * Created by dle on 16.10.2015.
 */
public class TableReservationModelRequest {

    public TableReservationModelRequest()
    {

    }

    public UUID roomId;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy'-'MM'-'dd'T'HH':'mm':'ss")
    public Date time;
}
