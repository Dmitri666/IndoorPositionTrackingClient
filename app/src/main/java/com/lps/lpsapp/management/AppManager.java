package com.lps.lpsapp.management;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.lps.lpsapp.LpsApplication;
import com.lps.lpsapp.activities.SettingsActivity;
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
public class AppManager {
    private static String TAG = "AppManager";
    public AppState AppState;
    private LpsApplication app;


    private boolean mPushServiceBound = false;
    private PushService mPushService;
    private boolean mPositionServiceBound = false;
    private InDoorPositionService mPositionService;

    private static AppManager instance;

    protected AppManager() {
        this.app = (LpsApplication) LpsApplication.getContext();
        this.AppState = new AppState();


    }

    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    public void CheckIsAuthenticated(final AppStateNotifier appStateListener) {

        String path = WebApiActions.IsAuthenticated();
        WebApiService service = new WebApiService(Boolean.class, true);
        service.performGet(path, new IWebApiResultListener() {
            @Override
            public void onResult(Object objResult) {
                AppState.IsAuthenticated = true;
                if (!mPushServiceBound) {
                    app.bindService(new Intent(app, PushService.class), mPushServiceConnection, Context.BIND_AUTO_CREATE);
                }
                if (!mPositionServiceBound) {
                    app.bindService(new Intent(app, InDoorPositionService.class), mPositionServiceConnection, Context.BIND_AUTO_CREATE);
                }
                appStateListener.StateChanged(AppState);
            }

            @Override
            public void onError(Exception err) {
                AppState.IsAuthenticated = false;
                appStateListener.StateChanged(AppState);
            }
        });
    }

    public void CheckSeviceAvalability() {

        this.AppState.IsConnectedToInternet = this.IsConnectedToNetwork();
        this.AppState.IsBlootuthOn = this.CheckBleAvailability();
        if(this.AppState.IsConnectedToInternet && mPositionServiceBound) {
            this.mPositionService.InitRegionBootstrap();
        }
    }


    public void LogOut() {
        AppState.IsAuthenticated = false;
        AccessToken.CurrentToken = null;
        app.saveAuthenticationData(null);

    }

    public void OnLogIn(AccessToken accessToken) {
        AppState.IsAuthenticated = true;
        AccessToken.CurrentToken = accessToken;
        app.saveAuthenticationData(accessToken);
        if (!mPushServiceBound) {
            app.bindService(new Intent(app, PushService.class), mPushServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            this.mPushService.resetCredentials();
        }
        if (!mPositionServiceBound) {
            app.bindService(new Intent(app, InDoorPositionService.class), mPositionServiceConnection, Context.BIND_AUTO_CREATE);
        }


    }

    private boolean IsConnectedToNetwork() {
        Boolean result = false;
        ConnectivityManager connectivity = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {
                result = info.isConnectedOrConnecting();
            }
        }

        return result;
    }


    @TargetApi(18)
    private boolean CheckBleAvailability() throws BleNotAvailableException {
        if (SettingsActivity.UseBeaconSimulator) {
            return true;
        }
        if (Build.VERSION.SDK_INT < 18) {
            Log.e(TAG, "Bluetooth LE not supported by this device");
            return false;
        } else if (!app.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Log.e(TAG, "Bluetooth LE not supported by this device");
            return false;
        } else {
            return ((BluetoothManager) app.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter().isEnabled();
        }
    }


    private ServiceConnection mPushServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PushService.LocalBinder binder = (PushService.LocalBinder) service;
            mPushService = binder.getService();
            mPushServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mPushServiceBound = false;
        }
    };


    private ServiceConnection mPositionServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            InDoorPositionService.LocalBinder binder = (InDoorPositionService.LocalBinder) service;
            mPositionService = binder.getService();
            mPositionServiceBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mPositionServiceBound = false;


        }
    };

}
