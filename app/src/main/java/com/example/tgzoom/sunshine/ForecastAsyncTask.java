package com.example.tgzoom.sunshine;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by tgzoom on 9/22/16.
 */
public class ForecastAsyncTask extends AsyncTask<Void,String,String> {

    @Override
    protected String doInBackground(Void... voids) {
        NetworkRequest networkRequest = new NetworkRequest();
        String weatherString = networkRequest.getWeatherString();
        return weatherString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i("WeatherString",s);
    }
}
