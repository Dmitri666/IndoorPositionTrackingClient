package com.lps.lpsapp.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lps.lpsapp.LpsApplication;

/**
 * Created by dle on 01.06.2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
private static String TAG = "NetworkChangeReceiver";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        LpsApplication app = (LpsApplication)context.getApplicationContext();
        app.CheckInternetAvailability();
        Log.d(TAG, intent.getAction());

    }
}