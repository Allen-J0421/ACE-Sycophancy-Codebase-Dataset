package com.termux.app.fragments.settings.termux_api;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.termux.R;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferencesFragment;
import com.termux.shared.termux.settings.preferences.TermuxAPIAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BasePackageDebuggingPreferencesFragment<TermuxAPIAppSharedPreferences> {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_api_debugging_preferences;
    }

    @Override
    protected TermuxAPIAppSharedPreferences buildPreferences(@NonNull Context context) {
        return TermuxAPIAppSharedPreferences.build(context, true);
    }

}
