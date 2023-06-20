package com.lipakov.smartlink;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.TwoStatePreference;

import com.lipakov.smartlink.presenter.SmartLinkPresenter;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SmartLinkPresenter.SmartLinkView {
        private TwoStatePreference changeBackground;
        private TwoStatePreference deleteAllSmartLinkList;
        public SettingsFragment() {}
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            changeBackground = findPreference("background");
            Objects.requireNonNull(changeBackground).setOnPreferenceClickListener(preference -> {
                if (changeBackground.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return false;
            });
            deleteAllSmartLinkList = findPreference("delete_list");
            Objects.requireNonNull(deleteAllSmartLinkList).setOnPreferenceClickListener(preference -> {
                SmartLinkPresenter smartLinkPresenter = new SmartLinkPresenter(requireContext(), this);
                smartLinkPresenter.deleteSmartLinkList();
                return false;
            });
        }

        @Override
        public void showNotify(String notify) {
            Toast.makeText(requireContext(), notify, Toast.LENGTH_SHORT).show();
            deleteAllSmartLinkList.setChecked(false);
        }
    }
}