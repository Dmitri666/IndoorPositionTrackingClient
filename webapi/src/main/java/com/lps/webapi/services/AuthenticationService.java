package com.lps.webapi.services;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lps.webapi.AccessToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by user on 05.08.2015.
 */
public class AuthenticationService {
    private static String TAG = "AuthenticationService";

    public AccessToken Authenticate(String userName, String password,String androidId,String url) throws Exception {

        try {
            String data = "grant_type=password&client_id=" + URLEncoder.encode(androidId, "UTF-8") + "&username=" + URLEncoder.encode(userName, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");

            String accessToken = new GetTokenTask().execute(url,data).get();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            AccessToken token = mapper.readValue(accessToken, AccessToken.class);
            return token;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
            throw ex;
        }
    }

    private boolean running = false;

//    public synchronized void RefreshToken(String deviceId) {
//        try {
//            Log.d(TAG, "try refresh token");
//            if (running) {
//                return;
//            }
//            running = true;
//            String data = "grant_type=refresh_token&client_id=" + URLEncoder.encode(deviceId, "UTF-8") + "&refresh_token=" + URLEncoder.encode(AccessToken.CurrentToken.refresh_token, "UTF-8");
//
//            String accessToken = new GetTokenTask().execute(data).get();
//
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            AccessToken authenticationData = mapper.readValue(accessToken, AccessToken.class);
//            //app.saveAuthenticationData(authenticationData);
//            running = false;
//            Log.d(TAG, "refresh token");
//        } catch (Exception ex) {
//            Log.e(TAG, ex.getMessage(), ex);
//        }
//    }

    private class GetTokenTask extends AsyncTask<String, Void, String> {
        private Exception exception;
        private HttpURLConnection urlConnection;

        protected String doInBackground(String... args) {
            try {
                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlendcoded");

                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        urlConnection.getOutputStream());
                wr.writeBytes(args[1]);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = urlConnection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();


                return response.toString();


            } catch (Exception ex) {
                try {
                    InputStream err = urlConnection.getErrorStream();
                    int rCode = urlConnection.getResponseCode();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(err));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    String eror = response.toString();
                    Log.e(TAG, eror, ex);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                Log.e(TAG, ex.getMessage(), ex);
                this.exception = ex;
                return null;
            } finally {
                urlConnection.disconnect();
            }
        }

        protected void onPostExecute(byte[] feed) throws Exception {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }

    private class RefreshTokenTask extends AsyncTask<String, Void, String> {
        private Exception exception;
        private HttpURLConnection urlConnection;

        protected String doInBackground(String... args) {
            try {
                URL url = new URL(args[0]);
                String data = "grant_type=refresh_token&client_id=" + URLEncoder.encode(args[1], "UTF-8") + "&refresh_token=" + URLEncoder.encode(args[2], "UTF-8");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlendcoded");

                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        urlConnection.getOutputStream());
                wr.writeBytes(data);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = urlConnection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();


                return response.toString();


            } catch (Exception ex) {
                try {
                    InputStream err = urlConnection.getErrorStream();
                    int rCode = urlConnection.getResponseCode();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(err));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    String eror = response.toString();
                    Log.e(TAG, eror, ex);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                Log.e(TAG, ex.getMessage(), ex);
                this.exception = ex;
                return null;
            } finally {
                urlConnection.disconnect();
            }
        }

        protected void onPostExecute(byte[] feed) throws Exception {
            if (this.exception != null) {
                throw this.exception;
            }
        }
    }
}
