final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int mid = midpoint(low, high);
            int currentValue = sortedValues[mid];

            if (currentValue == target) {
                return mid;
            }

            if (currentValue < target) {
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
        System.out.println(demoSearchResult());
    }

    private static String demoSearchResult() {
        int result = binarySearch(demoValues(), DEMO_TARGET);
        return formatSearchResult(result);
    }

    private static int[] demoValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static String formatSearchResult(int result) {
        if (result == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }
}
