package com.termux.app.fragments.settings.base;

import androidx.preference.PreferenceDataStore;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MappedPreferenceDataStoreTest {

    @Test
    public void returnsMappedValuesAndFallsBackToDefaults() {
        PreferenceDataStore dataStore = new MappedPreferenceDataStore.Builder()
            .putString("log_level", () -> "3", null)
            .putBoolean("terminal_view_key_logging_enabled", () -> true, null)
            .build();

        Assert.assertEquals("3", dataStore.getString("log_level", "1"));
        Assert.assertTrue(dataStore.getBoolean("terminal_view_key_logging_enabled", false));
        Assert.assertEquals("fallback", dataStore.getString("missing", "fallback"));
        Assert.assertFalse(dataStore.getBoolean("missing", false));
    }

    @Test
    public void writesMappedValues() {
        AtomicReference<String> stringValue = new AtomicReference<>();
        AtomicBoolean booleanValue = new AtomicBoolean(false);

        PreferenceDataStore dataStore = new MappedPreferenceDataStore.Builder()
            .putString("log_level", () -> "1", stringValue::set)
            .putBoolean("terminal_view_key_logging_enabled", () -> false, booleanValue::set)
            .build();

        dataStore.putString("log_level", "2");
        dataStore.putBoolean("terminal_view_key_logging_enabled", true);

        Assert.assertEquals("2", stringValue.get());
        Assert.assertTrue(booleanValue.get());
    }
}
