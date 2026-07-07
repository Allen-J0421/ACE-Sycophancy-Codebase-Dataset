package search;

import java.util.Optional;

public interface SearchStrategy<T extends Comparable<T>> {
    Optional<Integer> search(T[] arr, T target);
}
