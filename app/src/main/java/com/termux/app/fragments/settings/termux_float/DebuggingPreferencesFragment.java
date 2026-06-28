package com.termux.app.fragments.settings.termux_float;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BooleanPreferenceStore;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferenceDataStore;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferencesFragment;
import com.termux.shared.termux.settings.preferences.TermuxFloatAppSharedPreferences;

import java.util.Collections;

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

    private final BooleanPreferenceStore<TermuxFloatAppSharedPreferences> mBooleanPreferences;

    DebuggingPreferencesDataStore(@NonNull Context context,
                                  @Nullable TermuxFloatAppSharedPreferences preferences) {
        super(context, preferences);
        mBooleanPreferences = new BooleanPreferenceStore<>(preferences,
            Collections.singletonList(BooleanPreferenceStore.binding("terminal_view_key_logging_enabled",
                prefs -> prefs.isTerminalViewKeyLoggingEnabled(true),
                (prefs, value) -> prefs.setTerminalViewKeyLoggingEnabled(value, true))));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        mBooleanPreferences.putBoolean(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mBooleanPreferences.getBoolean(key);
    }
}
