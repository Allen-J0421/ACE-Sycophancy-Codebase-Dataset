package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

public abstract class LogLevelPreferenceDataStore extends PreferenceDataStore {

    private static final String LOG_LEVEL_KEY = "log_level";

    protected final Context mContext;

    protected LogLevelPreferenceDataStore(Context context) {
        mContext = context;
    }

    @Override
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        if (!LOG_LEVEL_KEY.equals(key) || !hasPreferences()) return defValue;

        return String.valueOf(getLogLevel());
    }

    @Override
    public void putString(String key, @Nullable String value) {
        if (!LOG_LEVEL_KEY.equals(key) || value == null || !hasPreferences()) return;

        setLogLevel(Integer.parseInt(value));
    }

    protected abstract boolean hasPreferences();

    protected abstract int getLogLevel();

    protected abstract void setLogLevel(int logLevel);

}
