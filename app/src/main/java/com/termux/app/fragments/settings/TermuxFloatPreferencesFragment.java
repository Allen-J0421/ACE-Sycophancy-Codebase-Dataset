package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.base.TermuxPreferenceFragment;

@Keep
public class TermuxFloatPreferencesFragment extends TermuxPreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_float_preferences;
    }

    @Override
    protected PreferenceDataStore createPreferenceDataStore(@NonNull Context context) {
        return null;
    }
}
