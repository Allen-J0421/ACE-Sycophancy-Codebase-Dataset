package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.termux.shared.logger.Logger;

public final class LogLevelPreferenceUtils {

    private static final String LOGGING_CATEGORY_KEY = "logging";
    private static final String LOG_LEVEL_KEY = "log_level";

    private LogLevelPreferenceUtils() {
    }

    public static void configureLogLevelPreference(@NonNull PreferenceFragmentCompat fragment,
                                                   @NonNull Context context,
                                                   int logLevel) {
        PreferenceCategory loggingCategory = fragment.findPreference(LOGGING_CATEGORY_KEY);
        if (loggingCategory == null) return;

        ListPreference logLevelListPreference = fragment.findPreference(LOG_LEVEL_KEY);
        if (logLevelListPreference == null) return;

        setLogLevelListPreferenceData(logLevelListPreference, context, logLevel);
        loggingCategory.addPreference(logLevelListPreference);
    }

    private static void setLogLevelListPreferenceData(@NonNull ListPreference logLevelListPreference,
                                                      @NonNull Context context,
                                                      int logLevel) {
        CharSequence[] logLevels = Logger.getLogLevelsArray();
        CharSequence[] logLevelLabels = Logger.getLogLevelLabelsArray(context, logLevels, true);

        logLevelListPreference.setEntryValues(logLevels);
        logLevelListPreference.setEntries(logLevelLabels);
        logLevelListPreference.setValue(String.valueOf(logLevel));
        logLevelListPreference.setDefaultValue(Logger.DEFAULT_LOG_LEVEL);
    }

}
