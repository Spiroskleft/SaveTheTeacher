package com.example.spiros.savetheteacher.Weather;

/**
 * Created by Spiros on 13/4/2017.
 */

import android.util.Log;

import retrofit.Callback;
import retrofit.RestAdapter;

public class WeatherRestAdapter {
    protected final String TAG = getClass().getSimpleName();
    protected RestAdapter mRestAdapter;
    protected WeatherApi mApi;
    static final String WEATHER_URL="http://api.openweathermap.org";
    static final String OPEN_WEATHER_API = "b52003ee4639df0f6d5437861e76f7e1";

    public WeatherRestAdapter() {
        mRestAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(WEATHER_URL)
                .setErrorHandler(new WeatherApiErrorHandler())
                .build();
        mApi = mRestAdapter.create(WeatherApi.class); // create the interface
        Log.d(TAG, "WeatherRestAdapter -- created");
    }

    public void testWeatherApi(String city, Callback<WeatherData> callback){
        Log.d(TAG, "testWeatherApi: for city:" + city);
        mApi.getWeatherFromApi(city, OPEN_WEATHER_API, callback);
    }

    public WeatherData testWeatherApiSync(String city) {
        WeatherData result;
        Log.d(TAG, "testWeatherApi: for city:" + city);
        result = mApi.getWeatherFromApiSync(city,OPEN_WEATHER_API);
        return result;
    }
}
