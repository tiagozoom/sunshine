package com.example.tgzoom.sunshine;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null){
            mLocation = Utility.getPreferredLocation(this);
        }

        if(findViewById(R.id.weather_detail_container) != null){
            mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container,new DetailFragment()).commit();
            }else{
                mTwoPane = false;
            }
        }
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
