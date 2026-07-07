import java.util.Optional;

interface SearchStrategy<T extends Comparable<T>> {
    Optional<Integer> search(T[] arr, T target);
}

class BinarySearchStrategy<T extends Comparable<T>> implements SearchStrategy<T> {
    @Override
    public Optional<Integer> search(T[] arr, T target) {
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

class SearchService<T extends Comparable<T>> {
    private final SearchStrategy<T> strategy;

    SearchService(SearchStrategy<T> strategy) {
        this.strategy = strategy;
    }

    Optional<Integer> execute(T[] arr, T target) {
        return strategy.search(arr, target);
    }
}

class BinarySearch {
    public static void main(String[] args) {
        SearchService<Integer> service = new SearchService<>(new BinarySearchStrategy<Integer>());
        Integer[] arr = { 2, 3, 4, 10, 40 };
        Integer target = 10;

        service.execute(arr, target).ifPresentOrElse(
            index -> System.out.println("Element is present at index " + index),
            () -> System.out.println("Element is not present in array")
        );
    }
}
