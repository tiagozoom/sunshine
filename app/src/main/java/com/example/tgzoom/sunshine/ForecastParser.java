package com.example.tgzoom.sunshine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by tgzoom on 9/22/16.
 */
public class ForecastParser {
    private ArrayList<String> forecastStringArray = new ArrayList<String>();
    private final static String DAY         = "dt";
    private final static String TEMPERATURE = "temp";
    private final static String MAX         = "max";
    private final static String MIN         = "min";
    private final static String DESCRIPTION = "main";
    private final static String WEATHER     = "weather";
    private final static String LIST        = "list";

    /* The date/time conversion code is going to be moved outside the asynctask later,
        * so for convenience we're breaking it out into its own method now.
        */
    private String getReadableDateString(long time){
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    private Double getMaxTemperature(JSONObject temp) throws JSONException {
        Double max_temperature = temp.getDouble(MAX);
        return max_temperature;
    }

    private Double getMinTemperature(JSONObject temp) throws JSONException {
        Double min_temperature = temp.getDouble(MIN);
        return min_temperature;
    }

    private String getDescription(JSONArray weather) throws JSONException {
        JSONObject weather_object = weather.getJSONObject(0);
        String description = weather_object.getString(DESCRIPTION);
        return description;
    }

    private String getDay(JSONObject listObject) throws JSONException {
        long dateTime = listObject.getLong(DAY);
        dateTime = dateTime * 1000;
        String day = getReadableDateString(dateTime);
        return day;
    }

    public ArrayList<String> parseJson(String s) throws JSONException {
        JSONObject forecastJson = new JSONObject(s);
        JSONArray list = forecastJson.getJSONArray(LIST);

        for(int index=0;index<list.length();index++){
            JSONObject listObject   = list.getJSONObject(index);
            Double max_temperature  = getMaxTemperature(listObject.getJSONObject(TEMPERATURE));
            Double min_temperature  = getMinTemperature(listObject.getJSONObject(TEMPERATURE));
            String highLow          = formatHighLows(max_temperature,min_temperature);
            String description      = getDescription(listObject.getJSONArray(WEATHER));
            String readableDay      = getDay(listObject);

            forecastStringArray.add(
                    readableDay + " - "+
                    highLow + " - " +
                    description
            );
        }
        return forecastStringArray;
    }
}
