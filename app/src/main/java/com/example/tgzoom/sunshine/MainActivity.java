package com.example.tgzoom.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> forecastArrayList;
    private ListView listViewForecast = null;
    private ArrayAdapter forecastArrayAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forecastArrayList = new ArrayList<String>(Arrays.asList(
                "Today - Sunny - 88/33",
                "Tomorrow - Sunny - 44/22",
                "Saturday - Sunny - 88/33",
                "Sunday - Sunny - 75/21",
                "Monday - Cloudy - 99/11"
        ));

        forecastArrayAdapter = new ArrayAdapter(this,R.layout.list_item_forecast,forecastArrayList);
        listViewForecast = (ListView) findViewById(R.id.listview_forecast);
        listViewForecast.setAdapter(forecastArrayAdapter);

        listViewForecast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent forecast_intent = new Intent(getApplicationContext(),DetailActivity.class);
                forecast_intent.setAction(Intent.ACTION_VIEW);
                String forecast = forecastArrayList.get(position);
                forecast_intent.putExtra("Forecast",forecast);
                startActivity(forecast_intent);
            }

        });
    }
}
