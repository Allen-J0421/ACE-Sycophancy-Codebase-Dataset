package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.shared.termux.settings.preferences.TermuxFloatAppSharedPreferences;

@Keep
public class TermuxFloatPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_float_preferences;
    }

    @NonNull
    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return TermuxFloatPreferencesDataStore.getInstance(context);
    }
}

class TermuxFloatPreferencesDataStore extends InitializingPreferenceDataStore {

    private static TermuxFloatPreferencesDataStore mInstance;

    private TermuxFloatPreferencesDataStore(Context context) {
        super(context, appContext -> TermuxFloatAppSharedPreferences.build(appContext, true));
    }

    public static synchronized TermuxFloatPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TermuxFloatPreferencesDataStore(context);
        }
        return mInstance;
    }

}
