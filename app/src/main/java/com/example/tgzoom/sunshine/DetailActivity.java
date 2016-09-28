package com.example.tgzoom.sunshine;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    private ShareActionProvider shareActionProvider;
    private TextView forecast_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent forecast_intent = getIntent();
        forecast_item = (TextView) findViewById(R.id.detail_forecast_item);
        forecast_item.setText(forecast_intent.getStringExtra("forecast"));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailfragment,menu);
        MenuItem share_item = menu.findItem(R.id.forecast_item_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share_item);
        Intent share_intent = createShareIntent();
        setShareIntent(share_intent);
        return super.onCreateOptionsMenu(menu);
    }

    public void setShareIntent(Intent share_intent){
        if(shareActionProvider != null){
            shareActionProvider.setShareIntent(share_intent);
        }
    }

    public Intent createShareIntent(){
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
        share_intent.putExtra(Intent.EXTRA_TEXT,forecast_item.getText() + "#SunshineApp");
        share_intent.setType("text/plain");
        return share_intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent settings_intent = new Intent(this,SettingsActivity.class);
                settings_intent.setAction(Intent.ACTION_SEND);
                startActivity(settings_intent);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }
}
