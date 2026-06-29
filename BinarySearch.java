public class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int SAMPLE_TARGET = 10;

    private BinarySearch() {
    }

    public static int binarySearch(int[] values, int target) {
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

    private static void printSearchResult(int index) {
        System.out.println(formatSearchResult(index));
    }

    private static String formatSearchResult(int index) {
        return index == NOT_FOUND
                ? "Element is not present in array"
                : "Element is present at index " + index;
    }

    private static int[] sampleValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    public static void main(String[] args) {
        int result = binarySearch(sampleValues(), SAMPLE_TARGET);

        printSearchResult(result);
    }
}
