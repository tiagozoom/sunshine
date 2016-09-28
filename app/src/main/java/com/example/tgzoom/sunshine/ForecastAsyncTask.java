package com.example.tgzoom.sunshine;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by tgzoom on 9/22/16.
 */
public class ForecastAsyncTask extends AsyncTask<Void,ArrayList<String>,ArrayList<String>> {
    private ArrayAdapter forecastArrayAdapter   = null;
    private ListView listViewForecast           = null;
    private View context                        = null;
    private String location                     = null;

    public ForecastAsyncTask(ArrayAdapter forecastArrayAdapter, ListView listViewForecast, View context, String location) {
        this.forecastArrayAdapter = forecastArrayAdapter;
        this.listViewForecast     = listViewForecast;
        this.context              = context;
        this.location             = location;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        try {
            NetworkRequest networkRequest = new NetworkRequest();
            String weatherString = networkRequest.getWeatherString(this.location);
            ForecastParser forecastParser = new ForecastParser();
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
}
