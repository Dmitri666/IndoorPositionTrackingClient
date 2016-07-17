package com.lps.lpsapp.services;

import com.lps.lpsapp.viewModel.chat.DevicePosition;

/**
 * Created by dle on 20.08.2015.
 */
public interface DevicePositionNotifier {
    void positionChanged(DevicePosition position);
}
