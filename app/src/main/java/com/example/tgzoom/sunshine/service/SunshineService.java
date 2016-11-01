package com.example.tgzoom.sunshine.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.example.tgzoom.sunshine.ForecastAsyncTask;
import com.example.tgzoom.sunshine.NetworkRequest;
import com.example.tgzoom.sunshine.R;
import com.example.tgzoom.sunshine.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tgzoom on 11/1/16.
 */

public class SunshineService extends IntentService{

    public static final String LOCATION_QUERY_EXTRA = "lqe";
    private final String LOG_TAG = SunshineService.class.getSimpleName();
    private ArrayList<String> forecastStringArray = new ArrayList<String>();

    private final static String TEMPERATURE = "temp";

    private final static String MAX         = "max";
    private final static String MIN         = "min";
    private final static String DESCRIPTION = "main";
    private final static String WEATHER     = "weather";
    private final static String PRESSURE    = "pressure";
    private final static String LIST        = "list";
    private static final String HUMIDITY    = "humidity";
    private static final String DEGREES     = "deg";
    private static final String WIND_SPEED  = "speed";
    private static final String WEATHER_ID  = "id";

    private final static String LON         = "lon";
    private final static String LAT         = "lat";
    private final static String CITY_NAME   = "name";
    private final static String CITY        = "city";
    private final static String COORD       = "coord";


    public SunshineService() {
        super("SunshineService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String location = intent.getStringExtra(LOCATION_QUERY_EXTRA);
        NetworkRequest networkRequest = new NetworkRequest();
        String weatherString = networkRequest.getWeatherString(location);
        try {
            parseJson(weatherString,location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Double getMaxTemperature(JSONObject temp) throws JSONException {
        Double max_temperature = temp.getDouble(MAX);
        return max_temperature;
    }

    private Double getMinTemperature(JSONObject temp) throws JSONException {
        Double min_temperature = temp.getDouble(MIN);
        return min_temperature;
    }

    long addLocation(String locationSetting, String cityName, double lat, double lon) {
        Cursor cursor = getBaseContext().getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null
        );

        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,locationSetting);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,lon);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,lat);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,cityName);

        if(cursor.moveToFirst()){
            int index = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            long _id = cursor.getLong(index);
            getBaseContext().getContentResolver().update(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    contentValues,
                    WeatherContract.LocationEntry._ID +" = ?",
                    new String[]{String.valueOf(_id)}
            );
            return _id;
        }else{
            Uri uri = getBaseContext().getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    contentValues
            );
            return Long.parseLong(uri.getLastPathSegment());
        }
    }


    private void parseJson(String s,String location) throws JSONException {
        JSONObject forecastJson = new JSONObject(s);

        JSONObject cityObject   = forecastJson.getJSONObject(CITY);
        String name             = cityObject.getString(CITY_NAME);
        JSONObject coordObject  = cityObject.getJSONObject(COORD);
        Double lon              = coordObject.getDouble(LON);
        Double lat              = coordObject.getDouble(LAT);

        long _id = addLocation(location,name,lat,lon);
        JSONArray list = forecastJson.getJSONArray(LIST);
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>();
        Time daytime = new Time();
        daytime.setToNow();
        int julianDayStart = Time.getJulianDay(System.currentTimeMillis(),daytime.gmtoff);
        daytime = new Time();

        for(int index=0;index<list.length();index++){
            JSONObject listObject   = list.getJSONObject(index);
            Double max_temperature  = getMaxTemperature(listObject.getJSONObject(TEMPERATURE));
            Double min_temperature  = getMinTemperature(listObject.getJSONObject(TEMPERATURE));
            Double pressure         = listObject.getDouble(PRESSURE);
            Double speed            = listObject.getDouble(WIND_SPEED);
            long dateTime           = daytime.setJulianDay(julianDayStart+index);
            int humidity            = listObject.getInt(HUMIDITY);
            int deg                 = listObject.getInt(DEGREES);

            JSONArray weather_array = listObject.getJSONArray(WEATHER);
            JSONObject weather_object = (JSONObject) weather_array.get(0);
            String description        = weather_object.getString(DESCRIPTION);
            int weather_id            = weather_object.getInt(WEATHER_ID);

            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_DATE,dateTime);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,max_temperature);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,min_temperature);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,pressure);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,humidity);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,description);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY,_id);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,deg);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,speed);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,weather_id);

            contentValuesArrayList.add(contentValues);
        }

        if(contentValuesArrayList.size() > 0){
            ContentValues[] contentValuesArray = new ContentValues[contentValuesArrayList.size()];
            contentValuesArrayList.toArray(contentValuesArray);
            int rowsInserted = getBaseContext().getContentResolver().bulkInsert(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    contentValuesArray
            );
        }

        String order = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC ";
        Cursor cursor = getBaseContext().getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,null,null,order
        );

        if(cursor.moveToFirst()){
            contentValuesArrayList = new ArrayList<ContentValues>();
            do{
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor,contentValues);
                contentValuesArrayList.add(contentValues);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }
}
