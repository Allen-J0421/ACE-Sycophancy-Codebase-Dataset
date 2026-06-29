final class BinarySearch {
    private static final int NOT_FOUND = -1;
    private static final int DEMO_TARGET = 10;
    private static final String NOT_FOUND_MESSAGE = "Element is not present in array";
    private static final String FOUND_MESSAGE_PREFIX = "Element is present at index ";

    private BinarySearch() {
    }

    static int binarySearch(int[] sortedValues, int target) {
        int lowIndex = 0;
        int highIndex = sortedValues.length - 1;

        while (lowIndex <= highIndex) {
            int midIndex = midpoint(lowIndex, highIndex);
            int comparison = compareAtIndex(sortedValues, midIndex, target);

            if (comparison == 0) {
                return midIndex;
            }

            if (comparison < 0) {
                lowIndex = midIndex + 1;
            } else {
                highIndex = midIndex - 1;
            }
        }

        return NOT_FOUND;
    }

    private static int midpoint(int lowIndex, int highIndex) {
        return lowIndex + (highIndex - lowIndex) / 2;
    }

    private static int compareAtIndex(int[] sortedValues, int index, int target) {
        return Integer.compare(sortedValues[index], target);
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

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        int result = binarySearch(demoSortedValues(), DEMO_TARGET);

        System.out.println(formatSearchResult(result));
    }

    private static int[] demoSortedValues() {
        return new int[] { 2, 3, 4, 10, 40 };
    }
}
