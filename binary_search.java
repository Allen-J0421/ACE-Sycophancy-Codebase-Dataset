import java.util.Optional;

interface SearchStrategy {
    Optional<Integer> search(int[] arr, int target);
}

class BinarySearchStrategy implements SearchStrategy {
    @Override
    public Optional<Integer> search(int[] arr, int target) {
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
        SearchStrategy strategy = new BinarySearchStrategy();
        int[] arr = { 2, 3, 4, 10, 40 };
        int target = 10;

        strategy.search(arr, target).ifPresentOrElse(
            index -> System.out.println("Element is present at index " + index),
            () -> System.out.println("Element is not present in array")
        );
    }
}
