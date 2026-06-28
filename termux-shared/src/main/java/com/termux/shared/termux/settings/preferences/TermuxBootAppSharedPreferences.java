package com.termux.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants.TERMUX_BOOT_APP;
import com.termux.shared.termux.TermuxConstants;

public class TermuxBootAppSharedPreferences extends TermuxPackageAppSharedPreferences {

    private TermuxBootAppSharedPreferences(@NonNull Context context) {
        super(context, TermuxConstants.TERMUX_BOOT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION);
    }

    /**
     * Get {@link TermuxBootAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_BOOT_PACKAGE_NAME}.
     * @return Returns the {@link TermuxBootAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static TermuxBootAppSharedPreferences build(@NonNull final Context context) {
        return buildPreferences(context, TermuxConstants.TERMUX_BOOT_PACKAGE_NAME, false,
            TermuxBootAppSharedPreferences::new);
    }

    /**
     * Get {@link TermuxBootAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_BOOT_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link TermuxBootAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static TermuxBootAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        return buildPreferences(context, TermuxConstants.TERMUX_BOOT_PACKAGE_NAME, exitAppOnError,
            TermuxBootAppSharedPreferences::new);
    }



    public int getLogLevel(boolean readFromFile) {
        return getStoredLogLevel(TERMUX_BOOT_APP.KEY_LOG_LEVEL, readFromFile);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        setStoredLogLevel(context, TERMUX_BOOT_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }

}
