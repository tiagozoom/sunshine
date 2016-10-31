package com.example.tgzoom.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {
    private String mLocation;
    private boolean mTwoPane;
    static final String DETAILFRAGMENT_TAG = "detail_fragment_tag";

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
                getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container,new DetailFragment(),DETAILFRAGMENT_TAG).commit();
            }
        } else{
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ForecastFragment forecastFragment = ((ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        String currentLocation = Utility.getPreferredLocation(this);
        if(currentLocation != null && !currentLocation.equals(mLocation)){
            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (forecastFragment != null) {
                forecastFragment.onLocationChanged();
            }
            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(detailFragment != null){
                detailFragment.onLocationChanged(currentLocation);
            }
            mLocation = currentLocation;
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if(mTwoPane){
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI,dateUri);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.weather_detail_container,fragment,DETAILFRAGMENT_TAG).commit();
        }else{
            Intent intent = new Intent(this,DetailActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(dateUri);
            startActivity(intent);
        }
    }
}
