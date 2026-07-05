package sorting.core;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class SortTask<T> {
    private final T[] data;
    private final SortContext<T> context;

    public SortTask(T[] data, SortContext<T> context) {
        this.data = Arrays.copyOf(data, data.length);
        this.context = context;
    }

    // Returns a sorted copy; original data in this task is preserved for re-use.
    public T[] sort() {
        T[] copy = Arrays.copyOf(data, data.length);
        context.sort(copy);
        return copy;
    }

    public CompletableFuture<T[]> sortAsync() {
        return CompletableFuture.supplyAsync(this::sort);
    }

    public CompletableFuture<T[]> sortAsync(Executor executor) {
        return CompletableFuture.supplyAsync(this::sort, executor);
    }
}
