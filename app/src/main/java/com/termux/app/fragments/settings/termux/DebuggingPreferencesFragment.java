package com.termux.app.fragments.settings.termux;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.LogLevelPreferenceDataStore;
import com.termux.app.fragments.settings.LogLevelPreferenceFragment;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;
import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants.TERMUX_APP;

@Keep
public class DebuggingPreferencesFragment extends LogLevelPreferenceFragment {

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
    protected Integer getLogLevel(@NonNull Context context) {
        TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(context, true);
        return preferences == null ? null : preferences.getLogLevel();
    }

}

class DebuggingPreferencesDataStore extends LogLevelPreferenceDataStore<TermuxAppSharedPreferences> {

    private static DebuggingPreferencesDataStore mInstance;

    private DebuggingPreferencesDataStore(Context context) {
        super(context, TermuxAppSharedPreferences.build(context, true));
    }

    public static synchronized DebuggingPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DebuggingPreferencesDataStore(context);
        }
        return mInstance;
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
            case TERMUX_APP.KEY_TERMINAL_VIEW_KEY_LOGGING_ENABLED:
                mPreferences.setTerminalViewKeyLoggingEnabled(value);
                break;
            case TERMUX_APP.KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED:
                mPreferences.setPluginErrorNotificationsEnabled(value);
                break;
            case TERMUX_APP.KEY_CRASH_REPORT_NOTIFICATIONS_ENABLED:
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
            case TERMUX_APP.KEY_TERMINAL_VIEW_KEY_LOGGING_ENABLED:
                return mPreferences.isTerminalViewKeyLoggingEnabled();
            case TERMUX_APP.KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED:
                return mPreferences.arePluginErrorNotificationsEnabled(false);
            case TERMUX_APP.KEY_CRASH_REPORT_NOTIFICATIONS_ENABLED:
                return mPreferences.areCrashReportNotificationsEnabled(false);
            default:
                return defValue;
        }
    }

}
