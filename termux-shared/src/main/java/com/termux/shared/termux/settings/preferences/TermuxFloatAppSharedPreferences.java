package com.termux.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.settings.preferences.SharedPreferenceUtils;
import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants.TERMUX_FLOAT_APP;
import com.termux.shared.termux.TermuxConstants;

public class TermuxFloatAppSharedPreferences extends TermuxFontSizeAppSharedPreferences {

    private TermuxFloatAppSharedPreferences(@NonNull Context context) {
        super(context, TermuxConstants.TERMUX_FLOAT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION);
    }

    /**
     * Get {@link TermuxFloatAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_FLOAT_PACKAGE_NAME}.
     * @return Returns the {@link TermuxFloatAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static TermuxFloatAppSharedPreferences build(@NonNull final Context context) {
        return buildPreferences(context, TermuxConstants.TERMUX_FLOAT_PACKAGE_NAME, false,
            TermuxFloatAppSharedPreferences::new);
    }

    /**
     * Get {@link TermuxFloatAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link TermuxConstants#TERMUX_FLOAT_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link TermuxFloatAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static TermuxFloatAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        return buildPreferences(context, TermuxConstants.TERMUX_FLOAT_PACKAGE_NAME, exitAppOnError,
            TermuxFloatAppSharedPreferences::new);
    }



    public int getWindowX() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, TERMUX_FLOAT_APP.KEY_WINDOW_X, 200);

    }

    public void setWindowX(int value) {
        SharedPreferenceUtils.setInt(mSharedPreferences, TERMUX_FLOAT_APP.KEY_WINDOW_X, value, false);
    }

    public int getWindowY() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, TERMUX_FLOAT_APP.KEY_WINDOW_Y, 200);

    }

    public void setWindowY(int value) {
        SharedPreferenceUtils.setInt(mSharedPreferences, TERMUX_FLOAT_APP.KEY_WINDOW_Y, value, false);
    }



    public int getWindowWidth() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, TERMUX_FLOAT_APP.KEY_WINDOW_WIDTH, 500);

    }

    public void setWindowWidth(int value) {
        SharedPreferenceUtils.setInt(mSharedPreferences, TERMUX_FLOAT_APP.KEY_WINDOW_WIDTH, value, false);
    }

    public int getWindowHeight() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, TERMUX_FLOAT_APP.KEY_WINDOW_HEIGHT, 500);

    }

    public void setWindowHeight(int value) {
        SharedPreferenceUtils.setInt(mSharedPreferences, TERMUX_FLOAT_APP.KEY_WINDOW_HEIGHT, value, false);
    }

    @Override
    protected String getFontSizePreferenceKey() {
        return TERMUX_FLOAT_APP.KEY_FONTSIZE;
    }


    public int getLogLevel(boolean readFromFile) {
        return getStoredLogLevel(TERMUX_FLOAT_APP.KEY_LOG_LEVEL, readFromFile);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        setStoredLogLevel(context, TERMUX_FLOAT_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }


    public boolean isTerminalViewKeyLoggingEnabled(boolean readFromFile) {
        return getStoredBoolean(TERMUX_FLOAT_APP.KEY_TERMINAL_VIEW_KEY_LOGGING_ENABLED,
            TERMUX_FLOAT_APP.DEFAULT_VALUE_TERMINAL_VIEW_KEY_LOGGING_ENABLED, readFromFile);
    }

    public void setTerminalViewKeyLoggingEnabled(boolean value, boolean commitToFile) {
        setStoredBoolean(TERMUX_FLOAT_APP.KEY_TERMINAL_VIEW_KEY_LOGGING_ENABLED, value, commitToFile);
    }

}
