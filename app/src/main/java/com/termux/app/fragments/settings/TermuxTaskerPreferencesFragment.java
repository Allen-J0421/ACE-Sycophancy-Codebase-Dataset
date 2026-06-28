package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;

import com.termux.R;

@Keep
public class TermuxTaskerPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_tasker_preferences;
    }

}
