import java.util.Objects;

final class BinarySearch {
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        return indexOf(sortedValues, target);
    }

    static int indexOf(int[] sortedValues, int target) {
        Objects.requireNonNull(sortedValues, "sortedValues must not be null");

        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (sortedValues[mid] == target) {
                return mid;
            }

            if (sortedValues[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int[] values = {2, 3, 4, 10, 40};
        int target = 10;
        int result = binarySearch(values, target);

        System.out.println(formatSearchResult(result));
    }

    static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }
}

final class BinarySearchTest {
    private static final int NOT_FOUND = -1;

    private BinarySearchTest() {
    }

    public static void main(String[] args) {
        assertIndex(0, new int[] {1}, 1);
        assertIndex(NOT_FOUND, new int[] {1}, 2);
        assertIndex(0, new int[] {1, 3, 5, 7}, 1);
        assertIndex(2, new int[] {1, 3, 5, 7}, 5);
        assertIndex(3, new int[] {1, 3, 5, 7}, 7);
        assertIndex(NOT_FOUND, new int[] {}, 1);
        assertMessage("Element is present at index 2", 2);
        assertMessage("Element is not present in array", NOT_FOUND);
    }

    private static void assertIndex(int expected, int[] values, int target) {
        int actual = BinarySearch.binarySearch(values, target);
        if (actual != expected) {
            throw new AssertionError("expected index " + expected + " but got " + actual);
        }
    }

    private static void assertMessage(String expected, int result) {
        String actual = BinarySearch.formatSearchResult(result);
        if (!expected.equals(actual)) {
            throw new AssertionError("expected message \"" + expected + "\" but got \"" + actual + "\"");
        }
    }
}
