import java.util.Objects;

final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        Objects.requireNonNull(values, "values");

        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int value = values[mid];
            int comparison = Integer.compare(value, target);

            if (comparison == 0) {
                return mid;
            }

            if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static String formatSearchResult(int index) {
        if (!isFound(index)) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }

    private static boolean isFound(int index) {
        return index != NOT_FOUND;
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int result = binarySearch(demoValues(), DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static int[] demoValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }
}
