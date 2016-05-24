package com.lps.webapi;

/**
 * Created by dle on 25.11.2015.
 */
public interface IWebApiResultListener<T> {
    void onResult(T objResult);
}
