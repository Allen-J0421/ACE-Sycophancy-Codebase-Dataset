package com.termux.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.termux.TermuxConstants;
import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants.TERMUX_TASKER_APP;

public class TermuxTaskerAppSharedPreferences extends TermuxPendingIntentRequestCodeAppSharedPreferences {

    private  TermuxTaskerAppSharedPreferences(@NonNull Context context) {
        super(context, TermuxConstants.TERMUX_TASKER_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION);
    }

    /**
     * Get {@link TermuxTaskerAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_TASKER_PACKAGE_NAME}.
     * @return Returns the {@link TermuxTaskerAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static TermuxTaskerAppSharedPreferences build(@NonNull final Context context) {
        return buildPreferences(context, TermuxConstants.TERMUX_TASKER_PACKAGE_NAME, false,
            TermuxTaskerAppSharedPreferences::new);
    }

    /**
     * Get {@link TermuxTaskerAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_TASKER_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link TermuxTaskerAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static  TermuxTaskerAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        return buildPreferences(context, TermuxConstants.TERMUX_TASKER_PACKAGE_NAME, exitAppOnError,
            TermuxTaskerAppSharedPreferences::new);
    }

    @Override
    protected String getLogLevelPreferenceKey() {
        return TERMUX_TASKER_APP.KEY_LOG_LEVEL;
    }

    @Override
    protected String getLastPendingIntentRequestCodePreferenceKey() {
        return TERMUX_TASKER_APP.KEY_LAST_PENDING_INTENT_REQUEST_CODE;
    }

    @Override
    protected int getDefaultLastPendingIntentRequestCode() {
        return TERMUX_TASKER_APP.DEFAULT_VALUE_KEY_LAST_PENDING_INTENT_REQUEST_CODE;
    }

    @Override
    protected boolean shouldCommitLastPendingIntentRequestCodeToFile() {
        return false;
    }

}
