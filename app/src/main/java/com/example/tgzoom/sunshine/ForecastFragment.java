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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tgzoom.sunshine.data.WeatherContract;
import com.example.tgzoom.sunshine.data.WeatherProvider;
import com.facebook.stetho.Stetho;
import java.util.ArrayList;

public class ForecastFragment extends Fragment {
    private ForecastAdapter forecastAdapter   = null;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_forecast, container, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String location = sharedPreferences.getString(getContext().getString(R.string.pref_location_key),getContext().getString(R.string.pref_location_default_value));
        String sort = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC ";
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location,System.currentTimeMillis());

        Cursor cursor = getActivity().getContentResolver().query(
                weatherUri,
                null,
                null,
                null,
                sort
        );

        ListView listViewForecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastAdapter = new ForecastAdapter(getActivity(),cursor,0);
        listViewForecast.setAdapter(forecastAdapter);

        listViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent forecast_intent = new Intent(getActivity(),DetailActivity.class);
                forecast_intent.setAction(Intent.ACTION_VIEW);
                String forecast = forecastAdapter.getItem(position).toString();
                forecast_intent.putExtra("forecast",forecast);
                startActivity(forecast_intent);
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
