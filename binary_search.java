import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;

interface SearchService<T> {
    SearchResult search(List<T> sortedValues, T target);
}

record SearchResult(boolean found, int index) {
    private static final int NOT_FOUND_INDEX = -1;

    SearchResult {
        if (found && index < 0) {
            throw new IllegalArgumentException("index must not be negative");
        }

        if (!found) {
            index = NOT_FOUND_INDEX;
        }
    }

    static SearchResult found(int index) {
        return new SearchResult(true, index);
    }

    static SearchResult notFound() {
        return new SearchResult(false, NOT_FOUND_INDEX);
    }

    @Override
    public int index() {
        if (!found) {
            throw new IllegalStateException("No index is available for a missing result");
        }

        return index;
    }

    <T> T map(IntFunction<? extends T> mapper, T defaultValue) {
        Objects.requireNonNull(mapper, "mapper must not be null");
        return found ? mapper.apply(index) : defaultValue;
    }

    int orElse(int defaultIndex) {
        return found ? index : defaultIndex;
    }
}

final class BinarySearchService<T> implements SearchService<T> {
    private final Comparator<? super T> comparator;

    BinarySearchService(Comparator<? super T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator must not be null");
    }

    static <T extends Comparable<? super T>> BinarySearchService<T> naturalOrder() {
        return new BinarySearchService<T>(Comparator.naturalOrder());
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

class BinarySearchDemo {
    public static void main(String[] args) {
        List<Integer> values = Arrays.asList(2, 3, 4, 10, 40);
        int target = 10;

        SearchService<Integer> searchService = BinarySearchService.naturalOrder();
        SearchResult result = searchService.search(values, target);
        String message = result.map(
                index -> "Element is present at index " + index,
                "Element is not present in array");

        System.out.println(message);
    }
}
