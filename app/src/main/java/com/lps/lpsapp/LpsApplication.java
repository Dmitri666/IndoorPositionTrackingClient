package com.lps.lpsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.lps.core.webapi.AccessToken;
import com.lps.core.webapi.JsonSerializer;
import com.lps.lpsapp.activities.LoginActivity;
import com.lps.lpsapp.activities.SettingsActivity;
import com.lps.lpsapp.services.AltBeaconService;
import com.lps.lpsapp.services.AuthenticationService;
import com.lps.lpsapp.services.PushService;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.services.WebApiService;
import com.lps.lpsapp.viewModel.Device;
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
        mAndroidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        AuthenticationService.authenticationData = this.getAuthenticationData();
        AuthenticationService.currentApplication = this;
        AndroidModel model = AndroidModel.forThisDevice();
        Device device = new Device(this.getAndroidId(), model.getBuildNumber(), model.getManufacturer(), model.getModel(), model.getVersion());

        WebApiService service = new WebApiService(Device.class,false);
        service.performPost(WebApiActions.RegisterDevice(),device);


        beaconService = new Intent(this, AltBeaconService.class);
        startService(beaconService);
        if(AuthenticationService.authenticationData != null) {
            puchService = new Intent(this, PushService.class);
            startService(puchService);
        }

        SharedPreferences settings = getSharedPreferences("settings", 0);
        SettingsActivity.SendToServer = settings.getBoolean("sendToServer", false);

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

    public void saveAuthenticationData() {
        if(AuthenticationService.authenticationData != null) {
            try {
                String token = JsonSerializer.serialize(AuthenticationService.authenticationData);
                SharedPreferences settings = getSharedPreferences("token", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("token", token);
                editor.commit();

                puchService = new Intent(this, PushService.class);
                startService(puchService);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }

        }
    }

    public void saveBaeconCountSetting(int beaconCount)
    {
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("beaconCount", beaconCount);
        editor.commit();
    }

    public void saveSendToServerSetting(Boolean sendToServer)
    {
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("sendToServer", sendToServer);
        editor.commit();
    }

    public void Authenticate()
    {
        Intent myIntent = new Intent(this, LoginActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(myIntent);
    }

}