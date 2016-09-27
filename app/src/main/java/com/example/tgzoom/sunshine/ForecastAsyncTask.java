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

    public ForecastAsyncTask(ArrayAdapter forecastArrayAdapter, ListView listViewForecast, View context) {
        this.forecastArrayAdapter = forecastArrayAdapter;
        this.listViewForecast     = listViewForecast;
        this.context              = context;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        try {
            NetworkRequest networkRequest = new NetworkRequest();
            String weatherString = networkRequest.getWeatherString();
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
        this.forecastArrayAdapter = new ArrayAdapter(this.context.getContext(),R.layout.list_item_forecast,forecastArrayList);
        this.listViewForecast.setAdapter(forecastArrayAdapter);
    }
}
