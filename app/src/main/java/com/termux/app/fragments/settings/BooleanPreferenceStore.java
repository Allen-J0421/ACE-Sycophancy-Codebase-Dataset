package com.termux.app.fragments.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public final class BooleanPreferenceStore<T> {

    @FunctionalInterface
    public interface Getter<T> {
        boolean get(@NonNull T preferences);
    }

    @FunctionalInterface
    public interface Setter<T> {
        void set(@NonNull T preferences, boolean value);
    }

    public static final class Binding<T> {
        @NonNull
        private final String mKey;
        @NonNull
        private final Getter<T> mGetter;
        @NonNull
        private final Setter<T> mSetter;

        private Binding(@NonNull String key, @NonNull Getter<T> getter, @NonNull Setter<T> setter) {
            mKey = key;
            mGetter = getter;
            mSetter = setter;
        }
    }

    @Nullable
    private final T mPreferences;
    @NonNull
    private final List<Binding<T>> mBindings;

    public BooleanPreferenceStore(@Nullable T preferences, @NonNull List<Binding<T>> bindings) {
        mPreferences = preferences;
        mBindings = bindings;
    }

    @NonNull
    public static <T> Binding<T> binding(@NonNull String key, @NonNull Getter<T> getter,
                                         @NonNull Setter<T> setter) {
        return new Binding<>(key, getter, setter);
    }

    public void putBoolean(@Nullable String key, boolean value) {
        if (mPreferences == null || key == null) return;

        Binding<T> binding = getBinding(key);
        if (binding == null) return;

        binding.mSetter.set(mPreferences, value);
    }

    public boolean getBoolean(@Nullable String key) {
        if (mPreferences == null || key == null) return false;

        Binding<T> binding = getBinding(key);
        if (binding == null) return false;

        return binding.mGetter.get(mPreferences);
    }

    @Nullable
    private Binding<T> getBinding(@NonNull String key) {
        for (Binding<T> binding : mBindings) {
            if (binding.mKey.equals(key)) return binding;
        }

        return null;
    }

}
