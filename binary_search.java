import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

interface SearchService<T> {
    SearchResult search(List<T> sortedValues, T target);
}

final class SearchResult {
    private static final int NOT_FOUND_INDEX = -1;

    private final boolean found;
    private final int index;

    private SearchResult(boolean found, int index) {
        this.found = found;
        this.index = index;
    }

    static SearchResult found(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must not be negative");
        }

        return new SearchResult(true, index);
    }

    static SearchResult notFound() {
        return new SearchResult(false, NOT_FOUND_INDEX);
    }

    boolean found() {
        return found;
    }

    int index() {
        if (!found) {
            throw new IllegalStateException("No index is available for a missing result");
        }

        return index;
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

        if (result.found()) {
            System.out.println("Element is present at index " + result.index());
        } else {
            System.out.println("Element is not present in array");
        }
    }
}
