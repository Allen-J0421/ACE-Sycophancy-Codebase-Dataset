final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        int lowerBound = 0;
        int upperBound = lastIndex(sortedValues);

        while (lowerBound <= upperBound) {
            int mid = midpoint(lowerBound, upperBound);
            int currentValue = sortedValues[mid];

            if (currentValue == target) {
                return mid;
            }

            if (currentValue < target) {
                lowerBound = mid + 1;
            } else {
                upperBound = mid - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int lowerBound, int upperBound) {
        return lowerBound + (upperBound - lowerBound) / 2;
    }

    private static int lastIndex(int[] values) {
        return values.length - 1;
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
        if (isNotFound(result)) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + result;
    }

    private static boolean isNotFound(int result) {
        return result == NOT_FOUND;
    }
}
