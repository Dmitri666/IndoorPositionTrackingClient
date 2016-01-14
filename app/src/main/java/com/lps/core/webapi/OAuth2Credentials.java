package com.lps.core.webapi;

import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.http.Request;

/**
 * Created by dle on 06.08.2015.
 */
public class OAuth2Credentials implements Credentials {
    private String accessToken;
    public OAuth2Credentials(String accessToken)
    {
        this.accessToken = accessToken;
    }

    @Override
    public void prepareRequest(Request request) {
        request.addHeader("Authorization", "Bearer " + accessToken);
    }
}
