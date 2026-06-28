package com.termux.app.fragments.settings.termux_widget;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.base.MappedPreferenceDataStore;
import com.termux.app.fragments.settings.base.TermuxDebuggingPreferenceFragment;
import com.termux.shared.logger.Logger;
import com.termux.shared.termux.settings.preferences.TermuxWidgetAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends TermuxDebuggingPreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_widget_debugging_preferences;
    }

    @Override
    protected PreferenceDataStore createPreferenceDataStore(@NonNull Context context) {
        TermuxWidgetAppSharedPreferences preferences = TermuxWidgetAppSharedPreferences.build(context, true);
        if (preferences == null) return null;

        return new MappedPreferenceDataStore.Builder()
            .putString("log_level",
                () -> String.valueOf(preferences.getLogLevel(true)),
                value -> preferences.setLogLevel(context, Integer.parseInt(value), true))
            .build();
    }

    @Override
    protected int getLogLevel(@NonNull Context context) {
        TermuxWidgetAppSharedPreferences preferences = TermuxWidgetAppSharedPreferences.build(context, true);
        return preferences == null ? Logger.DEFAULT_LOG_LEVEL : preferences.getLogLevel(true);
    }
}
