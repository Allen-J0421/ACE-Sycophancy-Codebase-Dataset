import search.BinarySearchStrategy;
import search.SearchService;

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
