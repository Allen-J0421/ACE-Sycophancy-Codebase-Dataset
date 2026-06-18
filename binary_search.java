import java.util.OptionalInt;

final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] arr, int target) {
        return findIndex(arr, target).orElse(NOT_FOUND);
    }

    private static OptionalInt findIndex(int[] arr, int target) {
        if (arr.length == 0) {
            return OptionalInt.empty();
        }

        return findIndex(arr, target, 0, arr.length - 1);
    }

    private static OptionalInt findIndex(int[] arr, int target, int low, int high) {

        while (low <= high) {
            int middleIndex = low + (high - low) / 2;
            int middleValue = arr[middleIndex];

            if (middleValue == target) {
                return OptionalInt.of(middleIndex);
            }

            if (middleValue < target) {
                low = middleIndex + 1;
            } else {
                high = middleIndex - 1;
            }
        }

        return OptionalInt.empty();
    }

    private static String formatSearchResult(int result) {
        return result == NOT_FOUND
                ? "Element is not present in array"
                : "Element is present at index " + result;
    }

    public static void main(String[] args) {
        int[] arr = { 2, 3, 4, 10, 40 };
        int target = 10;
        int result = binarySearch(arr, target);

        System.out.println(formatSearchResult(result));
    }
}
