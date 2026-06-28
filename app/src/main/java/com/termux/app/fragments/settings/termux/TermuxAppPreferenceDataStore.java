package com.termux.app.fragments.settings.termux;

import android.content.Context;

import androidx.preference.PreferenceDataStore;

import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

abstract class TermuxAppPreferenceDataStore extends PreferenceDataStore {

    protected final TermuxAppSharedPreferences mPreferences;

    protected TermuxAppPreferenceDataStore(Context context) {
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }

}
