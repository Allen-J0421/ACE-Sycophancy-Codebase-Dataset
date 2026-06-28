package com.termux.app.fragments.settings.termux_widget;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BaseDebuggingPreferencesFragment;
import com.termux.app.fragments.settings.BaseLogLevelPreferenceDataStore;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.settings.preferences.TermuxWidgetAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BaseDebuggingPreferencesFragment {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_widget_debugging_preferences;
    }

    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return new DebuggingPreferencesDataStore(context);
    }

    @Override
    protected int getLogLevel(@NonNull Context context) {
        TermuxWidgetAppSharedPreferences preferences = TermuxWidgetAppSharedPreferences.build(context, true);
        return preferences != null ? preferences.getLogLevel(true) : Logger.DEFAULT_LOG_LEVEL;
    }
}

class DebuggingPreferencesDataStore extends BaseLogLevelPreferenceDataStore {

    private final TermuxWidgetAppSharedPreferences mPreferences;

    DebuggingPreferencesDataStore(@NonNull Context context) {
        super(context);
        mPreferences = TermuxWidgetAppSharedPreferences.build(context, true);
    }

    @Override
    protected int getLogLevel() {
        return mPreferences != null ? mPreferences.getLogLevel(true) : Logger.DEFAULT_LOG_LEVEL;
    }

    @Override
    protected void setLogLevel(int logLevel) {
        if (mPreferences == null) return;
        mPreferences.setLogLevel(getContext(), logLevel, true);
    }

}
