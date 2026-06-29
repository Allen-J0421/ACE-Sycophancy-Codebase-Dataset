import java.util.Objects;

final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        Objects.requireNonNull(sortedValues, "sortedValues");

        int left = 0;
        int right = sortedValues.length - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            int value = sortedValues[middle];

            if (value == target) {
                return middle;
            }

            if (value < target) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        int result = binarySearch(SAMPLE_VALUES, SAMPLE_TARGET);
        System.out.println(formatResult(result));
    }

    private static String formatResult(int index) {
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }
}
