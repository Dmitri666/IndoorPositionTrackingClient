package com.lps.lpsapp.services;

import android.util.Log;

import com.lps.core.webapi.AsyncTaskResult;
import com.lps.core.webapi.AuthenticationException;
import com.lps.core.webapi.HttpGetTask;
import com.lps.core.webapi.HttpPostTask;
import com.lps.core.webapi.IHttpResultListener;
import com.lps.core.webapi.IWebApiResultListener;
import com.lps.core.webapi.JsonSerializer;

import java.io.IOException;
import java.util.List;

/**
 * Created by dle on 25.11.2015.
 */
public class WebApiService<T> {
    private static String TAG = "WebApiService";
    final Class<T> typeParameterClass;
    private boolean needAuthentication;

    public WebApiService(Class<T> typeParameterClass,boolean needAuthentication) {
        this.typeParameterClass = typeParameterClass;
        this.needAuthentication = needAuthentication;
    }

    public void performGet(String path,final IWebApiResultListener<T> resultListener)
    {
        new HttpGetTask(new IHttpResultListener() {
            @Override
            public void OnResult(AsyncTaskResult<String> result) {
                if(result.getError() instanceof AuthenticationException)
                {
                    AuthenticationService.currentApplication.Authenticate();
                    return;
                }
                if(result.getResult().length() > 0)
                {
                    try {
                        T objResult = JsonSerializer.deserialize(result.getResult(), typeParameterClass);
                        resultListener.onResult(objResult);
                    }
                    catch (IOException ex)
                    {
                        Log.e(TAG,ex.getMessage(),ex);
                    }
                }
            }
        },needAuthentication).execute(path);
    }

    public void performGetList(String path,final IWebApiResultListener<List<T>> resultListener)
    {
        new HttpGetTask(new IHttpResultListener() {
            @Override
            public void OnResult(AsyncTaskResult<String> result) {
                if(result.getError() instanceof AuthenticationException)
                {
                    AuthenticationService.currentApplication.Authenticate();
                    return;
                }

                if(result.getResult().length() > 0)
                {
                    try {
                        List<T> objResult = JsonSerializer.deserializeList(result.getResult(), typeParameterClass);
                        resultListener.onResult(objResult);
                    }
                    catch (IOException ex)
                    {
                        Log.e(TAG,ex.getMessage(),ex);
                    }
                }
            }
        },needAuthentication).execute(path);
    }

    public <P> void performPost(String path,P parameter, final IWebApiResultListener<T> resultListener)
    {
        try {
            String strParameter = JsonSerializer.serialize(parameter);
            new HttpPostTask(strParameter,new IHttpResultListener() {
                @Override
                public void OnResult(AsyncTaskResult<String> result) {
                    if(result.getError() instanceof AuthenticationException)
                    {
                        AuthenticationService.currentApplication.Authenticate();
                        return;
                    }
                    if(result.getResult().length() > 0)
                    {
                        try {
                            T objResult = JsonSerializer.deserialize(result.getResult(), typeParameterClass);
                            resultListener.onResult(objResult);
                        }
                        catch (IOException ex)
                        {
                            Log.e(TAG,ex.getMessage(),ex);
                        }
                    }
                }
            },needAuthentication).execute(path);
        }
        catch (IOException ex)
        {
            Log.e(TAG,ex.getMessage(),ex);
        }

    }

    public <P> void performPostList(String path,P parameter, final IWebApiResultListener<List<T>> resultListener)
    {
        try {
            String strParameter = JsonSerializer.serialize(parameter);
            new HttpPostTask(strParameter,new IHttpResultListener() {
                @Override
                public void OnResult(AsyncTaskResult<String> result) {
                    if(result.getError() instanceof AuthenticationException)
                    {
                        AuthenticationService.currentApplication.Authenticate();
                        return;
                    }
                    if(result.getResult().length() > 0)
                    {
                        try {
                            List<T> objResult = JsonSerializer.deserializeList(result.getResult(), typeParameterClass);
                            resultListener.onResult(objResult);
                        }
                        catch (IOException ex)
                        {
                            Log.e(TAG,ex.getMessage(),ex);
                        }
                    }
                }
            },needAuthentication).execute(path);
        }
        catch (IOException ex)
        {
            Log.e(TAG,ex.getMessage(),ex);
        }

    }

    public void performPost(String path,T parameter)
    {
        try {
            String strParameter = JsonSerializer.serialize(parameter);
            new HttpPostTask(strParameter,needAuthentication).execute(path);
        }
        catch (IOException ex)
        {
            Log.e(TAG,ex.getMessage(),ex);
        }

    }
}
