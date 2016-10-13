package com.example.tgzoom.sunshine;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tgzoom.sunshine.data.WeatherContract;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by tgzoom on 9/22/16.
 */
public class ForecastAsyncTask extends AsyncTask<Void,ArrayList<String>,ArrayList<String>> {
    private ArrayAdapter forecastArrayAdapter   = null;
    private ForecastFragment parentActivity     = null;
    private String location                     = null;
    private String units                        = null;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public ForecastAsyncTask(ForecastFragment parentActivity, ArrayAdapter forecastArrayAdapter) {
        this.forecastArrayAdapter = forecastArrayAdapter;
        this.parentActivity = parentActivity;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(parentActivity.getContext());
        this.location = sharedPreferences.getString(parentActivity.getString(R.string.pref_location_key),parentActivity.getString(R.string.pref_location_default_value));
        this.units    = sharedPreferences.getString(parentActivity.getString(R.string.pref_units_key),parentActivity.getString(R.string.pref_units_default_value));
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        try {
            NetworkRequest networkRequest = new NetworkRequest();
            String weatherString = networkRequest.getWeatherString(this.location);
            ForecastParser forecastParser = new ForecastParser(this.units);
            ArrayList<String> forecastArrayList = forecastParser.parseJson(weatherString);
            return forecastArrayList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> forecastArrayList) {
        super.onPostExecute(forecastArrayList);
        this.forecastArrayAdapter.clear();

        if(forecastArrayList != null) {
            for (String forecastItem : forecastArrayList) {
                this.forecastArrayAdapter.add(forecastItem);
            }
        }
        this.forecastArrayAdapter.notifyDataSetChanged();
    }

    public int addLocation(String locationSetting, String cityName, double lat, double lon){
        return 0;
    }
}
