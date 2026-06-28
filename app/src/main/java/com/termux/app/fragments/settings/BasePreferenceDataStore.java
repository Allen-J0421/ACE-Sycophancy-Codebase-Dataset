package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

public abstract class BasePreferenceDataStore extends PreferenceDataStore {

    private final Context mContext;

    protected BasePreferenceDataStore(@NonNull Context context) {
        Context applicationContext = context.getApplicationContext();
        mContext = applicationContext != null ? applicationContext : context;
    }

    @NonNull
    protected Context getContext() {
        return mContext;
    }

}
