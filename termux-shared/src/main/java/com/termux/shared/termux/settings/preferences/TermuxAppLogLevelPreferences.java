package com.termux.shared.termux.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;

public interface TermuxAppLogLevelPreferences {

    int getLogLevel(boolean readFromFile);

    void setLogLevel(@NonNull Context context, int logLevel, boolean commitToFile);

}
