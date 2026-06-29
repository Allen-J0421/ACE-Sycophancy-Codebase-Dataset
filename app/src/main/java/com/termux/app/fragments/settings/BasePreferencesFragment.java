package com.termux.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

@Keep
public abstract class BasePreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(createDataStore(context));

        setPreferencesFromResource(getPreferencesXmlResource(), rootKey);

        onPreferencesCreated(context);
    }

    protected abstract int getPreferencesXmlResource();

    protected abstract PreferenceDataStore createDataStore(Context context);

    protected void onPreferencesCreated(@NonNull Context context) {}

}
