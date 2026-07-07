import java.util.Optional;

class BinarySearch {
    static Optional<Integer> binarySearch(int arr[], int x) {
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (arr[mid] == x)
                return Optional.of(mid);

            if (arr[mid] < x)
                low = mid + 1;

            else
                high = mid - 1;
        }

        return Optional.empty();
    }

    public static void main(String args[]) {
        int arr[] = { 2, 3, 4, 10, 40 };
        int x = 10;
        Optional<Integer> result = binarySearch(arr, x);
        result.ifPresentOrElse(
            index -> System.out.println("Element is present at index " + index),
            () -> System.out.println("Element is not present in array")
        );
    }
}
