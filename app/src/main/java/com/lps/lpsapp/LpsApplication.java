package com.lps.lpsapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.lps.lpsapp.activities.SettingsActivity;
import com.lps.lpsapp.network.ConnectionDetector;
import com.lps.lpsapp.services.AltBeaconService;
import com.lps.lpsapp.services.PushService;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.viewModel.Device;
import com.lps.webapi.AccessToken;
import com.lps.webapi.IAuthenticationListener;
import com.lps.webapi.JsonSerializer;
import com.lps.webapi.services.WebApiService;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.altbeacon.beacon.distance.AndroidModel;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;

/**
 * Created by dyoung on 12/13/13.
 */
public class LpsApplication extends MultiDexApplication {
    private static final String TAG = "LpsApplication";
    private static Context mContext;
    private AltBeaconService beaconService;
    private Intent puchService;
    private String mAndroidId;
    private Boolean mIsInternetAvailable;

    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        mContext = this;
        Platform.loadPlatformComponent(new AndroidPlatformComponent());



        SharedPreferences settings = getSharedPreferences("settings", 0);
        String url = settings.getString("url",null);
        if(url == null) {
            SharedPreferences.Editor editor = settings.edit();
            String defaultUrl = this.getResources().getString(R.string.serverUrl);
            editor.putString("url", defaultUrl);
            editor.commit();
            SettingsActivity.WebApiUrl = defaultUrl;
        } else {
            SettingsActivity.WebApiUrl = url;
        }

        puchService = new Intent(this, PushService.class);

        mAndroidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        WebApiService.AuthenticationListener = new IAuthenticationListener() {
            @Override
            public void Autenticate() {
                ShowLogin();
            }
        };

        AccessToken.CurrentToken = this.getAuthenticationData();

        Intent intent = new Intent(this, AltBeaconService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);



    }

    public static RefWatcher getRefWatcher(Context context) {
        return refWatcher;
    }

    private static RefWatcher refWatcher;

    public static Context getContext() {
        return mContext;
    }

    public String getAndroidId() {
        return mAndroidId;
    }

    /**/
    @Override
    public void onTerminate() {
        // Unbind from the service
        //stopService(beaconService);
        //beaconService = null;
        super.onTerminate();


    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AltBeaconService.LocalBinder binder = (AltBeaconService.LocalBinder) service;
            beaconService = binder.getService();
            ((LpsApplication)getApplicationContext()).CheckInternetAvailability();//mBound = true;
            //mService.setBeaconServiceListener(listener);
            //mService.monitoreBackgroundRegion(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            //mBound = false;

        }
    };

    private AccessToken getAuthenticationData() {
        SharedPreferences settings = getSharedPreferences("token", 0);
        String token = settings.getString("token", null);
        if (token == null) {
            return null;
        }

        AccessToken accesstoken = null;
        try {
            accesstoken = JsonSerializer.deserialize(token, AccessToken.class);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return accesstoken;
    }

    public void saveAuthenticationData(AccessToken authenticationData) {
        if(authenticationData != null) {
            try {
                String token = JsonSerializer.serialize(authenticationData);
                SharedPreferences settings = getSharedPreferences("token", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("token", token);
                editor.commit();
                AccessToken.CurrentToken = authenticationData;

                if(puchService == null) {
                    puchService = new Intent(this, PushService.class);
                    startService(puchService);
                } else {
                    stopService(puchService);
                    startService(puchService);
                }


            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }

        }
    }

    public void ShowLogin()
    {
        //if (AccessToken.CurrentToken == null) {
            //Intent myIntent = new Intent(this, LoginActivity.class);
            //myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //this.startActivity(myIntent);
        //} else {
        //    new AuthenticationService().RefreshToken(this);
        //}

    }

    public void CheckInternetAvailability() {
        ConnectionDetector cd = new ConnectionDetector(this);
        Boolean state = cd.isConnectedToNetwork();
        if(this.mIsInternetAvailable == null ||this.mIsInternetAvailable != state) {
            this.mIsInternetAvailable = state;
            if(this.mIsInternetAvailable) {
                this.GoIntoConnectedState();
            } else {
                this.GoIntoDisconnectedState();
            }
        }

    }

    private void GoIntoConnectedState() {
        Toast toast1 = Toast.makeText(getApplicationContext(), "Connected Internet", Toast.LENGTH_LONG);
        toast1.show();

        AndroidModel model = AndroidModel.forThisDevice();
        Device device = new Device(this.getAndroidId(), model.getBuildNumber(), model.getManufacturer(), model.getModel(), model.getVersion());

        WebApiService service = new WebApiService(Device.class,false);
        service.performPost(WebApiActions.RegisterDevice(),device);


        if(AccessToken.CurrentToken != null) {

            startService(puchService);
        }

        beaconService.InitRegionBootstrap();
    }

    private void GoIntoDisconnectedState() {
        Toast toast1 = Toast.makeText(getApplicationContext(), "Not connected Internet", Toast.LENGTH_LONG);
        toast1.show();
        ComponentName cn =  puchService.getComponent();
        stopService(puchService);
    }

//    private boolean isNetworkConnected() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkRequest.Builder builder = new NetworkRequest.Builder();
//
//        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED);
//        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
//        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
//        NetworkRequest networkRequest = builder.build();
//        //cm.requestNetwork(networkRequest, networkCallback);
//        //cm.registerNetworkCallback(networkRequest, new ConnectionDetector());
//
//        cm.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
//            @Override
//            public void onNetworkActive() {
//                Log.d(TAG,"NetworkActive");
//            }
//        });
//        NetworkInfo info = cm.getActiveNetworkInfo();
//        if(info == null) {
//            return false;
//        }
//        return info.isConnected();
//    }

}