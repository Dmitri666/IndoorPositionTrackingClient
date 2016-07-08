package com.lps.lpsapp;

import com.lps.lpsapp.network.ConnectionDetector;

/**
 * Created by dle on 08.07.2016.
 */
public class AppManager {
    private static Boolean mIsConnectedToInternet;
    private static Boolean mBlootuthOn;
    private static Boolean mIsAutenticated;
    protected static LpsApplication app;

    public static Boolean CheckInternatAvalability() {
        ConnectionDetector detector = new ConnectionDetector(app);
        Boolean connected = detector.isConnectedToNetwork();
        if(mIsConnectedToInternet == null || mIsConnectedToInternet != connected) {
            mIsConnectedToInternet = connected;
            if(mIsConnectedToInternet) {
                app.GoIntoConnectedState();
            } else {
                app.GoIntoDisconnectedState();
            }
        }
        return connected;
    }

}
