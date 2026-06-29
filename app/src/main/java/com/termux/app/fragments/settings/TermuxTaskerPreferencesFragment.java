package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.shared.termux.settings.preferences.TermuxTaskerAppSharedPreferences;

@Keep
public class TermuxTaskerPreferencesFragment extends BasePreferencesFragment {

    @Override
    protected int getPreferencesXmlResource() {
        return R.xml.termux_tasker_preferences;
    }

    @Override
    protected PreferenceDataStore createDataStore(Context context) {
        return TermuxTaskerPreferencesDataStore.getInstance(context);
    }

}

class TermuxTaskerPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TermuxTaskerAppSharedPreferences mPreferences;

    private static TermuxTaskerPreferencesDataStore mInstance;

    private TermuxTaskerPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TermuxTaskerAppSharedPreferences.build(context, true);
    }

    public static synchronized TermuxTaskerPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TermuxTaskerPreferencesDataStore(context);
        }
        return mInstance;
    }

}
