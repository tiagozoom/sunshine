package com.example.tgzoom.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;


public class ForecastFragment extends Fragment {
    private ArrayAdapter forecastArrayAdapter   = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_forecast, container, false);

        ListView listViewForecast = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastArrayAdapter = new ArrayAdapter(getContext(),R.layout.list_item_forecast,new ArrayList<String>());
        listViewForecast.setAdapter(forecastArrayAdapter);

        listViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent forecast_intent = new Intent(getActivity(),DetailActivity.class);
                forecast_intent.setAction(Intent.ACTION_VIEW);
                String forecast = forecastArrayAdapter.getItem(position).toString();
                forecast_intent.putExtra("forecast",forecast);
                startActivity(forecast_intent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getForecast();;
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

    public void getForecast(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String location = sharedPreferences.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default_value));
        String units = sharedPreferences.getString(getString(R.string.pref_units_key),getString(R.string.pref_units_default_value));
        new ForecastAsyncTask(this.forecastArrayAdapter,location,units).execute();
    }
}
