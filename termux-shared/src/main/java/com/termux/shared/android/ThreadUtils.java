package com.termux.shared.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class ThreadUtils {

    private ThreadUtils() {}

    @NonNull
    public static Thread runAsync(@NonNull Runnable runnable) {
        return runAsync(null, runnable);
    }

    @NonNull
    public static Thread runAsync(@Nullable String threadName, @NonNull Runnable runnable) {
        Thread thread = threadName == null ? new Thread(runnable) : new Thread(runnable, threadName);
        thread.start();
        return thread;
    }

}
