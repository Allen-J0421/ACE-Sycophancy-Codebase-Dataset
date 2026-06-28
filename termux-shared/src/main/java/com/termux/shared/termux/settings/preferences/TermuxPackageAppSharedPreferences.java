package com.termux.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.android.PackageUtils;
import com.termux.shared.logger.Logger;
import com.termux.shared.settings.preferences.AppSharedPreferences;
import com.termux.shared.settings.preferences.SharedPreferenceUtils;
import com.termux.shared.termux.TermuxUtils;

abstract class TermuxPackageAppSharedPreferences extends AppSharedPreferences
    implements TermuxAppLogLevelPreferences {

    @FunctionalInterface
    protected interface PreferencesFactory<T extends TermuxPackageAppSharedPreferences> {
        T create(@NonNull Context packageContext);
    }

    protected TermuxPackageAppSharedPreferences(@NonNull Context context, @NonNull String preferencesFileBasename) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context, preferencesFileBasename),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context, preferencesFileBasename));
    }

    @Nullable
    protected static <T extends TermuxPackageAppSharedPreferences> T buildPreferences(@NonNull Context context,
                                                                                       @NonNull String packageName,
                                                                                       boolean exitAppOnError,
                                                                                       @NonNull PreferencesFactory<T> factory) {
        Context packageContext = exitAppOnError
            ? TermuxUtils.getContextForPackageOrExitApp(context, packageName, exitAppOnError)
            : PackageUtils.getContextForPackage(context, packageName);
        if (packageContext == null) return null;

        return factory.create(packageContext);
    }

    protected int getStoredLogLevel(@NonNull String key, boolean readFromFile) {
        return SharedPreferenceUtils.getInt(readFromFile ? mMultiProcessSharedPreferences : mSharedPreferences,
            key, Logger.DEFAULT_LOG_LEVEL);
    }

    protected void setStoredLogLevel(@NonNull Context context, @NonNull String key, int logLevel,
                                     boolean commitToFile) {
        SharedPreferenceUtils.setInt(mSharedPreferences, key, Logger.setLogLevel(context, logLevel), commitToFile);
    }

    protected boolean getStoredBoolean(@NonNull String key, boolean defaultValue, boolean readFromFile) {
        return SharedPreferenceUtils.getBoolean(readFromFile ? mMultiProcessSharedPreferences : mSharedPreferences,
            key, defaultValue);
    }

    protected void setStoredBoolean(@NonNull String key, boolean value, boolean commitToFile) {
        SharedPreferenceUtils.setBoolean(mSharedPreferences, key, value, commitToFile);
    }

    @NonNull
    protected abstract String getLogLevelPreferenceKey();

    @Override
    public final int getLogLevel(boolean readFromFile) {
        return getStoredLogLevel(getLogLevelPreferenceKey(), readFromFile);
    }

    @Override
    public final void setLogLevel(@NonNull Context context, int logLevel, boolean commitToFile) {
        setStoredLogLevel(context, getLogLevelPreferenceKey(), logLevel, commitToFile);
    }

}
