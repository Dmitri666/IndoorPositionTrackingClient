package com.lps.lpsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.lps.lpsapp.activities.SettingsActivity;
import com.lps.lpsapp.services.WebApiActions;
import com.lps.lpsapp.viewModel.Device;
import com.lps.webapi.AccessToken;
import com.lps.webapi.AuthenticationException;
import com.lps.webapi.JsonSerializer;
import com.lps.webapi.services.WebApiService;

import org.altbeacon.beacon.distance.AndroidModel;

import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.InvalidHttpStatusCodeException;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.transport.NegotiationException;

//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;

/**
 * Created by dyoung on 12/13/13.
 */
public class LpsApplication extends MultiDexApplication {
    private static final String TAG = "LpsApplication";
    private static Context mContext;
    private String mAndroidId;


    public void onCreate() {
        super.onCreate();
        //refWatcher = LeakCanary.install(this);
        mContext = this;
        ServiceManager.app = this;
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

        AccessToken.CurrentToken = this.getAuthenticationData();



    }

    //public static RefWatcher getRefWatcher(Context context) {
    //    return refWatcher;
    //}

    //private static RefWatcher refWatcher;

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

    protected void saveAuthenticationData(AccessToken authenticationData) {
        if(authenticationData != null) {
            try {
                String token = JsonSerializer.serialize(authenticationData);
                SharedPreferences settings = getSharedPreferences("token", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("token", token);
                editor.commit();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }

        } else {
            SharedPreferences settings = getSharedPreferences("token", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("token", null);
            editor.commit();
        }
    }


    public void HandleError(Exception ex) {
        if(ex instanceof AuthenticationException || ex instanceof NegotiationException || ex.getCause() instanceof InvalidHttpStatusCodeException) {
            ServiceManager.LogOut();
        } else {
            Log.e(TAG, ex.toString(), ex);
        }
    }

    public void GoIntoConnectedState() {
        Toast toast = Toast.makeText(getApplicationContext(), "Connected Internet", Toast.LENGTH_LONG);
        toast.show();
        AndroidModel model = AndroidModel.forThisDevice();
        Device device = new Device(this.getAndroidId(), model.getBuildNumber(), model.getManufacturer(), model.getModel(), model.getVersion());

        WebApiService service = new WebApiService(Device.class,false);
        service.performPost(WebApiActions.RegisterDevice(),device);


    }

    protected void GoIntoDisconnectedState() {
        Toast toast1 = Toast.makeText(getApplicationContext(), "Not connected Internet", Toast.LENGTH_LONG);
        toast1.show();

    }



}