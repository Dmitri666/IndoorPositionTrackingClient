package com.lps.core.webapi;


import android.os.AsyncTask;
import android.util.Log;

import com.lps.lpsapp.services.AuthenticationService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by dle on 23.07.2015.
 */
public class HttpPostTask extends AsyncTask<String, Void, AsyncTaskResult<String>> {
    private static String TAG = "HttpPostTask";
    private Exception exception;
    private boolean mAuthenticate;
    private IHttpResultListener mResultListener;
    private String mParameter;

    public HttpPostTask(String parameter, IHttpResultListener resultListener, boolean authenticate) {

        this.mAuthenticate = authenticate;
        this.mResultListener = resultListener;
        this.mParameter = parameter;
    }

    public HttpPostTask(String parameter, boolean authenticate) {

        this.mAuthenticate = authenticate;
        this.mParameter = parameter;
    }

    protected AsyncTaskResult<String> doInBackground(String... args) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(args[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (AuthenticationService.authenticationData != null && mAuthenticate) {
                urlConnection.addRequestProperty("Authorization", "Bearer " + AuthenticationService.authenticationData.access_token);
            }
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            //Send request
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(mParameter);
            wr.flush();
            wr.close();

            //Get Response
            if(mResultListener == null)
            {
                //return null;
            }

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
            return null;
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    protected void onPostExecute(AsyncTaskResult<String> feed) {
        try {
            if(mResultListener != null && feed != null) {
                mResultListener.OnResult(feed);
            }
            // TODO: check this.exception
            // TODO: do something with the feed
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }


}
