package com.termux.app.fragments.settings.base;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public abstract class TermuxPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceDataStore preferenceDataStore = createPreferenceDataStore(context);
        if (preferenceDataStore != null) {
            PreferenceManager preferenceManager = getPreferenceManager();
            preferenceManager.setPreferenceDataStore(preferenceDataStore);
        }

        setPreferencesFromResource(getPreferencesResourceId(), rootKey);
        onPreferencesCreated(context);
    }

    @XmlRes
    protected abstract int getPreferencesResourceId();

    @Nullable
    protected abstract PreferenceDataStore createPreferenceDataStore(@NonNull Context context);

    protected void onPreferencesCreated(@NonNull Context context) {
        // Optional hook for subclasses.
    }
}
