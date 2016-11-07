package com.example.tgzoom.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tgzoom.sunshine.data.WeatherContract;
import com.example.tgzoom.sunshine.sync.SunshineSyncAdapter;
import com.facebook.stetho.Stetho;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ForecastAdapter forecastAdapter = null;
    private ListView listViewForecast;
    private int visiblePosition;
    private boolean useTodayLayout = false;
    static final int FORECAST_LOADER_ID = 105;
    static final String LISTVIEW_VISIBLE_POSITION = "listview_visible_position";

    static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Stetho.initialize(
                Stetho.newInitializerBuilder(getContext())
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(getContext()))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(getContext()))
                        .build());
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        if(forecastAdapter != null){
            forecastAdapter.setUseTodayLayout(useTodayLayout);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(visiblePosition != ListView.INVALID_POSITION){
            outState.putInt(LISTVIEW_VISIBLE_POSITION,visiblePosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String location = sharedPreferences.getString(getContext().getString(R.string.pref_location_key), getContext().getString(R.string.pref_location_default_value));
        String sort = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC ";
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location, System.currentTimeMillis());

        return new CursorLoader(
                getActivity(),
                weatherUri,
                FORECAST_COLUMNS,
                null,
                null,
                sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        forecastAdapter.swapCursor(data);
        listViewForecast.smoothScrollToPosition(visiblePosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastAdapter.swapCursor(null);
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);

        listViewForecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        listViewForecast.setAdapter(forecastAdapter);
        forecastAdapter.setUseTodayLayout(useTodayLayout);
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);

        listViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent forecast_intent = new Intent(getActivity(), DetailActivity.class);
                forecast_intent.setAction(Intent.ACTION_VIEW);
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                visiblePosition = position;
                if (cursor != null) {
                    String location = Utility.getPreferredLocation(getActivity());
                    long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
                    ((Callback) getActivity()).onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location,date));
                }
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(LISTVIEW_VISIBLE_POSITION)) {
            visiblePosition = savedInstanceState.getInt(LISTVIEW_VISIBLE_POSITION);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settings_intent = new Intent(getContext(), SettingsActivity.class);
                settings_intent.setAction(Intent.ACTION_VIEW);
                startActivity(settings_intent);
                break;
            case R.id.preferred_location:
                openPreferredLocationInMap();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void openPreferredLocationInMap(){
        Cursor cursor = forecastAdapter.getCursor();

        if(!cursor.moveToFirst()){
            return;
        }

        Double lat = cursor.getDouble(ForecastFragment.COL_COORD_LAT);
        Double lon = cursor.getDouble(ForecastFragment.COL_COORD_LONG);

        Uri geoIntentUri = Uri.parse("geo:" + lat + "," + lon );

        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(geoIntentUri);

        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getContext(), "No map class had been found.", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void updateWeather() {
        SunshineSyncAdapter.syncImmediately(getContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
    }

    public interface Callback{
        void onItemSelected(Uri dateUri);
    }
}
