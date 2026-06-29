package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.shared.termux.settings.preferences.TermuxAPIAppSharedPreferences;

@Keep
public class TermuxAPIPreferencesFragment extends BasePreferencesFragment {

    @Override
    protected int getPreferencesXmlResource() {
        return R.xml.termux_api_preferences;
    }

    @Override
    protected PreferenceDataStore createDataStore(Context context) {
        return TermuxAPIPreferencesDataStore.getInstance(context);
    }

}

class TermuxAPIPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final TermuxAPIAppSharedPreferences mPreferences;

    private static TermuxAPIPreferencesDataStore mInstance;

    private TermuxAPIPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = TermuxAPIAppSharedPreferences.build(context, true);
    }

    public static synchronized TermuxAPIPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TermuxAPIPreferencesDataStore(context);
        }
        return mInstance;
    }

}
