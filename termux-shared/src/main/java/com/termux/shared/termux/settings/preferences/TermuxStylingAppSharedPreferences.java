package com.termux.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants.TERMUX_STYLING_APP;
import com.termux.shared.termux.TermuxConstants;

public class TermuxStylingAppSharedPreferences extends TermuxPackageAppSharedPreferences {

    private TermuxStylingAppSharedPreferences(@NonNull Context context) {
        super(context, TermuxConstants.TERMUX_STYLING_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION);
    }

    /**
     * Get {@link TermuxStylingAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_STYLING_PACKAGE_NAME}.
     * @return Returns the {@link TermuxStylingAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static TermuxStylingAppSharedPreferences build(@NonNull final Context context) {
        return buildPreferences(context, TermuxConstants.TERMUX_STYLING_PACKAGE_NAME, false,
            TermuxStylingAppSharedPreferences::new);
    }

    /**
     * Get {@link TermuxStylingAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_STYLING_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link TermuxStylingAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static TermuxStylingAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        return buildPreferences(context, TermuxConstants.TERMUX_STYLING_PACKAGE_NAME, exitAppOnError,
            TermuxStylingAppSharedPreferences::new);
    }



    public int getLogLevel(boolean readFromFile) {
        return getStoredLogLevel(TERMUX_STYLING_APP.KEY_LOG_LEVEL, readFromFile);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        setStoredLogLevel(context, TERMUX_STYLING_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }

}
