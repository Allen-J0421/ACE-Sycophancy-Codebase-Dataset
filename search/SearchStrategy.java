package search;

import java.util.Optional;

public interface SearchStrategy<T extends Comparable<T>> {
    Optional<Integer> search(T[] arr, T target);

    default void validate(T[] arr, T target) {
        if (arr == null)
            throw new IllegalArgumentException("Array must not be null");
        if (arr.length == 0)
            throw new IllegalArgumentException("Array must not be empty");
        if (target == null)
            throw new IllegalArgumentException("Target must not be null");
    }
}
