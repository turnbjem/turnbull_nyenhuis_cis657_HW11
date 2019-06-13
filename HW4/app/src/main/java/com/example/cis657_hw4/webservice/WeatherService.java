
package com.example.cis657_hw4.webservice;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WeatherService extends IntentService {
    public static final String TAG = "WeatherService";
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FETCH_DATA = "com.example.cis657_hw4.webservice.action.WEATHER_AT";
    private static final String API_URL = "https://api.darksky.net/forecast/e07c8965257a5a09c808af90286835fe";
    public static final String BROADCAST_ELEMENT = "com.example.cis657_hw4.webservice.action.BROADCAST";
    // TODO: Rename parameters
    private static final String FETCHED_KEY = "cis.gvsu.edu.geocalculator.webservice.extra.KEY";
    private static final String FETCHED_LAT = "cis.gvsu.edu.geocalculator.webservice.extra.LAT";
    private static final String FETCHED_LNG = "cis.gvsu.edu.geocalculator.webservice.extra.LNG";
    private static final String FETCHED_TIME = "cis.gvsu.edu.geocalculator.webservice.extra.TIME";

    public WeatherService() {
        super("WeatherService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startGetWeather(Context context, String lat, String lng, String key) {
        Intent intent = new Intent(context, WeatherService.class);
        intent.setAction(ACTION_FETCH_DATA);
        intent.putExtra(FETCHED_LAT, lat);
        intent.putExtra(FETCHED_LNG, lng);
        intent.putExtra(FETCHED_KEY, key);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_DATA.equals(action)) {
                final String key = intent.getStringExtra(FETCHED_KEY);
                final String lat = intent.getStringExtra(FETCHED_LAT);
                final String lng = intent.getStringExtra(FETCHED_LNG);
                final String when = intent.getStringExtra(FETCHED_TIME);
                fetchWeatherData(key, lat, lng, when);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void fetchWeatherData(String key, String lat, String lon, String time) {

        try {
            // TODO: Format the url based on the input params
            String test = "42.96335990000001,-85.6680863";
            URL url = new URL(API_URL + String.format("/%s,%s",lat,lon));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(5000 /* milliseconds */);
            conn.setConnectTimeout(10000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int len;
                while ((len = bis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                JSONObject data = new JSONObject(new String(baos.toByteArray()));
                JSONObject current = data.getJSONObject("currently");

                String condition = current.getString("summary");
                String icon = current.getString("icon");
                double temp = current.getDouble("temperature");
                Log.d(TAG, "fetchWeatherData: " + condition + " " + time);

                Intent result = new Intent(BROADCAST_ELEMENT);

                // TODO: use putExtra to add the extracted values to your broadcast

                result.putExtra("KEY", key);
                result.putExtra("SUMMARY", condition);
                result.putExtra("TEMPERATURE", temp);
                result.putExtra("ICON",icon);
                LocalBroadcastManager.getInstance(this).sendBroadcast(result);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
