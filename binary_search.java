import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import search.SearchResult;
import search.SearchService;

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
                () -> "Element is not present in array");

        System.out.println(message);
    }
}
