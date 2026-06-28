package com.termux.app.fragments.settings.termux_float;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BasePreferenceFragment;
import com.termux.app.fragments.settings.LogLevelPreferenceDataStore;
import com.termux.app.fragments.settings.LogLevelPreferenceUtils;
import com.termux.shared.termux.settings.preferences.TermuxFloatAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_float_debugging_preferences;
    }

    @NonNull
    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return DebuggingPreferencesDataStore.getInstance(context);
    }

    @Override
    protected void onPreferencesLoaded(@NonNull Context context) {
        TermuxFloatAppSharedPreferences preferences = TermuxFloatAppSharedPreferences.build(context, true);
        if (preferences == null) return;

        LogLevelPreferenceUtils.configureLogLevelPreference(this, context, preferences.getLogLevel(true));
    }
}

class DebuggingPreferencesDataStore extends LogLevelPreferenceDataStore {

    private final TermuxFloatAppSharedPreferences mPreferences;

    private static DebuggingPreferencesDataStore mInstance;

    private DebuggingPreferencesDataStore(Context context) {
        super(context);
        mPreferences = TermuxFloatAppSharedPreferences.build(context, true);
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
        return mPreferences.getLogLevel(true);
    }

    @Override
    protected void setLogLevel(int logLevel) {
        mPreferences.setLogLevel(mContext, logLevel, true);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null) return;
        if (key == null) return;

        switch (key) {
            case "terminal_view_key_logging_enabled":
                mPreferences.setTerminalViewKeyLoggingEnabled(value, true);
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
                return mPreferences.isTerminalViewKeyLoggingEnabled(true);
            default:
                return defValue;
        }
    }

}
