package com.lps.core.webapi;

/**
 * Created by user on 25.10.2015.
 */
public interface IHttpResultListener {
    void OnResult(AsyncTaskResult<String> result);
}
