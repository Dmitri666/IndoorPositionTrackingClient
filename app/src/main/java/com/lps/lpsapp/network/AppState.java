package com.lps.lpsapp.network;

import java.util.UUID;

/**
 * Created by dle on 11.07.2016.
 */
public class AppState {
    public Boolean IsConnectedToInternet;
    public Boolean IsBlootuthOn;
    public Boolean IsAuthenticated;
    public UUID LocaleId;

    public AppState() {
        this.IsAuthenticated = false;
        this.IsBlootuthOn = false;
        this.IsConnectedToInternet = false;
        this.LocaleId = null;
    }
}
