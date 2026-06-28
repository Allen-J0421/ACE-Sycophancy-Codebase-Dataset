package com.termux.app.fragments.settings.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDataStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class MappedPreferenceDataStore extends PreferenceDataStore {

    private final Map<String, PreferenceBinding> mBindings;

    private MappedPreferenceDataStore(@NonNull Map<String, PreferenceBinding> bindings) {
        mBindings = Collections.unmodifiableMap(bindings);
    }

    public static final class Builder {

        private final Map<String, PreferenceBinding> mBindings = new HashMap<>();

        public Builder putString(@NonNull String key, @Nullable Supplier<String> getter, @Nullable Consumer<String> setter) {
            mBindings.put(key, PreferenceBinding.forString(getter, setter));
            return this;
        }

        public Builder putBoolean(@NonNull String key, @Nullable Supplier<Boolean> getter, @Nullable Consumer<Boolean> setter) {
            mBindings.put(key, PreferenceBinding.forBoolean(getter, setter));
            return this;
        }

        public MappedPreferenceDataStore build() {
            return new MappedPreferenceDataStore(new HashMap<>(mBindings));
        }
    }

    private static final class PreferenceBinding {

        @Nullable
        private final Supplier<String> mStringGetter;
        @Nullable
        private final Consumer<String> mStringSetter;
        @Nullable
        private final Supplier<Boolean> mBooleanGetter;
        @Nullable
        private final Consumer<Boolean> mBooleanSetter;

        private PreferenceBinding(@Nullable Supplier<String> stringGetter, @Nullable Consumer<String> stringSetter,
                                  @Nullable Supplier<Boolean> booleanGetter, @Nullable Consumer<Boolean> booleanSetter) {
            mStringGetter = stringGetter;
            mStringSetter = stringSetter;
            mBooleanGetter = booleanGetter;
            mBooleanSetter = booleanSetter;
        }

        private static PreferenceBinding forString(@Nullable Supplier<String> getter, @Nullable Consumer<String> setter) {
            return new PreferenceBinding(getter, setter, null, null);
        }

        private static PreferenceBinding forBoolean(@Nullable Supplier<Boolean> getter, @Nullable Consumer<Boolean> setter) {
            return new PreferenceBinding(null, null, getter, setter);
        }
    }

    @Override
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        PreferenceBinding binding = mBindings.get(key);
        if (binding == null || binding.mStringGetter == null) return defValue;

        String value = binding.mStringGetter.get();
        return value == null ? defValue : value;
    }

    @Override
    public void putString(String key, @Nullable String value) {
        PreferenceBinding binding = mBindings.get(key);
        if (binding == null || binding.mStringSetter == null || value == null) return;

        binding.mStringSetter.accept(value);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        PreferenceBinding binding = mBindings.get(key);
        if (binding == null || binding.mBooleanGetter == null) return defValue;

        Boolean value = binding.mBooleanGetter.get();
        return value == null ? defValue : value;
    }

    @Override
    public void putBoolean(String key, boolean value) {
        PreferenceBinding binding = mBindings.get(key);
        if (binding == null || binding.mBooleanSetter == null) return;

        binding.mBooleanSetter.accept(value);
    }
}
