package com.termux.app.fragments.settings.termux;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.base.MappedPreferenceDataStore;
import com.termux.app.fragments.settings.base.TermuxPreferenceFragment;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

@Keep
public class TerminalViewPreferencesFragment extends TermuxPreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_terminal_view_preferences;
    }

    @Override
    protected PreferenceDataStore createPreferenceDataStore(@NonNull Context context) {
        TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(context, true);
        if (preferences == null) return null;

        return new MappedPreferenceDataStore.Builder()
            .putBoolean("terminal_margin_adjustment", preferences::isTerminalMarginAdjustmentEnabled, preferences::setTerminalMarginAdjustment)
            .build();
    }
}
