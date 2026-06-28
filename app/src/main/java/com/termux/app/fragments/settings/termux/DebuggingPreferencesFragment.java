package com.termux.app.fragments.settings.termux;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import com.termux.R;
import com.termux.app.fragments.settings.BooleanPreferenceStore;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferenceDataStore;
import com.termux.app.fragments.settings.BasePackageDebuggingPreferencesFragment;
import com.termux.shared.termux.settings.preferences.TermuxAppSharedPreferences;

import java.util.Arrays;

@Keep
public class DebuggingPreferencesFragment extends BasePackageDebuggingPreferencesFragment<TermuxAppSharedPreferences> {

    @Override
    protected int getPreferencesResource() {
        return R.xml.termux_debugging_preferences;
    }

    @Override
    protected PreferenceDataStore createPreferenceDataStore(@NonNull Context context,
                                                            @Nullable TermuxAppSharedPreferences preferences) {
        return new DebuggingPreferencesDataStore(context, preferences);
    }

    @Override
    protected TermuxAppSharedPreferences buildPreferences(@NonNull Context context) {
        return TermuxAppSharedPreferences.build(context, true);
    }

    @Override
    protected boolean shouldReadLogLevelFromFile() {
        return false;
    }

}

class DebuggingPreferencesDataStore extends BasePackageDebuggingPreferenceDataStore<TermuxAppSharedPreferences> {

    private final BooleanPreferenceStore<TermuxAppSharedPreferences> mBooleanPreferences;

    DebuggingPreferencesDataStore(@NonNull Context context,
                                  @Nullable TermuxAppSharedPreferences preferences) {
        super(context, preferences);
        mBooleanPreferences = new BooleanPreferenceStore<>(preferences,
            Arrays.asList(
                BooleanPreferenceStore.binding("terminal_view_key_logging_enabled",
                    TermuxAppSharedPreferences::isTerminalViewKeyLoggingEnabled,
                    TermuxAppSharedPreferences::setTerminalViewKeyLoggingEnabled),
                BooleanPreferenceStore.binding("plugin_error_notifications_enabled",
                    prefs -> prefs.arePluginErrorNotificationsEnabled(false),
                    TermuxAppSharedPreferences::setPluginErrorNotificationsEnabled),
                BooleanPreferenceStore.binding("crash_report_notifications_enabled",
                    prefs -> prefs.areCrashReportNotificationsEnabled(false),
                    TermuxAppSharedPreferences::setCrashReportNotificationsEnabled)
            ));
    }

    @Override
    protected boolean shouldReadLogLevelFromFile() {
        return false;
    }

    @Override
    protected boolean shouldCommitLogLevelToFile() {
        return false;
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
