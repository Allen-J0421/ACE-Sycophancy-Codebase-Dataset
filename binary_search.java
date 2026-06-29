import java.util.Objects;

final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";

    private BinarySearch() {
    }

    static int binarySearch(int[] numbers, int target) {
        return indexOf(numbers, target);
    }

    static int indexOf(int[] numbers, int target) {
        Objects.requireNonNull(numbers, "numbers");

        int low = 0;
        int high = numbers.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);

            if (numbers[mid] == target) {
                return mid;
            }

            if (numbers[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        final int[] numbers = {2, 3, 4, 10, 40};
        final int target = 10;
        final int result = binarySearch(numbers, target);

        System.out.println(formatSearchResult(result));
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
