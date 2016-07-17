package com.lps.lpsapp.management;

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
        Log.d(TAG, intent.getAction());

    }
}