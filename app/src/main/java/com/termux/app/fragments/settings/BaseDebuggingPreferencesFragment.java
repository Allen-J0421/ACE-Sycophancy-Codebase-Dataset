package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;

public abstract class BaseDebuggingPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected final void onPreferencesCreated(@NonNull Context context) {
        ListPreference logLevelListPreference = findPreference("log_level");
        LogLevelPreferenceUtils.configureLogLevelListPreference(logLevelListPreference, context, getLogLevel(context));
    }

    protected abstract int getLogLevel(@NonNull Context context);

}
