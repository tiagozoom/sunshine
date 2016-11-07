package com.example.tgzoom.sunshine.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;

import com.example.tgzoom.sunshine.DetailActivity;
import com.example.tgzoom.sunshine.DetailFragment;
import com.example.tgzoom.sunshine.MainActivity;
import com.example.tgzoom.sunshine.NetworkRequest;
import com.example.tgzoom.sunshine.R;
import com.example.tgzoom.sunshine.Utility;
import com.example.tgzoom.sunshine.data.WeatherContract;
import com.example.tgzoom.sunshine.data.WeatherProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOCATION_QUERY_EXTRA = "lqe";
    private final static String TEMPERATURE = "temp";

    private final static String MAX = "max";
    private final static String MIN = "min";
    private final static String DESCRIPTION = "main";
    private final static String WEATHER = "weather";
    private final static String PRESSURE = "pressure";
    private final static String LIST = "list";
    private static final String HUMIDITY = "humidity";
    private static final String DEGREES = "deg";
    private static final String WIND_SPEED = "speed";
    private static final String WEATHER_ID = "id";

    private final static String LON = "lon";
    private final static String LAT = "lat";
    private final static String CITY_NAME = "name";
    private final static String CITY = "city";
    private final static String COORD = "coord";
    private static final int SYNC_INTERVAL = 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[] {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
    };

    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;
    private static final int INDEX_SHORT_DESC = 3;

    public SunshineSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    private void notifyWeather() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        boolean displayNotifications = Utility.getPreferredNotification(context);
        if(displayNotifications) {


            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the weather.
                String locationQuery = Utility.getPreferredLocation(context);

                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis());

                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null);

                if (cursor.moveToFirst()) {
                    int weatherId = cursor.getInt(INDEX_WEATHER_ID);
                    double high = cursor.getDouble(INDEX_MAX_TEMP);
                    double low = cursor.getDouble(INDEX_MIN_TEMP);
                    String desc = cursor.getString(INDEX_SHORT_DESC);

                    int iconId = Utility.getIconResourceForWeatherCondition(weatherId);
                    String title = context.getString(R.string.app_name);

                    // Define the text of the forecast.
                    String contentText = String.format(context.getString(R.string.format_notification),
                            desc,
                            Utility.formatTemperature(context, high, Utility.isMetric(context)),
                            Utility.formatTemperature(context, low, Utility.isMetric(context)));

                    //build your notification here.
                    NotificationCompat.Builder mNotificationBuilder =
                            new NotificationCompat.Builder(context)
                                    .setContentTitle(title)
                                    .setContentText(contentText)
                                    .setSmallIcon(R.drawable.art_clear);

                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.setData(weatherUri);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(intent);

                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mNotificationBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mNotificationBuilder.build());

                    //refreshing last sync
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.commit();
                }
            }
        }

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String location = Utility.getPreferredLocation(getContext());
        NetworkRequest networkRequest = new NetworkRequest();
        String weatherString = networkRequest.getWeatherString(location);
        try {
            parseJson(weatherString, location);
            notifyWeather();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Double getMaxTemperature(JSONObject temp) throws JSONException {
        Double max_temperature = temp.getDouble(MAX);
        return max_temperature;
    }

    private Double getMinTemperature(JSONObject temp) throws JSONException {
        Double min_temperature = temp.getDouble(MIN);
        return min_temperature;
    }

    long addLocation(String locationSetting, String cityName, double lat, double lon) {
        Cursor cursor = getContext().getContentResolver().query(
                WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null
        );

        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, lon);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);

        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            long _id = cursor.getLong(index);
            getContext().getContentResolver().update(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    contentValues,
                    WeatherContract.LocationEntry._ID + " = ?",
                    new String[]{String.valueOf(_id)}
            );
            return _id;
        } else {
            Uri uri = getContext().getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI,
                    contentValues
            );
            return Long.parseLong(uri.getLastPathSegment());
        }
    }


    private void parseJson(String s, String location) throws JSONException {
        JSONObject forecastJson = new JSONObject(s);

        JSONObject cityObject = forecastJson.getJSONObject(CITY);
        String name = cityObject.getString(CITY_NAME);
        JSONObject coordObject = cityObject.getJSONObject(COORD);
        Double lon = coordObject.getDouble(LON);
        Double lat = coordObject.getDouble(LAT);

        long _id = addLocation(location, name, lat, lon);
        JSONArray list = forecastJson.getJSONArray(LIST);
        ArrayList<ContentValues> contentValuesArrayList = new ArrayList<ContentValues>();
        Time daytime = new Time();
        daytime.setToNow();
        int julianDayStart = Time.getJulianDay(System.currentTimeMillis(), daytime.gmtoff);
        daytime = new Time();

        for (int index = 0; index < list.length(); index++) {
            JSONObject listObject = list.getJSONObject(index);
            Double max_temperature = getMaxTemperature(listObject.getJSONObject(TEMPERATURE));
            Double min_temperature = getMinTemperature(listObject.getJSONObject(TEMPERATURE));
            Double pressure = listObject.getDouble(PRESSURE);
            Double speed = listObject.getDouble(WIND_SPEED);
            long dateTime = daytime.setJulianDay(julianDayStart + index);
            int humidity = listObject.getInt(HUMIDITY);
            int deg = listObject.getInt(DEGREES);

            JSONArray weather_array = listObject.getJSONArray(WEATHER);
            JSONObject weather_object = (JSONObject) weather_array.get(0);
            String description = weather_object.getString(DESCRIPTION);
            int weather_id = weather_object.getInt(WEATHER_ID);

            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, max_temperature);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, min_temperature);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, _id);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, deg);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, speed);
            contentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weather_id);

            contentValuesArrayList.add(contentValues);
        }

        if (contentValuesArrayList.size() > 0) {
            ContentValues[] contentValuesArray = new ContentValues[contentValuesArrayList.size()];
            contentValuesArrayList.toArray(contentValuesArray);
            int rowsInserted = getContext().getContentResolver().bulkInsert(
                    WeatherContract.WeatherEntry.CONTENT_URI,
                    contentValuesArray
            );
        }

        String order = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC ";
        Cursor cursor = getContext().getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null, null, null, order
        );

        if (cursor.moveToFirst()) {
            contentValuesArrayList = new ArrayList<ContentValues>();
            do {
                ContentValues contentValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
                contentValuesArrayList.add(contentValues);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SunshineSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}