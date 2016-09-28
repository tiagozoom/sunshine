package com.example.tgzoom.sunshine;


import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        onSharedPreferenceChanged(
                getPreferenceManager().getSharedPreferences(),
                getString(R.string.pref_units_key)
        );

        onSharedPreferenceChanged(
                getPreferenceManager().getSharedPreferences(),
                getString(R.string.pref_location_key)
        );
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_units_key))){
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key,getString(R.string.pref_units_default_value)));
        }else if(key.equals(getString(R.string.pref_location_key))){
            Preference preference = findPreference(key);
            preference.setSummary(sharedPreferences.getString(key,getString(R.string.pref_location_default_value)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
