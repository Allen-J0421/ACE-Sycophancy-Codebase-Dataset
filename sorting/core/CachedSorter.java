package sorting.core;

import java.util.Comparator;

public class CachedSorter<T extends Comparable<T>> {
    private final SortCache<T> cache;
    private final SortConfig<T> config;

    public CachedSorter(SortConfig<T> config, int cacheSize) {
        this.config = config;
        this.cache = new SortCache<>(cacheSize);
    }

    public void sort(T[] array) throws SortException {
        sort(array, config.comparator());
    }

    public void sort(T[] array, Comparator<T> comparator) throws SortException {
        if (array == null || array.length == 0) {
            return;
        }

        String cacheKey = generateCacheKey(array);

        if (cache.get(cacheKey, array)) {
            return;
        }

        T[] original = array.clone();

        ParallelMergeSort<T> sorter = new ParallelMergeSort<>(config);
        sorter.sort(array, comparator);
        sorter.shutdown();

        cache.put(cacheKey, array);
    }

    private String generateCacheKey(T[] array) {
        return "sort_" + array.length + "_" + System.identityHashCode(array);
    }

    public String getCacheStats() {
        return cache.stats();
    }

    public void clearCache() {
        cache.clear();
    }
}
