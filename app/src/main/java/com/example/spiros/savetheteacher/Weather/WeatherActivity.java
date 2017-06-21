package com.example.spiros.savetheteacher.Weather;

/**
 * Created by Spiros on 13/4/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spiros.savetheteacher.MainActivity;
import com.example.spiros.savetheteacher.R;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WeatherActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    private RetainedAppData mRetainedAppData;
    // UX handlers
    private ProgressBar mProgressBar;
    private EditText    mInputCityName;
    private TextView    mCityNameTextView;
    private TextView    mCountryNameTextView;
    private TextView    mCodTextView;
    private TextView    mCoordsTextView;
    private TextView    mTempTextView;
    private TextView    mSunriseTextView;
    private TextView    mSunsetTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_id);
        mInputCityName = (EditText) findViewById(R.id.input_city_id);
        // Data saved
        if (savedInstanceState != null) {
            if (getLastCustomNonConfigurationInstance() != null) {
                mRetainedAppData = (RetainedAppData) getLastCustomNonConfigurationInstance();
                Log.d(TAG,"onCreate(): Reusing retained data set");
            }
        } else {
            mRetainedAppData = new RetainedAppData();
            Log.d(TAG, "onCreate(): Creating new  data set");
        }

        // Setup activity reference
        // mActivityRef = new WeakReference<>(this);
        mRetainedAppData.setAppContext(this);

        if (mRetainedAppData.mData != null) {
            updateUXWithWeatherData(mRetainedAppData.mData);
        }
        // Setup the progress bar
        if  (mRetainedAppData.isFetchInProgress()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mActivityRef = null;
        mRetainedAppData.setAppContext(null);
        Log.d(TAG,"onDestroy()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                return true;
            case R.id.home2:
                Intent launchNewIntent = new Intent(this,MainActivity.class);
                startActivityForResult(launchNewIntent, 0);

            default:
                return super.onOptionsItemSelected(item);
        }
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
    }
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mRetainedAppData;
    }

    public void expandWeatherSync(View v) {
        hideKeyboard(WeatherActivity.this, mInputCityName.getWindowToken());
        String city = mInputCityName.getText().toString();
        if (city.isEmpty()) {
            Toast.makeText(getApplicationContext(),"No city specified.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        mRetainedAppData.runRetrofitTestSync(city);
    }

    public void expandWeatherAsync(View v) {
        hideKeyboard(WeatherActivity.this, mInputCityName.getWindowToken());
        String city = mInputCityName.getText().toString();
        if (city.isEmpty()) {
            Toast.makeText(getApplicationContext(),"No city specified.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        mRetainedAppData.runRetrofitTestAsync(city);
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService
                (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }


    void updateUXWithWeatherData (final WeatherData data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Setup UX handlers
                // Get the UX handlers every time. This is to avoid a condition
                // when runOnUiThread may not have updated UX handles when screen is rotated.
                // 'mActivityRef.get()'
                mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_id);
                mInputCityName = (EditText) findViewById(R.id.input_city_id);
                mCityNameTextView = (TextView) findViewById(R.id.city_name_id);
                mCountryNameTextView = (TextView) findViewById(R.id.country_name_id);
                mCodTextView = (TextView) findViewById(R.id.cod_id);
                mCoordsTextView = (TextView) findViewById(R.id.coords_id);
                mTempTextView = (TextView) findViewById(R.id.temp_id);
                mSunriseTextView = (TextView) findViewById(R.id.sunrise_id);
                mSunsetTextView = (TextView) findViewById(R.id.sunset_id);

                // Refresh UX data
                if (mRetainedAppData.isFetchInProgress()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }

                // Print data to Android display
                Resources res = getResources();
                String textToPrint = res.getString(R.string.city) + data.getName();
                mCityNameTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.country) + data.getCountry();
                mCountryNameTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.coordinates) +"(" + data.getLat() + "," + data.getLon() + ")";
                mCoordsTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.cod) + data.getCod();
                mCodTextView.setText(textToPrint);
                String tempF = String.format(Locale.UK,"Temperature: %.2f F", (data.getTemp() - 273.15) * 1.8 + 32.00);
                mTempTextView.setText(tempF);
                DateFormat dfLocalTz = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.UK);
                Date sunriseTime = new Date(data.getSunrise() * 1000);
                Date sunsetTime = new Date(data.getSunset() * 1000);
                textToPrint = res.getString(R.string.sunrise) + dfLocalTz.format(sunriseTime);
                mSunriseTextView.setText(textToPrint);
                textToPrint = res.getString(R.string.sunset) + dfLocalTz.format(sunsetTime);
                mSunsetTextView.setText(textToPrint);
            }
        });
    }

    /**
     * This is main class object that should save all data upon configuration changes.
     * This object is saved by the 'onRetainCustomNonConfigurationInstance' method.
     *
     * Note: In the video it is referred to as 'private class TestWeatherData'
     */
    private static class RetainedAppData {
        private  WeakReference<WeatherActivity> mActivityRef;
        protected final String TAG = "RTD";
        private WeatherData mData; // Weather data received
        private AtomicBoolean mInProgress = new AtomicBoolean(false); // Is a download in progress
        private WeatherRestAdapter mWeatherRestAdapter; // REST Adapter
        private Callback<WeatherData> mWeatherDataCallback = new Callback<WeatherData>() {
            @Override
            public void success(WeatherData data, Response response) {

                Log.d(TAG, "Async success: weatherData: Name:" + data.getName() + ", cod:" + data.getCod()
                        + ",Coord: (" + data.getLat() + "," + data.getLon()
                        + "), Temp:" + data.getTemp()
                        + "\nSunset:" + data.getSunset() + ", " + data.getSunrise()
                        + ", Country:" + data.getCountry());
                mData = data;
                if (mActivityRef.get() != null) {
                    mActivityRef.get().updateUXWithWeatherData(mData);
                    mActivityRef.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivityRef.get().mProgressBar = (ProgressBar) mActivityRef.get().
                                    findViewById(R.id.progress_bar_id);
                            mActivityRef.get().mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                mInProgress.set(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG,"failure: " + error);
                mInProgress.set(false);
                if (mActivityRef.get() != null) {
                    mActivityRef.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mActivityRef.get().mProgressBar = (ProgressBar) mActivityRef.get().
                                    findViewById(R.id.progress_bar_id);
                            mActivityRef.get().mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        };
        // Method to test Async. call
        public void runRetrofitTestAsync (final String city) {
            if ( (mActivityRef.get() != null) && (mInProgress.get())) {
                Toast.makeText(mActivityRef.get(),"Weather fetch in progress.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            // Get the Adapter
            if (mWeatherRestAdapter == null)
                mWeatherRestAdapter = new WeatherRestAdapter();

            if (mActivityRef.get() != null) {
                mActivityRef.get().mProgressBar.setVisibility(View.VISIBLE);
            }

            // Test delay
            try {
                mInProgress.set(true);
                mWeatherRestAdapter.testWeatherApi(city, mWeatherDataCallback); // Call Async API
            } catch (Exception e) {
                Log.d(TAG, "Thread sleep error" + e);
            }
        }

        // Method to test sync. call
        public void runRetrofitTestSync (final String city) {

            if ((mActivityRef.get() != null) && (mInProgress.get())) {
                Toast.makeText(mActivityRef.get(),"Weather fetch in progress.",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (mActivityRef.get() != null) {
                mActivityRef.get().mProgressBar.setVisibility(View.VISIBLE);
            }

            if (mWeatherRestAdapter == null)
                mWeatherRestAdapter = new WeatherRestAdapter();

            mInProgress.set(true);

            // Test Sync version -- in a separate thread
            // Not doing this will crash the app. As Retro sync calls can not be made from
            // UI thread.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Call Async API -- always call in a try block if you dont want app to
                        // crash. You get 'HTTP/1.1 500 Internal Server Error' more often than
                        // you think.
                        Thread.sleep(10000);
                        WeatherData data = mWeatherRestAdapter.testWeatherApiSync(city);
                        Log.d(TAG, "Sync: Data: cod:" + data.getName() + ", cod:" + data.getCod()
                                + ",Coord: (" + data.getLat() + "," + data.getLon()
                                + "), Temp:" + data.getTemp()
                                + "\nSunset:" + data.getSunset() + ", " + data.getSunrise()
                                + ", Country:" + data.getCountry());
                        mData = data;
                        if (mActivityRef.get() != null) {
                            mActivityRef.get().updateUXWithWeatherData(mData);
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Sync: exception", ex);
                        if (mActivityRef.get() != null) {
                            mActivityRef.get().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mActivityRef.get().mProgressBar = (ProgressBar) mActivityRef.get().
                                            findViewById(R.id.progress_bar_id);
                                    mActivityRef.get().mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    } finally {
                        mInProgress.set(false);
                    }
                }
            }).start();
        }

        void setAppContext (WeatherActivity ref) {
            mActivityRef = new WeakReference<>(ref);
        }

        boolean isFetchInProgress() {
            return mInProgress.get();
        }
    }


}