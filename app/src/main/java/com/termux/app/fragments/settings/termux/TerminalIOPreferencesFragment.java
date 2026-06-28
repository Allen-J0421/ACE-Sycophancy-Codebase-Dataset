package com.termux.app.fragments.settings.termux;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BooleanPreferenceStore;
import com.termux.app.fragments.settings.BasePreferenceDataStore;
import com.termux.app.fragments.settings.BasePreferenceFragment;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

import java.util.Arrays;

@Keep
public class TerminalIOPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_terminal_io_preferences;
    }

    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return new TerminalIOPreferencesDataStore(context);
    }

}

class TerminalIOPreferencesDataStore extends BasePreferenceDataStore {

    private final BooleanPreferenceStore<TermuxAppSharedPreferences> mBooleanPreferences;

    TerminalIOPreferencesDataStore(@NonNull Context context) {
        super(context);
        mBooleanPreferences = new BooleanPreferenceStore<>(TermuxAppSharedPreferences.build(context, true),
            Arrays.asList(
                BooleanPreferenceStore.binding("soft_keyboard_enabled",
                    TermuxAppSharedPreferences::isSoftKeyboardEnabled,
                    TermuxAppSharedPreferences::setSoftKeyboardEnabled),
                BooleanPreferenceStore.binding("soft_keyboard_enabled_only_if_no_hardware",
                    TermuxAppSharedPreferences::isSoftKeyboardEnabledOnlyIfNoHardware,
                    TermuxAppSharedPreferences::setSoftKeyboardEnabledOnlyIfNoHardware)
            ));
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
