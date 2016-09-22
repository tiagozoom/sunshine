package com.example.tgzoom.sunshine;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tgzoom on 9/22/16.
 */
public class NetworkRequest {
    private HttpURLConnection httpURLConnection = null;
    private BufferedReader bufferedReader = null;
    private String forecastJsonStr = null;

    private final static String LOCATION    = "q";
    private final static String APPID       = "units";
    private final static String UNITS       = "appid";

    public String getWeatherString(){
        try{
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.openweathermap.org")
                    .appendPath("data")
                    .appendPath("2.5")
                    .appendPath("find")
                    .appendQueryParameter(LOCATION,"94043")
                    .appendQueryParameter(UNITS,"metric")
                    .appendQueryParameter(APPID,"bf39d9dabdfdca45aaaed83b3420961f");

            URL url = new URL(builder.build().toString());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            forecastJsonStr = buffer.toString();

            Log.i("Forecast",forecastJsonStr);
            return forecastJsonStr;
        }catch (Exception e){
            return e.toString();
        }
    }
}
