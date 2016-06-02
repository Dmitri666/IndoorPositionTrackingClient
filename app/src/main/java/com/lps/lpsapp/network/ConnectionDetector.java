package com.lps.lpsapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.InetAddress;

public class ConnectionDetector {
    private static String TAG = "ConnectionDetector";
    private Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }

    public boolean isConnectedToNetwork(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
            {
                Log.d(TAG,Integer.toString(info.getType()));
                return info.isConnectedOrConnecting();
            }
        }
        return false;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }
}
