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

import java.util.Collections;

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

    private final BooleanPreferenceStore<TermuxAppSharedPreferences> mBooleanPreferences;

    TerminalViewPreferencesDataStore(@NonNull Context context) {
        super(context);
        mBooleanPreferences = new BooleanPreferenceStore<>(TermuxAppSharedPreferences.build(context, true),
            Collections.singletonList(BooleanPreferenceStore.binding("terminal_margin_adjustment",
                TermuxAppSharedPreferences::isTerminalMarginAdjustmentEnabled,
                TermuxAppSharedPreferences::setTerminalMarginAdjustment)));
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
