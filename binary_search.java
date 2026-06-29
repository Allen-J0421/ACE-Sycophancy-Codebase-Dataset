class BinarySearch {
    private static final int DEMO_TARGET = 10;
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final int NOT_FOUND = -1;

    static int binarySearch(int[] sortedValues, int target) {
        int low = 0;
        int high = sortedValues.length - 1;

        while (low <= high) {
            int middle = midpoint(low, high);
            int candidate = sortedValues[middle];

            if (candidate == target) {
                return middle;
            }

            if (candidate < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return NOT_FOUND;
    }

    public static void main(String[] args) {
        printSearchResult(demoValues(), DEMO_TARGET);
    }

    private static int midpoint(int low, int high) {
        return low + (high - low) / 2;
    }

    private static int[] demoValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }

    private static void printSearchResult(int[] sortedValues, int target) {
        int index = binarySearch(sortedValues, target);

        System.out.println(formatSearchResult(index));
    }

    private static String formatSearchResult(int index) {
        if (index == NOT_FOUND) {
            return NOT_FOUND_MESSAGE;
        }

        return FOUND_MESSAGE_PREFIX + index;
    }
}
