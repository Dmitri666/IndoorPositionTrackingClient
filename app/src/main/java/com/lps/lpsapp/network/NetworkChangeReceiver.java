package com.lps.lpsapp.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by dle on 01.06.2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
private static String TAG = "NetworkChangeReceiver";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectionDetector detector = new ConnectionDetector(context);
        Boolean status = detector.isConnectingToInternet();
        Log.d(TAG, "Sulod sa network reciever");
        if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if(status==NetworkUtil.NETWORK_STATUS_NOT_CONNECTED){
                new ForceExitPause(context).execute();
            }else{
                new ResumeForceExitPause(context).execute();
            }

        }
    }
}