package sorting.strategy;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
public interface SortingStrategy<T extends Comparable<T>> {
    void sort(T[] array, int left, int right, Comparator<T> comparator);

    static <T extends Comparable<T>> SortingStrategy<T> caching(SortingStrategy<T> strategy) {
        Map<String, Long> cache = new HashMap<>();

        return (array, left, right, comparator) -> {
            String key = left + ":" + right + ":" + array.length;
            long startTime = System.nanoTime();

            strategy.sort(array, left, right, comparator);

            long duration = System.nanoTime() - startTime;
            cache.put(key, duration);
        };
    }

    static <T extends Comparable<T>> SortingStrategy<T> measured(
            SortingStrategy<T> strategy,
            java.util.function.Consumer<Long> durationConsumer) {
        return (array, left, right, comparator) -> {
            long startTime = System.nanoTime();
            strategy.sort(array, left, right, comparator);
            long duration = System.nanoTime() - startTime;
            durationConsumer.accept(duration);
        };
    }
}
