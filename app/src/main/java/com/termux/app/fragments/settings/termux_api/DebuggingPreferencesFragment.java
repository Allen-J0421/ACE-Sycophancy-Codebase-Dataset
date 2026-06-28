package com.termux.app.fragments.settings.termux_api;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.LogLevelPreferenceDataStore;
import com.termux.app.fragments.settings.LogLevelPreferenceFragment;
import com.termux.shared.termux.settings.preferences.TermuxAPIAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends LogLevelPreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_api_debugging_preferences;
    }

    @NonNull
    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return DebuggingPreferencesDataStore.getInstance(context);
    }

    @Override
    protected Integer getLogLevel(@NonNull Context context) {
        TermuxAPIAppSharedPreferences preferences = TermuxAPIAppSharedPreferences.build(context, true);
        return preferences == null ? null : preferences.getLogLevel(true);
    }
}

class DebuggingPreferencesDataStore extends LogLevelPreferenceDataStore<TermuxAPIAppSharedPreferences> {

    private static DebuggingPreferencesDataStore mInstance;

    private DebuggingPreferencesDataStore(Context context) {
        super(context, TermuxAPIAppSharedPreferences.build(context, true));
    }

    public static synchronized DebuggingPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DebuggingPreferencesDataStore(context);
        }
        return mInstance;
    }

    @Override
    protected int getLogLevel() {
        return mPreferences.getLogLevel(true);
    }

    @Override
    protected void setLogLevel(int logLevel) {
        mPreferences.setLogLevel(mContext, logLevel, true);
    }

}
