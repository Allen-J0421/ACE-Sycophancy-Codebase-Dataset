package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.termux.shared.logger.Logger;
import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants.TERMUX_APP;

public final class LogLevelPreferenceUtils {

    private static final String LOGGING_CATEGORY_KEY = "logging";

    private LogLevelPreferenceUtils() {
    }

    public static void configureLogLevelPreference(@NonNull PreferenceFragmentCompat fragment,
                                                   @NonNull Context context,
                                                   int logLevel) {
        if (fragment.findPreference(LOGGING_CATEGORY_KEY) == null) return;

        ListPreference logLevelListPreference = fragment.findPreference(TERMUX_APP.KEY_LOG_LEVEL);
        if (logLevelListPreference == null) return;

        setLogLevelListPreferenceData(logLevelListPreference, context, logLevel);
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
