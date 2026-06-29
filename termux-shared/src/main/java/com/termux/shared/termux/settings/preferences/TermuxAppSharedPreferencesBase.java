package com.termux.shared.termux.settings.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.android.PackageUtils;
import com.termux.shared.settings.preferences.AppSharedPreferences;
import com.termux.shared.termux.TermuxUtils;

public abstract class TermuxAppSharedPreferencesBase extends AppSharedPreferences {

    @FunctionalInterface
    protected interface ContextConstructor<T> {
        T create(@NonNull Context context);
    }

    protected TermuxAppSharedPreferencesBase(@NonNull Context context,
                                              @Nullable SharedPreferences sharedPreferences,
                                              @Nullable SharedPreferences multiProcessSharedPreferences) {
        super(context, sharedPreferences, multiProcessSharedPreferences);
    }

    @Nullable
    protected static <T extends TermuxAppSharedPreferencesBase> T build(
            @NonNull Context context, @NonNull String packageName,
            @NonNull ContextConstructor<T> constructor) {
        Context packageContext = PackageUtils.getContextForPackage(context, packageName);
        return packageContext != null ? constructor.create(packageContext) : null;
    }

    @Nullable
    protected static <T extends TermuxAppSharedPreferencesBase> T build(
            @NonNull Context context, @NonNull String packageName,
            boolean exitAppOnError, @NonNull ContextConstructor<T> constructor) {
        Context packageContext = TermuxUtils.getContextForPackageOrExitApp(context, packageName, exitAppOnError);
        return packageContext != null ? constructor.create(packageContext) : null;
    }

}
