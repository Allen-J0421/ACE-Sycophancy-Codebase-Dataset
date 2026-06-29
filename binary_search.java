final class BinarySearch {
    private static final int[] SORTED_SAMPLE_NUMBERS = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;
    private static final int NOT_FOUND = -1;

    private BinarySearch() {
    }

    /**
     * Returns the index of {@code target} in sorted {@code numbers}, or {@code -1} when absent.
     */
    static int indexOf(int[] numbers, int target) {
        if (numbers == null) {
            throw new IllegalArgumentException("numbers must not be null");
        }

        int low = 0;
        int high = numbers.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int value = numbers[mid];

            if (value == target) {
                return mid;
            }

            if (value < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    public static void main(String[] args) {
        runSelfTest();
        runDemo();
    }

    private static void runDemo() {
        int result = indexOf(SORTED_SAMPLE_NUMBERS, SAMPLE_TARGET);
        System.out.println(formatSearchResult(result));
    }

    private static void runSelfTest() {
        assertIndexOf(SORTED_SAMPLE_NUMBERS, 2, 0);
        assertIndexOf(SORTED_SAMPLE_NUMBERS, 10, 3);
        assertIndexOf(SORTED_SAMPLE_NUMBERS, 41, NOT_FOUND);
        assertIndexOf(new int[0], 10, NOT_FOUND);
        assertRejectsNullInput();
    }

    private static void assertIndexOf(int[] numbers, int target, int expectedIndex) {
        int actualIndex = indexOf(numbers, target);

        if (actualIndex != expectedIndex) {
            throw new AssertionError("Expected index " + expectedIndex + " but found " + actualIndex);
        }
    }

    private static void assertRejectsNullInput() {
        try {
            indexOf(null, SAMPLE_TARGET);
        } catch (IllegalArgumentException exception) {
            return;
        }

        throw new AssertionError("Expected null input to be rejected");
    }
}
