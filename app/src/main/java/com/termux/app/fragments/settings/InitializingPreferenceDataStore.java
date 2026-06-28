package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

public class InitializingPreferenceDataStore extends PreferenceDataStore {

    public interface Initializer {
        void initialize(@NonNull Context context);
    }

    protected InitializingPreferenceDataStore(@NonNull Context context, @NonNull Initializer initializer) {
        initializer.initialize(context);
    }

}
