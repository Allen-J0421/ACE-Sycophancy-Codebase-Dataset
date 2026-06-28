package com.termux.app.fragments.settings;

import androidx.annotation.Nullable;

public abstract class BaseLogLevelPreferenceDataStore extends BasePreferenceDataStore {

    private static final String KEY_LOG_LEVEL = "log_level";

    protected BaseLogLevelPreferenceDataStore(android.content.Context context) {
        super(context);
    }

    @Override
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        if (!KEY_LOG_LEVEL.equals(key)) return null;
        return String.valueOf(getLogLevel());
    }

    @Override
    public void putString(String key, @Nullable String value) {
        if (!KEY_LOG_LEVEL.equals(key) || value == null) return;
        setLogLevel(Integer.parseInt(value));
    }

    protected abstract int getLogLevel();

    protected abstract void setLogLevel(int logLevel);

}
