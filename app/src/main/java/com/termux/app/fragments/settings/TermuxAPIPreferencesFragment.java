package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.shared.termux.settings.preferences.TermuxAPIAppSharedPreferences;

@Keep
public class TermuxAPIPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_api_preferences;
    }

    @NonNull
    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return TermuxAPIPreferencesDataStore.getInstance(context);
    }
}

class TermuxAPIPreferencesDataStore extends InitializingPreferenceDataStore {

    private static TermuxAPIPreferencesDataStore mInstance;

    private TermuxAPIPreferencesDataStore(Context context) {
        super(context, appContext -> TermuxAPIAppSharedPreferences.build(appContext, true));
    }

    public static synchronized TermuxAPIPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TermuxAPIPreferencesDataStore(context);
        }
        return mInstance;
    }

}
