package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;

import com.termux.shared.logger.Logger;

public final class LogLevelPreferenceUtils {

    private LogLevelPreferenceUtils() {}

    public static void configureLogLevelListPreference(@Nullable ListPreference logLevelListPreference, @NonNull Context context, int logLevel) {
        if (logLevelListPreference == null) return;

        CharSequence[] logLevels = Logger.getLogLevelsArray();
        CharSequence[] logLevelLabels = Logger.getLogLevelLabelsArray(context, logLevels, true);

        logLevelListPreference.setEntryValues(logLevels);
        logLevelListPreference.setEntries(logLevelLabels);
        logLevelListPreference.setValue(String.valueOf(logLevel));
        logLevelListPreference.setDefaultValue(Logger.DEFAULT_LOG_LEVEL);
    }

}
