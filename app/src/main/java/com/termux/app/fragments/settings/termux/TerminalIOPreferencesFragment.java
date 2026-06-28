package com.termux.app.fragments.settings.termux;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BasePreferenceDataStore;
import com.termux.app.fragments.settings.BasePreferenceFragment;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

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

    private final TermuxAppSharedPreferences mPreferences;

    TerminalIOPreferencesDataStore(@NonNull Context context) {
        super(context);
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }



    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null) return;
        if (key == null) return;

        switch (key) {
            case "soft_keyboard_enabled":
                    mPreferences.setSoftKeyboardEnabled(value);
                break;
            case "soft_keyboard_enabled_only_if_no_hardware":
                mPreferences.setSoftKeyboardEnabledOnlyIfNoHardware(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null) return false;

        switch (key) {
            case "soft_keyboard_enabled":
                return mPreferences.isSoftKeyboardEnabled();
            case "soft_keyboard_enabled_only_if_no_hardware":
                return mPreferences.isSoftKeyboardEnabledOnlyIfNoHardware();
            default:
                return false;
        }
    }

}
