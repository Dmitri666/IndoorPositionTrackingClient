package com.lps.lpsapp;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import com.lps.lpsapp.activities.LoginActivity;
import com.lps.lpsapp.network.AppState;
import com.lps.lpsapp.network.IAppStateListener;
import com.lps.lpsapp.services.InDoorPositionService;
import com.lps.lpsapp.services.PushService;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.webapi.AccessToken;
import com.lps.webapi.IWebApiResultListener;
import com.lps.webapi.services.WebApiService;

import org.altbeacon.beacon.BleNotAvailableException;

/**
 * Created by dle on 08.07.2016.
 */
public class ServiceManager {
    public static AppState AppState = new AppState();
    protected static LpsApplication app;
    private static Intent beaconService;
    private static Intent puchService;

    public static void CheckSeviceAvalability(final IAppStateListener appStateListener) {

        Boolean mConnectedToInternet = ServiceManager.IsConnectedToNetwork();
        if(!mConnectedToInternet) {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            app.startActivity(intent);
        } else {
            puchService = new Intent(app, PushService.class);
            beaconService = new Intent(app, InDoorPositionService.class);
            String path = WebApiActions.IsAuthenticated();
            WebApiService service = new WebApiService(Boolean.class,true);
            service.performGet(path, new IWebApiResultListener() {
                @Override
                public void onResult(Object objResult) {
                    AppState.IsAuthenticated = true;
                    app.startService(beaconService);
                    app.startService(puchService);
                    app.GoIntoConnectedState();
                    appStateListener.StateChanged(AppState);
                }

                @Override
                public void onError(Exception err) {
                    AppState.IsAuthenticated = false;
                    Intent intent = new Intent(app,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    app.startActivity(intent);
                }
            });
        }

    }

    public static void LogOut(){
        app.stopService(beaconService);
        app.stopService(puchService);
        AppState.IsAuthenticated = false;
        AccessToken.CurrentToken = null;
        app.saveAuthenticationData(null);
    }

    public static void OnLogIn(AccessToken accessToken) {
        if(!AppState.IsAuthenticated || AccessToken.CurrentToken == null || !AccessToken.CurrentToken.userName.equals(accessToken.userName)) {
            AppState.IsAuthenticated = true;
            app.stopService(puchService);
            AccessToken.CurrentToken = accessToken;
            app.saveAuthenticationData(accessToken);
            app.startService(puchService);
        } else {
            AccessToken.CurrentToken = accessToken;
            app.saveAuthenticationData(accessToken);
        }
    }
    private static boolean IsConnectedToNetwork(){
        ConnectivityManager connectivity = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
            {
                AppState.IsConnectedToInternet =  info.isConnectedOrConnecting();
                return AppState.IsConnectedToInternet;
            }
        }

        AppState.IsConnectedToInternet = false;
        return AppState.IsConnectedToInternet;
    }


    @TargetApi(18)
    private static boolean CheckBleAvailability() throws BleNotAvailableException {
        if(Build.VERSION.SDK_INT < 18) {
            throw new BleNotAvailableException("Bluetooth LE not supported by this device");
        } else if(!app.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            throw new BleNotAvailableException("Bluetooth LE not supported by this device");
        } else {
            return ((BluetoothManager)app.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled();
        }
    }

}
