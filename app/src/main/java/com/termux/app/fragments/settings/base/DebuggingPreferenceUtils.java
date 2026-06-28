package com.termux.app.fragments.settings.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;

import com.termux.shared.logger.Logger;

public final class DebuggingPreferenceUtils {

    private DebuggingPreferenceUtils() {
    }

    @NonNull
    public static ListPreference configureLogLevelListPreference(@Nullable ListPreference listPreference, @NonNull Context context, int logLevel) {
        if (listPreference == null) {
            listPreference = new ListPreference(context);
        }

        CharSequence[] logLevels = Logger.getLogLevelsArray();
        CharSequence[] logLevelLabels = Logger.getLogLevelLabelsArray(context, logLevels, true);

        listPreference.setEntryValues(logLevels);
        listPreference.setEntries(logLevelLabels);
        listPreference.setValue(String.valueOf(logLevel));
        listPreference.setDefaultValue(String.valueOf(Logger.DEFAULT_LOG_LEVEL));

        return listPreference;
    }
}
