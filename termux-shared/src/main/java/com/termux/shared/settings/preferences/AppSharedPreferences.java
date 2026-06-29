package com.termux.shared.settings.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.termux.shared.logger.Logger;

/** A class that holds {@link SharedPreferences} objects for apps. */
public class AppSharedPreferences {

    private static final String KEY_LOG_LEVEL = "log_level";

    /** The {@link Context} for operations. */
    protected final Context mContext;

    /** The {@link SharedPreferences} that ideally should be created with {@link SharedPreferenceUtils#getPrivateSharedPreferences(Context, String)}. */
    protected final SharedPreferences mSharedPreferences;

    /** The {@link SharedPreferences}that ideally should be created with {@link SharedPreferenceUtils#getPrivateAndMultiProcessSharedPreferences(Context, String)}. */
    protected final SharedPreferences mMultiProcessSharedPreferences;

    protected AppSharedPreferences(@NonNull Context context, @Nullable SharedPreferences sharedPreferences) {
        this(context, sharedPreferences, null);
    }

    protected AppSharedPreferences(@NonNull Context context, @Nullable SharedPreferences sharedPreferences,
                                   @Nullable SharedPreferences multiProcessSharedPreferences) {
        mContext = context;
        mSharedPreferences = sharedPreferences;
        mMultiProcessSharedPreferences = multiProcessSharedPreferences;
    }



    /** Get {@link #mContext}. */
    public Context getContext() {
        return mContext;
    }

    /** Get {@link #mSharedPreferences}. */
    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    /** Get {@link #mMultiProcessSharedPreferences}. */
    public SharedPreferences getMultiProcessSharedPreferences() {
        return mMultiProcessSharedPreferences;
    }



    public int getLogLevel(boolean readFromFile) {
        if (readFromFile)
            return SharedPreferenceUtils.getInt(mMultiProcessSharedPreferences, KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
        else
            return SharedPreferenceUtils.getInt(mSharedPreferences, KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, KEY_LOG_LEVEL, logLevel, commitToFile);
    }

}
