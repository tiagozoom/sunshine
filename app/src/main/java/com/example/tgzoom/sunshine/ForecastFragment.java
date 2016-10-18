package com.example.tgzoom.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.facebook.stetho.Stetho;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ForecastAdapter forecastAdapter   = null;
    static final int FORECAST_LOADER_ID = 105;

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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String location = sharedPreferences.getString(getContext().getString(R.string.pref_location_key),getContext().getString(R.string.pref_location_default_value));
        String sort = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC ";
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location,System.currentTimeMillis());

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastAdapter.swapCursor(null);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_forecast, container, false);

        ListView listViewForecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastAdapter = new ForecastAdapter(getActivity(),null,0);
        listViewForecast.setAdapter(forecastAdapter);
        getLoaderManager().initLoader(FORECAST_LOADER_ID,null,this);

        listViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent forecast_intent = new Intent(getActivity(),DetailActivity.class);
                forecast_intent.setAction(Intent.ACTION_VIEW);
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if(cursor != null){
                    String location = Utility.getPreferredLocation(getActivity());
                    forecast_intent.setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location,cursor.getLong(ForecastFragment.COL_WEATHER_DATE)));
                    startActivity(forecast_intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent settings_intent = new Intent(getContext(),SettingsActivity.class);
                settings_intent.setAction(Intent.ACTION_VIEW);
                startActivity(settings_intent);
                break;
            case R.id.preferred_location:
                sendPreferredLocation();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendPreferredLocation(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String location = sharedPreferences.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default_value));

        try {
            Intent map_intent = new Intent();
            map_intent.setAction(Intent.ACTION_VIEW);

            Uri geoLocation = Uri.parse("geo:0,0?")
                    .buildUpon()
                    .appendQueryParameter("q",location)
                    .build();

            map_intent.setData(geoLocation);

            if (map_intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(map_intent);
            }else{
                Toast.makeText(getContext(),"No map class had been found.",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
