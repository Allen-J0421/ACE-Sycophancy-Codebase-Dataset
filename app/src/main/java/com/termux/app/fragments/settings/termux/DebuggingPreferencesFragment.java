package com.termux.app.fragments.settings.termux;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BaseDebuggingPreferencesFragment;
import com.termux.app.fragments.settings.BaseLogLevelPreferenceDataStore;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.logger.Logger;

@Keep
public class DebuggingPreferencesFragment extends BaseDebuggingPreferencesFragment {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_debugging_preferences;
    }

    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return new DebuggingPreferencesDataStore(context);
    }

    @Override
    protected int getLogLevel(@NonNull Context context) {
        TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(context, true);
        return preferences != null ? preferences.getLogLevel() : Logger.DEFAULT_LOG_LEVEL;
    }

}

class DebuggingPreferencesDataStore extends BaseLogLevelPreferenceDataStore {

    private final TermuxAppSharedPreferences mPreferences;

    DebuggingPreferencesDataStore(@NonNull Context context) {
        super(context);
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null) return;
        if (key == null) return;

        switch (key) {
            case "terminal_view_key_logging_enabled":
                    mPreferences.setTerminalViewKeyLoggingEnabled(value);
                break;
            case "plugin_error_notifications_enabled":
                mPreferences.setPluginErrorNotificationsEnabled(value);
                break;
            case "crash_report_notifications_enabled":
                mPreferences.setCrashReportNotificationsEnabled(value);
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLogLevel() {
        return mPreferences != null ? mPreferences.getLogLevel() : Logger.DEFAULT_LOG_LEVEL;
    }

    @Override
    protected void setLogLevel(int logLevel) {
        if (mPreferences == null) return;
        mPreferences.setLogLevel(getContext(), logLevel);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null) return false;
        switch (key) {
            case "terminal_view_key_logging_enabled":
                return mPreferences.isTerminalViewKeyLoggingEnabled();
            case "plugin_error_notifications_enabled":
                return mPreferences.arePluginErrorNotificationsEnabled(false);
            case "crash_report_notifications_enabled":
                return mPreferences.areCrashReportNotificationsEnabled(false);
            default:
                return false;
        }
    }

}
