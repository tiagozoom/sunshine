package com.example.tgzoom.sunshine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        Log.i("Intent content",intent.getStringExtra("Forecast"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailfragment,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent settings_intent = new Intent(getApplicationContext(),SettingsActivity.class);
                settings_intent.setAction(Intent.ACTION_VIEW);
                startActivity(settings_intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
