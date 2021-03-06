package com.example.tgzoom.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tgzoom.sunshine.data.WeatherContract;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ShareActionProvider shareActionProvider;
    private  ImageView iconView;
    private  TextView dateView;
    private  TextView minTempView;
    private  TextView maxTempView;
    private  TextView forecastView;
    private  TextView humidityView;
    private  TextView pressureView;
    private  TextView windView;
    private  TextView dayView;

    private  Uri mUri;
    static final int DETAIL_LOADER = 111;
    public static final String DETAIL_URI = "detail_uri";

    static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    static final int COLUMN_WEATHER_ID = 0;
    static final int COLUMN_WEATHER_DATE = 1;
    static final int COLUMN_WEATHER_DESC = 2;
    static final int COLUMN_WEATHER_MAX_TEMP = 3;
    static final int COLUMN_WEATHER_MIN_TEMP = 4;
    static final int COLUMN_PRESSURE = 5;
    static final int COLUMN_HUMIDITY = 6;
    static final int COLUMN_WIND_SPEED = 7;
    static final int COLUMN_DEGREES = 8;
    static final int COLUMN_LOCATION_SETTING = 9;
    static final int COLUMN_WEATHER_CONDITION_ID = 10;
    static final int COLUMN_COORD_LAT = 11;
    static final int COLUMN_COORD_LONG = 12;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static DetailFragment newInstance(Uri dateUri){
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.DETAIL_URI,dateUri);
        detailFragment.setArguments(args);
        return detailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        getLoaderManager().initLoader(DetailFragment.DETAIL_LOADER, null, this);

        maxTempView = (TextView) rootView.findViewById(R.id.forecast_detail_high_textview);
        minTempView = (TextView) rootView.findViewById(R.id.forecast_detail_low_textview);
        forecastView = (TextView) rootView.findViewById(R.id.forecast_detail_forecast_textview);
        humidityView = (TextView) rootView.findViewById(R.id.forecast_detail_humidity_textview);
        pressureView = (TextView) rootView.findViewById(R.id.forecast_detail_pressure_textview);
        windView = (TextView) rootView.findViewById(R.id.forecast_detail_wind_textview);
        iconView = (ImageView) rootView.findViewById(R.id.forecast_detail_icon);
        dateView = (TextView) rootView.findViewById(R.id.forecast_detail_date_textview);
        dayView = (TextView) rootView.findViewById(R.id.forecast_detail_day_textview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem share_item = menu.findItem(R.id.forecast_item_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share_item);
        Intent share_intent = createShareIntent();
        setShareIntent(share_intent);
    }

    public void setShareIntent(Intent share_intent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(share_intent);
        }
    }

    public Intent createShareIntent() {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
//        TextView forecast_item_textview = (TextView) getActivity().findViewById(R.id.detail_forecast_item);
//        share_intent.putExtra(Intent.EXTRA_TEXT, forecast_item_textview.getText() + "#SunshineApp");
        share_intent.setType("text/plain");
        return share_intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settings_intent = new Intent(getActivity(), SettingsActivity.class);
                settings_intent.setAction(Intent.ACTION_SEND);
                startActivity(settings_intent);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sort = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        if(mUri != null) {

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DetailFragment.FORECAST_COLUMNS,
                    null,
                    null,
                    sort);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(!data.moveToFirst()){
            return;
        }

        boolean isMetric = Utility.isMetric(getContext());

        double max_temp = data.getDouble(DetailFragment.COLUMN_WEATHER_MAX_TEMP);
        double min_temp = data.getDouble(DetailFragment.COLUMN_WEATHER_MIN_TEMP);
        Long date = data.getLong(DetailFragment.COLUMN_WEATHER_DATE);
        String weather_description = data.getString(DetailFragment.COLUMN_WEATHER_DESC);
        double pressure = data.getDouble(DetailFragment.COLUMN_PRESSURE);
        double humidity = data.getDouble(DetailFragment.COLUMN_HUMIDITY);
        float wind = data.getFloat(DetailFragment.COLUMN_WIND_SPEED);
        float degrees = data.getFloat(DetailFragment.COLUMN_DEGREES);

        String max_temp_string = Utility.formatTemperature(getContext(),max_temp, isMetric);
        String min_temp_string = Utility.formatTemperature(getContext(),min_temp, isMetric);
        String pressure_string = Utility.formatPressure(getContext(),pressure);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        String humidity_string = Utility.formatHumidity(getContext(),humidity);
        String wind_string = Utility.getFormattedWind(getContext(),wind,degrees);

        int weather_id = data.getInt(DetailFragment.COLUMN_WEATHER_CONDITION_ID);

        int weather_resource_id = Utility.getArtResourceForWeatherCondition(weather_id);

        dateView.setText(dateText);
        dayView.setText(friendlyDateText);
        maxTempView.setText(max_temp_string);
        minTempView.setText(min_temp_string);
        forecastView.setText(weather_description);
        pressureView.setText(pressure_string);
        humidityView.setText(humidity_string);
        windView.setText(wind_string);
        iconView.setImageResource(weather_resource_id);
        int iconDescriptionStringResource = Utility.getArtResourceDescriptionForWeatherCondition(weather_id);
        iconView.setContentDescription(getString(iconDescriptionStringResource));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}
