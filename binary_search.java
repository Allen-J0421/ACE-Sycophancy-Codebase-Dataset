final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int[] DEMO_ARRAY = { 2, 3, 4, 10, 40 };
    private static final int DEMO_TARGET = 10;

    private BinarySearch() {
    }

    static int binarySearch(int[] array, int target) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (array[mid] == target) {
                return mid;
            }

            if (array[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return "Element is not present in array";
        }

        return "Element is present at index " + result;
    }

    private static void printSearchResult(int result) {
        System.out.println(formatSearchResult(result));
    }

    public static void main(String[] args) {
        int result = binarySearch(DEMO_ARRAY, DEMO_TARGET);
        printSearchResult(result);
    }
}
