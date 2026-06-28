package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.shared.termux.settings.preferences.TermuxTaskerAppSharedPreferences;

@Keep
public class TermuxTaskerPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_tasker_preferences;
    }

    @NonNull
    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return TermuxTaskerPreferencesDataStore.getInstance(context);
    }
}

class TermuxTaskerPreferencesDataStore extends InitializingPreferenceDataStore {

    private static TermuxTaskerPreferencesDataStore mInstance;

    private TermuxTaskerPreferencesDataStore(Context context) {
        super(context, appContext -> TermuxTaskerAppSharedPreferences.build(appContext, true));
    }

    public static synchronized TermuxTaskerPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TermuxTaskerPreferencesDataStore(context);
        }
        return mInstance;
    }

}
