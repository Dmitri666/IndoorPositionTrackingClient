package com.lps.lpsapp.services;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by dle on 20.08.2015.
 */
public interface IBeaconServiceListener {
    void beaconsInRange(Collection<Beacon> beacon,Region region);
    void deviceInLocale(UUID localeId,boolean isInLocale);
}
