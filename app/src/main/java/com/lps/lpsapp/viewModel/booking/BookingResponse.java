package com.lps.lpsapp.viewModel.booking;

import java.util.UUID;

/**
 * Created by dle on 10.11.2015.
 */
public class BookingResponse {
    public Boolean accepted;
    public UUID bookingId;
    public BookingResponse() {
        this.accepted = false;
    }
}
