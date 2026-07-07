package search;

import java.util.Optional;

public class BinarySearchStrategy<T extends Comparable<T>> implements SearchStrategy<T> {
    @Override
    public Optional<Integer> search(T[] arr, T target) {
        validate(arr, target);

        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = arr[mid].compareTo(target);

            if (cmp == 0)
                return Optional.of(mid);

            if (cmp < 0)
                low = mid + 1;
            else
                high = mid - 1;
        }

        return Optional.empty();
    }
}
