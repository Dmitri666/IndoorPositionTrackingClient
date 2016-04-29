package com.lps.lpsapp.viewModel.booking;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.UUID;

/**
 * Created by dle on 05.10.2015.
 */
public class BookingRequest {
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy'-'MM'-'dd'T'HH':'mm", timezone="GMT")
    public Date time;

    public int peopleCount;

    public UUID tableId;

    public BookingRequest()
    {

    }
}
