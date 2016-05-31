package com.lps.lpsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.lps.lpsapp.activities.LoginActivity;
import com.lps.lpsapp.activities.SettingsActivity;
import com.lps.lpsapp.services.AltBeaconService;
import com.lps.lpsapp.services.AuthenticationService;
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
    private Intent beaconService;
    private Intent puchService;
    private String mAndroidId;

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


        mAndroidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        WebApiService.AuthenticationListener = new IAuthenticationListener() {
            @Override
            public void Autenticate() {
                ShowLogin();
            }
        };

        AccessToken.CurrentToken = this.getAuthenticationData();

        AndroidModel model = AndroidModel.forThisDevice();
        Device device = new Device(this.getAndroidId(), model.getBuildNumber(), model.getManufacturer(), model.getModel(), model.getVersion());

        WebApiService service = new WebApiService(Device.class,false);
        service.performPost(WebApiActions.RegisterDevice(),device);


        beaconService = new Intent(this, AltBeaconService.class);
        startService(beaconService);
        if(AccessToken.CurrentToken != null) {
            puchService = new Intent(this, PushService.class);
            startService(puchService);
        }
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
        stopService(beaconService);
        beaconService = null;
        super.onTerminate();


    }


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
        if (AccessToken.CurrentToken == null) {
            Intent myIntent = new Intent(this, LoginActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(myIntent);
        } else {
            new AuthenticationService().RefreshToken(this);
        }

    }

}