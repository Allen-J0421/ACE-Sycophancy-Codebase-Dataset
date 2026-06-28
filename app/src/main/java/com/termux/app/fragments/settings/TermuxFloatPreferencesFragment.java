package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;

import com.termux.R;

@Keep
public class TermuxFloatPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_float_preferences;
    }

}
