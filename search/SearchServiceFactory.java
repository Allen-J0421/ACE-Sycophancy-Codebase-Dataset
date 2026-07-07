package search;

public class SearchServiceFactory {
    public static <T extends Comparable<T>> SearchService<T> createBinarySearch() {
        return new SearchService<>(new BinarySearchStrategy<T>());
    }
}
