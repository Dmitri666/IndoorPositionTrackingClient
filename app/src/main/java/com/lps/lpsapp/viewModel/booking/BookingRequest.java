package com.lps.lpsapp.viewModel.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by dle on 05.10.2015.
 */
public class BookingRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy'-'MM'-'dd'T'HH':'mm", timezone = "GMT")
    public Date time;

    public int peopleCount;

    @JsonDeserialize(as = ArrayList.class, contentAs = UUID.class)
    public List<UUID> tables;

    public BookingRequest() {
        this.tables = new ArrayList<>();
    }
}
