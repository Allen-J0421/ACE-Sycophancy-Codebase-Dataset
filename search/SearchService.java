package search;

import java.util.Optional;

public class SearchService<T extends Comparable<T>> {
    private final AbstractSearchStrategy<T> strategy;

    public SearchService(AbstractSearchStrategy<T> strategy) {
        this.strategy = strategy;
    }

    public Optional<Integer> execute(T[] arr, T target) {
        return strategy.search(arr, target);
    }
}
