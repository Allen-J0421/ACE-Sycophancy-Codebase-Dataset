package search;

import java.util.Optional;

public abstract class AbstractSearchStrategy<T extends Comparable<T>> {
    public final Optional<Integer> search(T[] arr, T target) {
        validate(arr, target);
        validateSorted(arr);
        return doSearch(arr, target);
    }

    protected abstract Optional<Integer> doSearch(T[] arr, T target);

    protected void validateSorted(T[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i].compareTo(arr[i + 1]) > 0)
                throw new IllegalArgumentException(
                    "Array must be sorted in ascending order (violation at index " + i + ")");
        }
    }

    protected void validate(T[] arr, T target) {
        if (arr == null)
            throw new IllegalArgumentException("Array must not be null");
        if (arr.length == 0)
            throw new IllegalArgumentException("Array must not be empty");
        if (target == null)
            throw new IllegalArgumentException("Target must not be null");
    }
}
