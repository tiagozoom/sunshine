package com.example.tgzoom.sunshine;

/**
 * Created by tgzoom on 10/17/16.
 */

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tgzoom.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView minTempView;
        public final TextView maxTempView;
        public final TextView forecastView;

        public ViewHolder(View view) {
            forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            maxTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            minTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        }
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_forecast;
        } else {
            layoutId = R.layout.list_item_forecast_today;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        boolean isMetric = Utility.isMetric(mContext);

        Double max_temp = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        Double min_temp = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);

        String max_temp_string = Utility.formatTemperature(context,max_temp, isMetric);
        String min_temp_string = Utility.formatTemperature(context,min_temp, isMetric);

        Long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);

        String formated_date = Utility.getFriendlyDayString(context, date);
        String weather_description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

        int weather_id = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int weather_resource_id = 0;

        if(getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY) {
            weather_resource_id = Utility.getArtResourceForWeatherCondition(weather_id);
        }else{
            weather_resource_id = Utility.getIconResourceForWeatherCondition(weather_id);
        }

        viewHolder.dateView.setText(formated_date);
        viewHolder.maxTempView.setText(max_temp_string);
        viewHolder.minTempView.setText(min_temp_string);
        viewHolder.forecastView.setText(weather_description);
        viewHolder.iconView.setImageResource(weather_resource_id);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}