package com.example.focusfox.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.focusfox.R;

public class Settings extends AppCompatActivity {

    //Declaring the variables to be used for the activity
    private static SwitchPreferenceCompat nightModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        //Displaying the settings fragment within the activity.
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    //This is the settings fragment with the quote settings and nightmode option
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //Quotes is an individual activity and is declared in root_preferences.
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            //Night Mode is controlled through the preference settings fragment

            //Setting nightModeSwitch's preference to nightMode within preference fragment
            nightModeSwitch = findPreference("nightMode");

                if (nightModeSwitch != null) {
                    nightModeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                        //set isChecked boolean to false so night mode is off by default
                        boolean isChecked = false;
                        //if nightMode toggled check the state of boolean
                        if (newValue instanceof Boolean)
                            isChecked = (Boolean) newValue;
                        //If boolean is true/preference has been toggled on, set theme to night mode
                        if (isChecked) {
                            getPreferenceManager().getSharedPreferences().edit().putBoolean(getString(R.string.nightMode), true).apply();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        }//If boolean is false/preference has been toggled off, set theme to light mode
                        else {
                            getPreferenceManager().getSharedPreferences().edit().putBoolean(getString(R.string.nightMode), false).apply();
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                        return true;
                    });
                }

            }
        }
    }