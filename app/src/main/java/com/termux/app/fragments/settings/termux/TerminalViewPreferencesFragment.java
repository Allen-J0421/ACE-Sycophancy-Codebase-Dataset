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
public class TerminalViewPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_terminal_view_preferences;
    }

    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return new TerminalViewPreferencesDataStore(context);
    }

}

class TerminalViewPreferencesDataStore extends BasePreferenceDataStore {

    private final TermuxAppSharedPreferences mPreferences;

    TerminalViewPreferencesDataStore(@NonNull Context context) {
        super(context);
        mPreferences = TermuxAppSharedPreferences.build(context, true);
    }



    @Override
    public void putBoolean(String key, boolean value) {
        if (mPreferences == null) return;
        if (key == null) return;

        switch (key) {
            case "terminal_margin_adjustment":
                    mPreferences.setTerminalMarginAdjustment(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (mPreferences == null) return false;

        switch (key) {
            case "terminal_margin_adjustment":
                return mPreferences.isTerminalMarginAdjustmentEnabled();
            default:
                return false;
        }
    }

}
