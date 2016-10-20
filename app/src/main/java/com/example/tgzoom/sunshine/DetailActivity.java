package com.example.tgzoom.sunshine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null){
            DetailFragment detailFragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.weather_detail_container,detailFragment).commit();
        }
    }
}
