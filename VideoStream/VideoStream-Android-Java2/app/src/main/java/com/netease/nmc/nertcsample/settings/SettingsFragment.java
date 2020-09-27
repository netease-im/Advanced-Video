package com.netease.nmc.nertcsample.settings;

import android.os.Bundle;
import android.view.View;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.netease.nmc.nertcsample.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    private final Preference.OnPreferenceChangeListener changeListener = (preference, newValue) -> {
        updateSummary(preference, false);

        // accept
        return true;
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(getString(R.string.shared_prefs_push_stream));
        setPreferencesFromResource(R.xml.settings, rootKey);
        bindPreference(findPreference(getString(R.string.setting_push_stream_url_key)));
    }

    private void bindPreference(Preference preference) {
        preference.setOnPreferenceChangeListener(changeListener);
        updateSummary(preference, true);
    }

    private void updateSummary(Preference preference, boolean init) {
        Runnable runnable = () -> updateSummary(preference);
        if (init) {
            runnable.run();;
        } else {
            View v = getView();
            if (v != null) {
                v.post(runnable);
            }
        }
    }

    private static void updateSummary(Preference preference) {
        if (preference instanceof EditTextPreference) {
            EditTextPreference editText = (EditTextPreference) preference;
            preference.setSummary(editText.getText());
        }
    }
}
