package com.termux.app.fragments.settings.termux_widget;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BasePreferenceFragment;
import com.termux.app.fragments.settings.LogLevelPreferenceDataStore;
import com.termux.app.fragments.settings.LogLevelPreferenceUtils;
import com.termux.shared.termux.settings.preferences.TermuxWidgetAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_widget_debugging_preferences;
    }

    @NonNull
    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return DebuggingPreferencesDataStore.getInstance(context);
    }

    @Override
    protected void onPreferencesLoaded(@NonNull Context context) {
        TermuxWidgetAppSharedPreferences preferences = TermuxWidgetAppSharedPreferences.build(context, true);
        if (preferences == null) return;

        LogLevelPreferenceUtils.configureLogLevelPreference(this, context, preferences.getLogLevel(true));
    }
}

class DebuggingPreferencesDataStore extends LogLevelPreferenceDataStore<TermuxWidgetAppSharedPreferences> {

    private static DebuggingPreferencesDataStore mInstance;

    private DebuggingPreferencesDataStore(Context context) {
        super(context, TermuxWidgetAppSharedPreferences.build(context, true));
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
