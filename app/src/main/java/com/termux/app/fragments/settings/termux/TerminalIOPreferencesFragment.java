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
public class TerminalIOPreferencesFragment extends TermuxPreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_terminal_io_preferences;
    }

    @Override
    protected PreferenceDataStore createPreferenceDataStore(@NonNull Context context) {
        TermuxAppSharedPreferences preferences = TermuxAppSharedPreferences.build(context, true);
        if (preferences == null) return null;

        return new MappedPreferenceDataStore.Builder()
            .putBoolean("soft_keyboard_enabled", preferences::isSoftKeyboardEnabled, preferences::setSoftKeyboardEnabled)
            .putBoolean("soft_keyboard_enabled_only_if_no_hardware", preferences::isSoftKeyboardEnabledOnlyIfNoHardware, preferences::setSoftKeyboardEnabledOnlyIfNoHardware)
            .build();
    }
}
