package com.termux.app.fragments.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import com.termux.shared.logger.Logger;
import com.termux.shared.termux.settings.preferences.TermuxAppLogLevelPreferences;

public abstract class BasePackageDebuggingPreferencesFragment<T extends TermuxAppLogLevelPreferences>
    extends BaseDebuggingPreferencesFragment {

    @Override
    protected final PreferenceDataStore getPreferenceDataStore(@NonNull Context context) {
        T preferences = buildPreferences(context);
        return createPreferenceDataStore(context, preferences);
    }

    @Override
    protected final int getLogLevel(@NonNull Context context) {
        T preferences = buildPreferences(context);
        return preferences != null ? preferences.getLogLevel(shouldReadLogLevelFromFile())
            : Logger.DEFAULT_LOG_LEVEL;
    }

    @NonNull
    protected PreferenceDataStore createPreferenceDataStore(@NonNull Context context,
                                                            @Nullable T preferences) {
        return new BasePackageDebuggingPreferenceDataStore<>(context, preferences);
    }

    protected boolean shouldReadLogLevelFromFile() {
        return true;
    }

    @Nullable
    protected abstract T buildPreferences(@NonNull Context context);

}
