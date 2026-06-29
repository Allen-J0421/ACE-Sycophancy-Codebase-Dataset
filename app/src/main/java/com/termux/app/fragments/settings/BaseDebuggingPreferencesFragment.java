package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceCategory;

import com.termux.shared.logger.Logger;

@Keep
public abstract class BaseDebuggingPreferencesFragment extends BasePreferencesFragment {

    @Override
    protected void onPreferencesCreated(@NonNull Context context) {
        configureLoggingPreferences(context);
    }

    @Nullable
    protected abstract Integer getCurrentLogLevel(Context context);

    private void configureLoggingPreferences(@NonNull Context context) {
        PreferenceCategory loggingCategory = findPreference("logging");
        if (loggingCategory == null) return;

        ListPreference logLevelListPreference = findPreference("log_level");
        if (logLevelListPreference != null) {
            Integer logLevel = getCurrentLogLevel(context);
            if (logLevel == null) return;
            setLogLevelListPreferenceData(logLevelListPreference, context, logLevel);
            loggingCategory.addPreference(logLevelListPreference);
        }
    }

    public static ListPreference setLogLevelListPreferenceData(ListPreference logLevelListPreference, Context context, int logLevel) {
        if (logLevelListPreference == null)
            logLevelListPreference = new ListPreference(context);

        CharSequence[] logLevels = Logger.getLogLevelsArray();
        CharSequence[] logLevelLabels = Logger.getLogLevelLabelsArray(context, logLevels, true);

        logLevelListPreference.setEntryValues(logLevels);
        logLevelListPreference.setEntries(logLevelLabels);

        logLevelListPreference.setValue(String.valueOf(logLevel));
        logLevelListPreference.setDefaultValue(Logger.DEFAULT_LOG_LEVEL);

        return logLevelListPreference;
    }

}
