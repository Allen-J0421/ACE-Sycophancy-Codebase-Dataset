package com.termux.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceDataStore preferenceDataStore = getPreferenceDataStore(context);
        if (preferenceDataStore != null) {
            getPreferenceManager().setPreferenceDataStore(preferenceDataStore);
        }

        setPreferencesFromResource(getPreferencesResource(), rootKey);
        onPreferencesCreated(context);
    }

    protected abstract int getPreferencesResource();

    @Nullable
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return null;
    }

    protected void onPreferencesCreated(@NonNull Context context) {}

}
