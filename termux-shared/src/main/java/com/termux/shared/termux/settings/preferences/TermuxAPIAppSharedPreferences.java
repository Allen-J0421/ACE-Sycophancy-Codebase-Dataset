package com.termux.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.settings.preferences.SharedPreferenceUtils;
import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants.TERMUX_API_APP;
import com.termux.shared.termux.TermuxConstants;

public class TermuxAPIAppSharedPreferences extends TermuxPackageAppSharedPreferences {

    private TermuxAPIAppSharedPreferences(@NonNull Context context) {
        super(context, TermuxConstants.TERMUX_API_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION);
    }

    /**
     * Get {@link TermuxAPIAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_API_PACKAGE_NAME}.
     * @return Returns the {@link TermuxAPIAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static TermuxAPIAppSharedPreferences build(@NonNull final Context context) {
        return buildPreferences(context, TermuxConstants.TERMUX_API_PACKAGE_NAME, false,
            TermuxAPIAppSharedPreferences::new);
    }

    /**
     * Get {@link TermuxAPIAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_API_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link TermuxAPIAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static TermuxAPIAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        return buildPreferences(context, TermuxConstants.TERMUX_API_PACKAGE_NAME, exitAppOnError,
            TermuxAPIAppSharedPreferences::new);
    }

    @Override
    protected String getLogLevelPreferenceKey() {
        return TERMUX_API_APP.KEY_LOG_LEVEL;
    }


    public int getLastPendingIntentRequestCode() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, TERMUX_API_APP.KEY_LAST_PENDING_INTENT_REQUEST_CODE, TERMUX_API_APP.DEFAULT_VALUE_KEY_LAST_PENDING_INTENT_REQUEST_CODE);
    }

    public void setLastPendingIntentRequestCode(int lastPendingIntentRequestCode) {
        SharedPreferenceUtils.setInt(mSharedPreferences, TERMUX_API_APP.KEY_LAST_PENDING_INTENT_REQUEST_CODE, lastPendingIntentRequestCode, true);
    }

}
