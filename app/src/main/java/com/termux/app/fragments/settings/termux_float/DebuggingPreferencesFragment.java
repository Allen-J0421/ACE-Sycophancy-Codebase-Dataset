package com.termux.app.fragments.settings.termux_float;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferenceDataStore;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferencesFragment;
import com.termux.shared.termux.settings.preferences.TermuxFloatAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BasePackageDebuggingPreferencesFragment<TermuxFloatAppSharedPreferences> {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_float_debugging_preferences;
    }

    @Override
    protected PreferenceDataStore createPreferenceDataStore(@NonNull Context context,
                                                            @Nullable TermuxFloatAppSharedPreferences preferences) {
        return new DebuggingPreferencesDataStore(context, preferences);
    }

    @Override
    protected TermuxFloatAppSharedPreferences buildPreferences(@NonNull Context context) {
        return TermuxFloatAppSharedPreferences.build(context, true);
    }
}

class DebuggingPreferencesDataStore extends BasePackageDebuggingPreferenceDataStore<TermuxFloatAppSharedPreferences> {

    DebuggingPreferencesDataStore(@NonNull Context context,
                                  @Nullable TermuxFloatAppSharedPreferences preferences) {
        super(context, preferences);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        TermuxFloatAppSharedPreferences preferences = getPreferences();
        if (preferences == null) return;
        if (key == null) return;

        switch (key) {
            case "terminal_view_key_logging_enabled":
                preferences.setTerminalViewKeyLoggingEnabled(value, true);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        TermuxFloatAppSharedPreferences preferences = getPreferences();
        if (preferences == null) return false;
        switch (key) {
            case "terminal_view_key_logging_enabled":
                return preferences.isTerminalViewKeyLoggingEnabled(true);
            default:
                return false;
        }
    }
}
