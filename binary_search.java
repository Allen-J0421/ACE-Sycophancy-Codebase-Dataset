final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] SAMPLE_VALUES = {2, 3, 4, 10, 40};
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] values, int target) {
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);

            if (values[mid] == target) {
                return mid;
            }

            if (values[mid] < target) {
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
        if (index == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + index;
    }

    public static void main(String[] args) {
        int result = binarySearch(SAMPLE_VALUES, SAMPLE_TARGET);

        System.out.println(formatSearchResult(result));
    }
}
