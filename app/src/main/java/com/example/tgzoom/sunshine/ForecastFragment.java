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

import java.util.ArrayList;
import java.util.Arrays;


public class ForecastFragment extends Fragment {

    private ArrayList<String> forecastArrayList = new ArrayList<>();
    private ListView listViewForecast           = null;
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
        this.listViewForecast = (ListView) rootView.findViewById(R.id.listview_forecast);

        this.forecastArrayAdapter = new ArrayAdapter(getContext(),R.layout.list_item_forecast,forecastArrayList);
        this.listViewForecast.setAdapter(forecastArrayAdapter);

        new ForecastAsyncTask(this.forecastArrayAdapter,this.listViewForecast,rootView).execute();

        listViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent forecast_intent = new Intent(getActivity(),DetailActivity.class);
            forecast_intent.setAction(Intent.ACTION_VIEW);
            String forecast = forecastArrayList.get(position);
            forecast_intent.putExtra("Forecast",forecast);
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
            case R.id.action_refresh:
                //new ForecastAsyncTask().execute();
                break;
            case R.id.settings:
                Intent settings_intent = new Intent(getContext(),SettingsActivity.class);
                settings_intent.setAction(Intent.ACTION_VIEW);
                startActivity(settings_intent);
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
