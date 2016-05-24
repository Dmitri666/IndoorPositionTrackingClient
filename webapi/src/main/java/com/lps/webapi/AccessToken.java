package com.lps.webapi;

/**
 * Created by dle on 16.10.2015.
 */
public class AccessToken {
    public static AccessToken CurrentToken;
    public String access_token;
    public String token_type;
    public int expires_in;
    public String refresh_token;
    public String userName;
    public AccessToken()
    {

    }
}
