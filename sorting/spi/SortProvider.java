package sorting.spi;

import sorting.core.SortConfig;
import sorting.core.SortException;
import java.util.Comparator;
import java.util.ServiceLoader;

public interface SortProvider<T extends Comparable<T>> {
    String name();

    void sort(T[] array, Comparator<T> comparator) throws SortException;

    int priority();

    static <T extends Comparable<T>> SortProvider<T> loadFastest() {
        return ServiceLoader.load(SortProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .max((a, b) -> Integer.compare(a.priority(), b.priority()))
                .map(provider -> (SortProvider<T>) provider)
                .orElseThrow(() -> new IllegalStateException("No sort providers available"));
    }

    static <T extends Comparable<T>> java.util.List<SortProvider<T>> loadAll() {
        return ServiceLoader.load(SortProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .map(provider -> (SortProvider<T>) provider)
                .sorted((a, b) -> Integer.compare(b.priority(), a.priority()))
                .toList();
    }
}
