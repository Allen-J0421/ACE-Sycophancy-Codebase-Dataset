package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class LogLevelPreferenceFragment extends BasePreferenceFragment {

    @Override
    protected final void onPreferencesLoaded(@NonNull Context context) {
        Integer logLevel = getLogLevel(context);
        if (logLevel == null) return;

        LogLevelPreferenceUtils.configureLogLevelPreference(this, context, logLevel);
    }

    @Nullable
    protected abstract Integer getLogLevel(@NonNull Context context);

}
