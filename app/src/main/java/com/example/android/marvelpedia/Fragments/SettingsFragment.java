package com.example.android.marvelpedia.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.android.marvelpedia.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onStart() {
        super.onStart();
        //Register the OnSharedPreferenceChange Listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //Add marvel preferences from xml file preferences
        addPreferencesFromResource(R.xml.preferences);

        EditTextPreference teamNamePref = (EditTextPreference) findPreference(getString(R.string.team_name_key));
        teamNamePref.setSummary(teamNamePref.getText());
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof EditTextPreference) {
            /* For EditText, look up the correct display value in */
            /* the preference's 'entries' list (since they have separate labels/values). */
            EditTextPreference editTextPreference = (EditTextPreference) preference;
            String prefText = editTextPreference.getText();
            preference.setSummary(stringValue);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            if (preference instanceof EditTextPreference) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
