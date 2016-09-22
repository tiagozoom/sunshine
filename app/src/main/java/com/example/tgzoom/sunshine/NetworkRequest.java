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
    private BufferedReader bufferedReader       = null;
    private String forecastJsonStr              = null;

    private final static String LOCATION    = "q";
    private final static String UNITS       = "units";
    private final static String APPID       = "appid";

    private String buildUrl(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter(LOCATION,"94043")
                .appendQueryParameter(UNITS,"metric")
                .appendQueryParameter(APPID,"bf39d9dabdfdca45aaaed83b3420961f");

        return builder.build().toString();
    }

    private BufferedReader generateBufferedReader(InputStream inputStream){
        if (inputStream == null) {
            return null;
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        return buffer;
    }

    private HttpURLConnection openConection(URL url,String method){
        try{
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod(method);
            httpConnection.connect();
            return httpConnection;
        }catch (Exception e){

        }
        return null;
    }

    private String readBufferedReader(BufferedReader buffReader){
        StringBuffer buffer = new StringBuffer();
        String line;
        try{
            while ((line = buffReader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            return buffer.toString();
        }catch (Exception e){

        }
        return null;
    }

    public String getWeatherString(){
        try{
            String urlString = buildUrl();
            URL url = new URL(urlString);
            httpURLConnection = openConection(url,"GET");
            InputStream inputStream = httpURLConnection.getInputStream();
            bufferedReader = generateBufferedReader(inputStream);

            if(bufferedReader == null){
                return null;
            }

            forecastJsonStr = readBufferedReader(bufferedReader);
            return forecastJsonStr;
        }catch (Exception e){
            return e.toString();
        }
    }
}
