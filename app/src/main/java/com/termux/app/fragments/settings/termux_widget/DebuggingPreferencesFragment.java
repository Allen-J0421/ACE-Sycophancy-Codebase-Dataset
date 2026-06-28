package com.termux.app.fragments.settings.termux_widget;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.termux.R;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferencesFragment;
import com.termux.shared.termux.settings.preferences.TermuxWidgetAppSharedPreferences;

@Keep
public class DebuggingPreferencesFragment extends BasePackageDebuggingPreferencesFragment<TermuxWidgetAppSharedPreferences> {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_widget_debugging_preferences;
    }

    @Override
    protected TermuxWidgetAppSharedPreferences buildPreferences(@NonNull Context context) {
        return TermuxWidgetAppSharedPreferences.build(context, true);
    }

}
