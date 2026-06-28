package com.termux.app.fragments.settings.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;

public abstract class TermuxDebuggingPreferenceFragment extends TermuxPreferenceFragment {

    private static final String LOGGING_CATEGORY_KEY = "logging";
    private static final String LOG_LEVEL_KEY = "log_level";

    @Override
    protected final void onPreferencesCreated(@NonNull Context context) {
        configureLogLevelPreference(context);
    }

    protected final void configureLogLevelPreference(@NonNull Context context) {
        PreferenceCategory loggingCategory = findPreference(LOGGING_CATEGORY_KEY);
        if (loggingCategory == null) return;

        ListPreference logLevelListPreference = findPreference(LOG_LEVEL_KEY);
        if (logLevelListPreference == null) return;

        logLevelListPreference = DebuggingPreferenceUtils.configureLogLevelListPreference(
            logLevelListPreference, context, getLogLevel(context));
        loggingCategory.addPreference(logLevelListPreference);
    }

    protected abstract int getLogLevel(@NonNull Context context);
}
