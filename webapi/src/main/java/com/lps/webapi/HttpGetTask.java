package com.lps.webapi;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by dle on 23.07.2015.
 */
public class HttpGetTask extends AsyncTask<String, Void, AsyncTaskResult<String>> {
    private static String TAG = "HttpGetTask";
    private Exception exception;
    private boolean mAuthenticate;
    private IHttpResultListener mResultListener;

    public HttpGetTask(IHttpResultListener consumer, boolean authenticate) {

        this.mAuthenticate = authenticate;
        this.mResultListener = consumer;
    }


    protected AsyncTaskResult<String> doInBackground(String... args) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(args[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (AccessToken.CurrentToken != null && mAuthenticate) {
                urlConnection.addRequestProperty("Authorization", "Bearer " + AccessToken.CurrentToken.access_token);
            }
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            reader.close();

            return new AsyncTaskResult<String>(out.toString());
        } catch (FileNotFoundException e) {
            try {
                int rCode = urlConnection.getResponseCode();
                if (rCode == 401) {
                    return new AsyncTaskResult<String>(new AuthenticationException());
                } else {
                    InputStream err = urlConnection.getErrorStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(err));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    String eror = response.toString();
                    Log.e(TAG, eror, e);
                }
            } catch (IOException ee) {
                Log.e(TAG, e.getMessage(), e);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            this.exception = e;
            new AsyncTaskResult<String>(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    protected void onPostExecute(AsyncTaskResult<String> feed) {
        try {
            if(feed != null) {
                mResultListener.OnResult(feed);
            }
            // TODO: check this.exception
            // TODO: do something with the feed
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }
}





