import java.util.Optional;

class BinarySearchService {
    Optional<Integer> search(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (arr[mid] == target)
                return Optional.of(mid);

            if (arr[mid] < target)
                low = mid + 1;
            else
                high = mid - 1;
        }

        return Optional.empty();
    }
}

class BinarySearch {
    public static void main(String[] args) {
        BinarySearchService service = new BinarySearchService();
        int[] arr = { 2, 3, 4, 10, 40 };
        int target = 10;

        service.search(arr, target).ifPresentOrElse(
            index -> System.out.println("Element is present at index " + index),
            () -> System.out.println("Element is not present in array")
        );
    }
}
