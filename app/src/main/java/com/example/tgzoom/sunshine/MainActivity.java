package com.example.tgzoom.sunshine;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this,R.xml.pref_general,false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLocation = sharedPreferences.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default_value));
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.forecast_fragment_tag));
        String currentLocation = Utility.getPreferredLocation(this);
        if(currentLocation != mLocation){
            mLocation = currentLocation;
            forecastFragment.onLocationChanged();
        }
    }
}
