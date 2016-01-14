package com.lps.lpsapp.services;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by dle on 20.08.2015.
 */
public interface IBeaconServiceListener {
    void beaconsInRange(Collection<Beacon> beacon);
    void deviceInLocale(UUID localeId,boolean isInLocale);
}
