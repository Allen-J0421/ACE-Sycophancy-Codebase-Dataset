package com.termux.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;

import com.termux.shared.settings.preferences.SharedPreferenceUtils;

abstract class TermuxPendingIntentRequestCodeAppSharedPreferences
    extends TermuxPackageAppSharedPreferences {

    protected TermuxPendingIntentRequestCodeAppSharedPreferences(@NonNull Context context,
                                                                 @NonNull String preferencesFileBasename) {
        super(context, preferencesFileBasename);
    }

    @NonNull
    protected abstract String getLastPendingIntentRequestCodePreferenceKey();

    protected abstract int getDefaultLastPendingIntentRequestCode();

    protected abstract boolean shouldCommitLastPendingIntentRequestCodeToFile();

    public int getLastPendingIntentRequestCode() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, getLastPendingIntentRequestCodePreferenceKey(),
            getDefaultLastPendingIntentRequestCode());
    }

    public void setLastPendingIntentRequestCode(int lastPendingIntentRequestCode) {
        SharedPreferenceUtils.setInt(mSharedPreferences, getLastPendingIntentRequestCodePreferenceKey(),
            lastPendingIntentRequestCode, shouldCommitLastPendingIntentRequestCodeToFile());
    }

}
