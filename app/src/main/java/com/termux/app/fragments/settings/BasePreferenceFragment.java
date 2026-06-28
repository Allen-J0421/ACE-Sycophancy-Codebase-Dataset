package com.termux.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.XmlRes;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public final void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        getPreferenceManager().setPreferenceDataStore(getPreferenceDataStore(context));
        setPreferencesFromResource(getPreferencesResourceId(), rootKey);
        onPreferencesLoaded(context);
    }

    @XmlRes
    protected abstract int getPreferencesResourceId();

    @NonNull
    protected abstract PreferenceDataStore getPreferenceDataStore(@NonNull Context context);

    protected void onPreferencesLoaded(@NonNull Context context) {
    }

}
