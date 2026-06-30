package search;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class BinarySearchAlgorithm<T> implements SearchAlgorithm<T> {
    private final Comparator<? super T> comparator;

    public BinarySearchAlgorithm(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator must not be null");
    }

    public static <T extends Comparable<? super T>> BinarySearchAlgorithm<T> naturalOrder() {
        return new BinarySearchAlgorithm<T>(Comparator.naturalOrder());
    }

    @Override
    public SearchResult search(List<T> sortedValues, T target) {
        Objects.requireNonNull(sortedValues, "sortedValues must not be null");

        int low = 0;
        int high = sortedValues.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = comparator.compare(sortedValues.get(mid), target);

            if (comparison == 0) {
                return SearchResult.found(mid);
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound();
    }
}
