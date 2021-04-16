package com.example.chorerewards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.SettingsFrameLayout, new SettingsFragment()).commit();
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getApplicationContext(), key + " Setting Changed!", Toast.LENGTH_SHORT);
                toast.show();
            }
        };
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.recreate();

    }
}