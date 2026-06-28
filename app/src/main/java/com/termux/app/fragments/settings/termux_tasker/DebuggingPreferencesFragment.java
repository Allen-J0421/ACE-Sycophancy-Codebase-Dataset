package com.termux.app.fragments.settings.termux_tasker;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.termux.R;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferencesFragment;
import com.termux.shared.termux.settings.preferences.TermuxTaskerAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BasePackageDebuggingPreferencesFragment<TermuxTaskerAppSharedPreferences> {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_tasker_debugging_preferences;
    }

    @Override
    protected TermuxTaskerAppSharedPreferences buildPreferences(@NonNull Context context) {
        return TermuxTaskerAppSharedPreferences.build(context, true);
    }

}
