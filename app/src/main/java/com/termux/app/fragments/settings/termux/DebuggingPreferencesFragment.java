package com.termux.app.fragments.settings.termux;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferenceDataStore;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferencesFragment;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BasePackageDebuggingPreferencesFragment<TermuxAppSharedPreferences> {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_debugging_preferences;
    }

    @Override
    protected PreferenceDataStore createPreferenceDataStore(@NonNull Context context,
                                                            @Nullable TermuxAppSharedPreferences preferences) {
        return new DebuggingPreferencesDataStore(context, preferences);
    }

    @Override
    protected TermuxAppSharedPreferences buildPreferences(@NonNull Context context) {
        return TermuxAppSharedPreferences.build(context, true);
    }

    @Override
    protected boolean shouldReadLogLevelFromFile() {
        return false;
    }

}

class DebuggingPreferencesDataStore extends BasePackageDebuggingPreferenceDataStore<TermuxAppSharedPreferences> {

    DebuggingPreferencesDataStore(@NonNull Context context,
                                  @Nullable TermuxAppSharedPreferences preferences) {
        super(context, preferences);
    }

    @Override
    protected boolean shouldReadLogLevelFromFile() {
        return false;
    }

    @Override
    protected boolean shouldCommitLogLevelToFile() {
        return false;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        TermuxAppSharedPreferences preferences = getPreferences();
        if (preferences == null) return;
        if (key == null) return;

        switch (key) {
            case "terminal_view_key_logging_enabled":
                preferences.setTerminalViewKeyLoggingEnabled(value);
                break;
            case "plugin_error_notifications_enabled":
                preferences.setPluginErrorNotificationsEnabled(value);
                break;
            case "crash_report_notifications_enabled":
                preferences.setCrashReportNotificationsEnabled(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        TermuxAppSharedPreferences preferences = getPreferences();
        if (preferences == null) return false;
        switch (key) {
            case "terminal_view_key_logging_enabled":
                return preferences.isTerminalViewKeyLoggingEnabled();
            case "plugin_error_notifications_enabled":
                return preferences.arePluginErrorNotificationsEnabled(false);
            case "crash_report_notifications_enabled":
                return preferences.areCrashReportNotificationsEnabled(false);
            default:
                return false;
        }
    }

}
