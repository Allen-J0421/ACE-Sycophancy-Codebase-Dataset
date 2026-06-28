final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = { 2, 3, 4, 10, 40 };
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        return indexOf(values, target);
    }

    static int indexOf(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int current = values[mid];

            if (current == target) {
                return mid;
            }

            if (current < target) {
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

    public static void main(String[] args) {
        int result = indexOf(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static String formatSearchResult(int result) {
        if (isNotFound(result)) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    private static boolean isNotFound(int result) {
        return result == NOT_FOUND;
    }
}
