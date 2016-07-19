package com.lps.lpsapp.viewModel.rooms;

import java.util.UUID;

/**
 * Created by dle on 14.08.2015.
 */
public class RoomInfo {
    public UUID id;

    public String name;

    /// <summary>
    /// Gets or sets the lat.
    /// </summary>
    public double lat;

    /// <summary>
    /// Gets or sets the lng.
    /// </summary>
    public double lng;

    public String imageFileName;

    public boolean isChatExist;

    public double rating;
    public String city;
    public boolean isFavorite;

    public RoomInfo() {

    }

}
