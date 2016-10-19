package com.example.tgzoom.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tgzoom.sunshine.data.WeatherContract;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ShareActionProvider shareActionProvider;

    static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        getLoaderManager().initLoader(ForecastFragment.FORECAST_LOADER_ID, null, this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem share_item = menu.findItem(R.id.forecast_item_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share_item);
        Intent share_intent = createShareIntent();
        setShareIntent(share_intent);
    }

    public void setShareIntent(Intent share_intent) {
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(share_intent);
        }
    }

    public Intent createShareIntent() {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
//        TextView forecast_item_textview = (TextView) getActivity().findViewById(R.id.detail_forecast_item);
//        share_intent.putExtra(Intent.EXTRA_TEXT, forecast_item_textview.getText() + "#SunshineApp");
        share_intent.setType("text/plain");
        return share_intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settings_intent = new Intent(getActivity(), SettingsActivity.class);
                settings_intent.setAction(Intent.ACTION_SEND);
                startActivity(settings_intent);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent forecast_intent = getActivity().getIntent();
        if(forecast_intent == null){
            return null;
        }

        String sort = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        return new CursorLoader(
                getActivity(),
                forecast_intent.getData(),
                ForecastFragment.FORECAST_COLUMNS,
                null,
                null,
                sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()){
            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
