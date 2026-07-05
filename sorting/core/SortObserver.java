package sorting.core;

@FunctionalInterface
public interface SortObserver {
    default void onSortStart() {}
    void onSortComplete(long durationNs, long comparisons);
}
