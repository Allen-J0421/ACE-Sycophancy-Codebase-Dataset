package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.logger.Logger;
import com.termux.shared.termux.settings.preferences.TermuxAppLogLevelPreferences;

public class BasePackageDebuggingPreferenceDataStore<T extends TermuxAppLogLevelPreferences>
    extends BaseLogLevelPreferenceDataStore {

    @Nullable
    private final T mPreferences;

    protected BasePackageDebuggingPreferenceDataStore(@NonNull Context context, @Nullable T preferences) {
        super(context);
        mPreferences = preferences;
    }

    @Nullable
    protected final T getPreferences() {
        return mPreferences;
    }

    @Override
    protected int getLogLevel() {
        return mPreferences != null ? mPreferences.getLogLevel(shouldReadLogLevelFromFile())
            : Logger.DEFAULT_LOG_LEVEL;
    }

    @Override
    protected void setLogLevel(int logLevel) {
        if (mPreferences == null) return;
        mPreferences.setLogLevel(getContext(), logLevel, shouldCommitLogLevelToFile());
    }

    protected boolean shouldReadLogLevelFromFile() {
        return true;
    }

    protected boolean shouldCommitLogLevelToFile() {
        return true;
    }

}
