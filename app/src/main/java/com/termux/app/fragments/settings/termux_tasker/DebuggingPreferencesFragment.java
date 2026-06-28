package com.termux.app.fragments.settings.termux_tasker;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BasePreferenceFragment;
import com.termux.app.fragments.settings.LogLevelPreferenceDataStore;
import com.termux.app.fragments.settings.LogLevelPreferenceUtils;
import com.termux.shared.termux.settings.preferences.TermuxTaskerAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_tasker_debugging_preferences;
    }

    @NonNull
    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return DebuggingPreferencesDataStore.getInstance(context);
    }

    @Override
    protected void onPreferencesLoaded(@NonNull Context context) {
        TermuxTaskerAppSharedPreferences preferences = TermuxTaskerAppSharedPreferences.build(context, true);
        if (preferences == null) return;

        LogLevelPreferenceUtils.configureLogLevelPreference(this, context, preferences.getLogLevel(true));
    }
}

class DebuggingPreferencesDataStore extends LogLevelPreferenceDataStore<TermuxTaskerAppSharedPreferences> {

    private static DebuggingPreferencesDataStore mInstance;

    private DebuggingPreferencesDataStore(Context context) {
        super(context, TermuxTaskerAppSharedPreferences.build(context, true));
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
