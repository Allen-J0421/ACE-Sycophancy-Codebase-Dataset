package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.shared.termux.settings.preferences.TermuxWidgetAppSharedPreferences;

@Keep
public class TermuxWidgetPreferencesFragment extends BasePreferenceFragment {

    @Override
    protected int getPreferencesResourceId() {
        return R.xml.termux_widget_preferences;
    }

    @NonNull
    @Override
    protected PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        return TermuxWidgetPreferencesDataStore.getInstance(context);
    }
}

class TermuxWidgetPreferencesDataStore extends InitializingPreferenceDataStore {

    private static TermuxWidgetPreferencesDataStore mInstance;

    private TermuxWidgetPreferencesDataStore(Context context) {
        super(context, appContext -> TermuxWidgetAppSharedPreferences.build(appContext, true));
    }

    public static synchronized TermuxWidgetPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TermuxWidgetPreferencesDataStore(context);
        }
        return mInstance;
    }

}
