public final class BinarySearch {
    private static final int[] SAMPLE_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
        // Utility class.
    }

    public static int binarySearch(int[] array, int target) {
        if (array == null) {
            throw new IllegalArgumentException("array must not be null");
        }

        return binarySearch(array, target, 0, array.length - 1);
    }

    private static int binarySearch(int[] array, int target, int left, int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int value = array[mid];

            if (value == target) {
                return mid;
            }

            if (value < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        int result = binarySearch(SAMPLE_ARRAY, SAMPLE_TARGET);
        System.out.println(formatResult(result));
    }

    private static String formatResult(int index) {
        if (index == -1) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
