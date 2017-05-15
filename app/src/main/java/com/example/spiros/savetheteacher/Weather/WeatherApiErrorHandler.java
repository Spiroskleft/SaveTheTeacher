package com.example.spiros.savetheteacher.Weather;

/**
 * Created by Spiros on 13/4/2017.
 */

import android.util.Log;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class WeatherApiErrorHandler implements ErrorHandler {
    protected final String TAG = getClass().getSimpleName();
    @Override
    public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
            Log.e(TAG, "Error:", cause);
        }
        return cause;

    }
}