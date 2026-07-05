package sorting.core;

import java.util.Comparator;

@FunctionalInterface
public interface SortStrategyFactory<T> {
    SortStrategy<T> create(Comparator<T> comparator);
}
