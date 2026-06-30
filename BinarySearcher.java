import java.util.Comparator;

final class BinarySearcher<T> implements SearchAlgorithm<T> {
    private final Comparator<? super T> comparator;

    BinarySearcher(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }

        this.comparator = comparator;
    }

    static <T extends Comparable<? super T>> BinarySearcher<T> naturalOrder() {
        return new BinarySearcher<T>(Comparator.naturalOrder());
    }

    @Override
    public SearchResult search(T[] sortedArray, T target) {
        validateInput(sortedArray, target);

        int low = 0;
        int high = sortedArray.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = comparator.compare(sortedArray[mid], target);

            if (comparison == 0) {
                return SearchResult.foundAt(mid);
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return SearchResult.notFound();
    }

    private static <T> void validateInput(T[] sortedArray, T target) {
        if (sortedArray == null) {
            throw new IllegalArgumentException("sortedArray must not be null");
        }

        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
    }
}
