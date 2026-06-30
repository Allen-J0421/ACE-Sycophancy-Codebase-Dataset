import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Supplier;

interface SearchService<T> {
    SearchResult search(List<T> sortedValues, T target);
}

sealed interface SearchResult permits Found, NotFound {
    <T> T map(IntFunction<? extends T> foundMapper, Supplier<? extends T> notFoundSupplier);

    int orElse(int defaultIndex);

    static SearchResult found(int index) {
        return new Found(index);
    }

    static SearchResult notFound() {
        return new NotFound();
    }
}

record Found(int index) implements SearchResult {
    Found {
        if (index < 0) {
            throw new IllegalArgumentException("index must not be negative");
        }
    }

    @Override
    public <T> T map(IntFunction<? extends T> foundMapper, Supplier<? extends T> notFoundSupplier) {
        Objects.requireNonNull(foundMapper, "foundMapper must not be null");
        return foundMapper.apply(index);
    }

    @Override
    public int orElse(int defaultIndex) {
        return index;
    }
}

record NotFound() implements SearchResult {
    @Override
    public <T> T map(IntFunction<? extends T> foundMapper, Supplier<? extends T> notFoundSupplier) {
        Objects.requireNonNull(notFoundSupplier, "notFoundSupplier must not be null");
        return notFoundSupplier.get();
    }

    @Override
    public int orElse(int defaultIndex) {
        return defaultIndex;
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

        if (result instanceof Found found) {
            System.out.println("Element is present at index " + found.index());
        } else {
            System.out.println("Element is not present in array");
        }
    }
}
