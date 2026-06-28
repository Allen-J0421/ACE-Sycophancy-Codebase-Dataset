package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants.TERMUX_APP;

public abstract class LogLevelPreferenceDataStore<T> extends PreferenceDataStore {

    protected final Context mContext;
    protected final T mPreferences;

    protected LogLevelPreferenceDataStore(Context context, T preferences) {
        mContext = context;
        mPreferences = preferences;
    }

    @Override
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        if (!TERMUX_APP.KEY_LOG_LEVEL.equals(key) || !hasPreferences()) return defValue;

        return String.valueOf(getLogLevel());
    }

    @Override
    public void putString(String key, @Nullable String value) {
        if (!TERMUX_APP.KEY_LOG_LEVEL.equals(key) || value == null || !hasPreferences()) return;

        setLogLevel(Integer.parseInt(value));
    }

    private boolean hasPreferences() {
        return mPreferences != null;
    }

    protected abstract int getLogLevel();

    protected abstract void setLogLevel(int logLevel);

}
