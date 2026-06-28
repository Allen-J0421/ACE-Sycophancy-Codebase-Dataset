package com.termux.app.fragments.settings.termux;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BasePreferenceFragment;
import com.termux.app.fragments.settings.LogLevelPreferenceDataStore;
import com.termux.app.fragments.settings.LogLevelPreferenceUtils;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_debugging_preferences;
    }

    @NonNull
    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return DebuggingPreferencesDataStore.getInstance(context);
    }

    @Override
    protected void onPreferencesLoaded(@NonNull Context context) {
        TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(context, true);
        if (preferences == null) return;

        LogLevelPreferenceUtils.configureLogLevelPreference(this, context, preferences.getLogLevel());
    }

}

class DebuggingPreferencesDataStore extends LogLevelPreferenceDataStore {

    private final TermuxAppSharedPreferences mPreferences;

    private static DebuggingPreferencesDataStore mInstance;

    private DebuggingPreferencesDataStore(Context context) {
        super(context);
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

    public static synchronized DebuggingPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DebuggingPreferencesDataStore(context);
        }
        return mInstance;
    }



    @Override
    protected boolean hasPreferences() {
        return mPreferences != null;
    }

    @Override
    protected int getLogLevel() {
        return mPreferences.getLogLevel();
    }

    @Override
    protected void setLogLevel(int logLevel) {
        mPreferences.setLogLevel(mContext, logLevel);
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
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null) return defValue;
        switch (key) {
            case "terminal_view_key_logging_enabled":
                return mPreferences.isTerminalViewKeyLoggingEnabled();
            case "plugin_error_notifications_enabled":
                return mPreferences.arePluginErrorNotificationsEnabled(false);
            case "crash_report_notifications_enabled":
                return mPreferences.areCrashReportNotificationsEnabled(false);
            default:
                return defValue;
        }
    }

}
