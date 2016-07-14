package com.lps.lpsapp.management;

import java.util.UUID;

/**
 * Created by dle on 11.07.2016.
 */
public class AppState {
    protected Boolean IsConnectedToInternet;
    protected Boolean IsBlootuthOn;
    protected Boolean IsAuthenticated;
    protected UUID localeId;

    public AppState() {
        this.IsAuthenticated = false;
        this.IsBlootuthOn = false;
        this.IsConnectedToInternet = false;
        this.localeId = null;
    }

    public UUID getCurrentLocaleId() {
        return this.localeId;
    }

    public void setCurrentLocaleId(UUID localeId) {
        this.localeId = localeId;
    }

    public Boolean getIsAuthenticated() {
        return this.IsAuthenticated;
    }
}
