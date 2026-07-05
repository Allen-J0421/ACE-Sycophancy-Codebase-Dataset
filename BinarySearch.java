import java.util.Objects;

public final class BinarySearch {
    public static final int NOT_FOUND = -1;
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    /**
     * Returns the index of {@code target} in an ascending sorted array, or
     * {@link #NOT_FOUND} when the target is absent.
     */
    public static int binarySearch(int[] sortedNumbers, int target) {
        Objects.requireNonNull(sortedNumbers, "sortedNumbers must not be null");

        int left = 0;
        int right = sortedNumbers.length - 1;

        while (left <= right) {
            int middle = midpoint(left, right);
            int comparison = Integer.compare(sortedNumbers[middle], target);

            if (comparison == 0) {
                return middle;
            }

            if (comparison < 0) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int left, int right) {
        return left + (right - left) / 2;
    }

    private static void printSearchResult(int index) {
        System.out.println(formatSearchResult(index));
    }

    private static String formatSearchResult(int index) {
        return index == NOT_FOUND
                ? "Element is not present in array"
                : "Element is present at index " + index;
    }

    public static void main(String[] args) {
        runSampleSearch();
    }

    private static void runSampleSearch() {
        int result = binarySearch(sampleNumbers(), SAMPLE_TARGET);
        printSearchResult(result);
    }

    private static int[] sampleNumbers() {
        return new int[] {2, 3, 4, 10, 40};
    }
}
